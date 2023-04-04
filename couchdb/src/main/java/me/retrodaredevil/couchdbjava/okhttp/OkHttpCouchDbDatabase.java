package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbShared;
import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.CouchDbUtil;
import me.retrodaredevil.couchdbjava.attachment.AcceptRange;
import me.retrodaredevil.couchdbjava.attachment.AttachmentData;
import me.retrodaredevil.couchdbjava.attachment.AttachmentGet;
import me.retrodaredevil.couchdbjava.attachment.AttachmentInfo;
import me.retrodaredevil.couchdbjava.attachment.ContentEncoding;
import me.retrodaredevil.couchdbjava.exception.CouchDbCodeException;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import me.retrodaredevil.couchdbjava.option.DatabaseCreationOption;
import me.retrodaredevil.couchdbjava.request.BulkGetRequest;
import me.retrodaredevil.couchdbjava.request.BulkPostRequest;
import me.retrodaredevil.couchdbjava.request.ViewQueryParams;
import me.retrodaredevil.couchdbjava.response.BulkDocumentResponse;
import me.retrodaredevil.couchdbjava.response.BulkGetResponse;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import me.retrodaredevil.couchdbjava.security.DatabaseSecurity;
import me.retrodaredevil.couchdbjava.tag.DocumentEntityTag;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static me.retrodaredevil.couchdbjava.CouchDbUtil.*;

public class OkHttpCouchDbDatabase implements CouchDbDatabase {
	private static final String DATABASE_REGEX = "^[a-z][a-z0-9_$()+/-]*$";
	private final String name;
	private final HttpUrl url;
	private final OkHttpCouchDbInstance instance;
	private final CouchDbDatabaseService service;

	private final OkHttpCouchDbShared rootShared;

	public OkHttpCouchDbDatabase(String name, HttpUrl url, OkHttpCouchDbInstance instance) {
		if (name.startsWith("_") ? !name.substring(1).matches(DATABASE_REGEX) : !name.matches(DATABASE_REGEX)) {
			throw new IllegalArgumentException("Invalid database name! name: " + name);
		}
		this.name = name;
		this.url = url;
		this.instance = instance;

		Retrofit retrofit = new Retrofit.Builder()
				.client(instance.getClient())
				.baseUrl(url)
				.addConverterFactory(JacksonConverterFactory.create())
				.addConverterFactory(ScalarsConverterFactory.create())
				.build()
				;
//		System.out.println(retrofit.baseUrl());
		service = retrofit.create(CouchDbDatabaseService.class);

		rootShared = new OkHttpCouchDbShared("");
	}
	@Deprecated
	private HttpUrl.Builder createUrlBuilder() {
		return instance.createUrlBuilder().addPathSegment(name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public CouchDbShared getPartition(String partitionName) {
		return new OkHttpCouchDbShared("_partition/" + partitionName + "/");
	}

	@Override
	public boolean exists() throws CouchDbException {
		instance.preAuthorize();
		retrofit2.Response<Void> response = instance.executeCall(service.checkExists());
		if (response.isSuccessful()) {
			return true;
		}
		if (response.code() == CouchDbStatusCode.NOT_FOUND) {
			return false;
		}
		throw OkHttpUtil.createExceptionFromResponse(response);
	}

	@Override
	public void create(DatabaseCreationOption databaseCreationOption) throws CouchDbException {
		instance.preAuthorize();
		Map<String, Object> map = new HashMap<>();
		if (databaseCreationOption.getShards() != null) {
			map.put("q", databaseCreationOption.getShards());
		}
		if (databaseCreationOption.getReplicas() != null) {
			map.put("n", databaseCreationOption.getReplicas());
		}
		if (databaseCreationOption.getPartitioned() != null) {
			map.put("partitioned", databaseCreationOption.getPartitioned());
		}
		instance.executeAndHandle(service.createDatabase(map));
	}

	@Override
	public boolean createIfNotExists(DatabaseCreationOption databaseCreationOption) throws CouchDbException {
		try {
			create(databaseCreationOption);
			return true;
		} catch (CouchDbCodeException exception) {
			if (exception.getCode() == CouchDbStatusCode.PRECONDITION_FAILED) {
				return false;
			}
			throw exception;
		}
	}

	@Override
	public void deleteDatabase() throws CouchDbException {
		instance.preAuthorize();
		instance.executeAndHandle(service.deleteDatabase());
	}

	@Override
	public DatabaseInfo getDatabaseInfo() throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.getInfo());
	}

	private static DocumentResponse transformDocumentResponse(retrofit2.Response<DocumentResponse.Body> response) {
		String rawETag = response.headers().get("ETag");
		DocumentEntityTag eTag = DocumentEntityTag.parseETag(requireNonNull(rawETag, "ETag not present on response!"));
		if (!response.isSuccessful()) {
			throw new AssertionError("You should not be using this method if you did not already check whether this was a successful response!");
		}
		return DocumentResponse.create(
				requireNonNull(response.body(), "Response was successful, so body should not be null!"),
				eTag
		);
	}

	@Override
	public DocumentResponse postNewDocument(JsonData jsonData) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.postDocument(OkHttpUtil.createJsonRequestBody(jsonData)), response -> {
			if (!response.isSuccessful()) {
				throw new AssertionError("You should not be using this method if you did not already check whether this was a successful response!");
			}
			DocumentResponse.Body body = requireNonNull(response.body(), "Response was successful");
			// We do not expect this response to have an ETag header
			// Also, this method does not work on PouchDB, so we can freely assume that using a DocumentEntityTag with isRevision() == true is safe.
			return DocumentResponse.create(body, DocumentEntityTag.fromRevision(body.getRev()));
		});
	}

	@Override
	public DocumentResponse putDocument(String id, JsonData jsonData) throws CouchDbException {
		return updateDocument(id, null, jsonData, false);
	}

	@Override
	public DocumentResponse updateDocument(String id, DocumentEntityTag eTag, JsonData jsonData, boolean forceETagUse) throws CouchDbException {
		requireNonNull(id);
		instance.preAuthorize();
		if (eTag != null) {
			if (eTag.isWeak() || forceETagUse) {
				return instance.executeAndHandle(service.putDocumentWithIfMatch(encodeDocumentId(id), eTag.getRawValue(), OkHttpUtil.createJsonRequestBody(jsonData)), OkHttpCouchDbDatabase::transformDocumentResponse);
			}
			// for revision ETags we prefer the rev query parameter because PouchDB will always be OK with that
			return instance.executeAndHandle(service.putDocumentWithRevQuery(encodeDocumentId(id), eTag.getValue(), OkHttpUtil.createJsonRequestBody(jsonData)), OkHttpCouchDbDatabase::transformDocumentResponse);
		}
		// while this method name is updateDocument, we actually also have logic for putDocument here
		// using WithIfMatch or WithRevQuery does not matter here because both are identical when not including a revision/ETag
		return instance.executeAndHandle(service.putDocumentWithIfMatch(encodeDocumentId(id), null, OkHttpUtil.createJsonRequestBody(jsonData)), OkHttpCouchDbDatabase::transformDocumentResponse);
	}

	@Override
	public DocumentResponse deleteDocument(String id, String revision) throws CouchDbException {
		requireNonNull(id);
		requireNonNull(revision);
		instance.preAuthorize();
		return instance.executeAndHandle(service.deleteDocument(encodeDocumentId(id), encodeRevisionForHeader(revision)), OkHttpCouchDbDatabase::transformDocumentResponse);
	}

	@Override
	public DocumentData getDocument(String id) throws CouchDbException {
		return getDocumentIfUpdated(id, (DocumentEntityTag) null);
	}

	@Override
	public DocumentData getDocumentIfUpdated(String id, String revision) throws CouchDbException {
		return getDocumentIfUpdated(id, DocumentEntityTag.fromRevision(revision));
	}

	@Override
	public DocumentData getDocumentIfUpdated(String id, @Nullable DocumentEntityTag eTag) throws CouchDbException {
		// https://docs.couchdb.org/en/stable/api/document/common.html#get--db-docid
		instance.preAuthorize();
		Request.Builder builder = new Request.Builder()
				.get()
				.url(url.newBuilder().addEncodedPathSegments(encodeDocumentId(id)).build());
		if (eTag != null) {
			// On PouchDB this only works if DocumentEntityTag#isWeak() == true
			// There is no workaround for that as there is no alternate query parameter that we can use
			builder.header("If-None-Match", eTag.getRawValue());
		}
		Response response = instance.executeCall(instance.getClient().newCall(builder.build()));
		if (response.isSuccessful()) {
			String json;
			try {
				json = requireNonNull(response.body()).string();
			} catch (IOException e) {
				throw new CouchDbException("Couldn't read response!", e);
			}
			JsonData jsonData = new StringJsonData(json);
			final DocumentEntityTag responseETag;
			try {
				responseETag = DocumentEntityTag.fromDocumentResponse(response);
			} catch (IllegalArgumentException e) {
				throw new CouchDbException("Bad response!", e);
			}
			final String responseRevision;
			if (responseETag.isRevision()) {
				responseRevision = responseETag.getValue(); // DocumentEntityTag has already performed validation
			} else { // This else only happens on PouchDB servers
				try {
					responseRevision = CouchDbUtil.revisionFromJson(jsonData);
				} catch (IllegalArgumentException e) {
					throw new CouchDbException("Bad response!", e);
				}
			}
			return new DocumentData(responseRevision, jsonData, responseETag);
		}
		throw OkHttpUtil.createExceptionFromResponse(response);

	}

	@Override
	public String getCurrentRevision(String id) throws CouchDbException {
		instance.preAuthorize();
		Response response = instance.executeCall(instance.getClient().newCall(
				new Request.Builder()
						.head()
						.url(url.newBuilder().addEncodedPathSegments(encodeDocumentId(id)).build())
						.build()
		));
		if (response.isSuccessful()) {
			return getRevision(response);
		}
		throw OkHttpUtil.createExceptionFromResponse(response);
	}
	@Deprecated
	private String getRevision(Response response) throws CouchDbException {
		// TODO ETag value in PouchDB is different than CouchDB.
		//   We should consider a workaround
		String revision = response.header("ETag");
		if (revision == null) {
			throw new CouchDbException("ETag header was not present!");
		}
		if (revision.length() < 36) { // minimum length of a revision is 34, plus two for the two double quotes
			throw new CouchDbException("Revision length is too small! revision: " + revision);
		}
		return CouchDbUtil.trimDoubleQuotes(revision); // trim off the double quotes
	}

	@Override
	public DocumentResponse copyToNewDocument(String id, String newDocumentId) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.copyToDocument(encodeDocumentId(id), newDocumentId), OkHttpCouchDbDatabase::transformDocumentResponse);
	}

	@Override
	public DocumentResponse copyFromRevisionToNewDocument(String id, String revision, String newDocumentId) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.copyFromRevisionToDocument(encodeDocumentId(id), revision, newDocumentId), OkHttpCouchDbDatabase::transformDocumentResponse);
	}

	@Override
	public DocumentResponse copyToExistingDocument(String id, String targetDocumentId, String targetDocumentRevision) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.copyToDocument(encodeDocumentId(id), targetDocumentId + "?rev=" + targetDocumentRevision), OkHttpCouchDbDatabase::transformDocumentResponse);
	}

	@Override
	public DocumentResponse copyFromRevisionToExistingDocument(String id, String revision, String targetDocumentId, String targetDocumentRevision) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.copyFromRevisionToDocument(encodeDocumentId(id), revision, targetDocumentId + "?rev=" + targetDocumentRevision), OkHttpCouchDbDatabase::transformDocumentResponse);
	}

	@Override
	public ViewResponse queryView(String designDoc, String viewName, ViewQueryParams viewQueryParams) throws CouchDbException {
		return rootShared.queryView(designDoc, viewName, viewQueryParams);
	}

	@Override
	public ViewResponse allDocs(ViewQueryParams viewQueryParams) throws CouchDbException {
		return rootShared.allDocs(viewQueryParams);
	}

	@Override
	public DatabaseSecurity getSecurity() throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.getSecurity());
	}

	@Override
	public void setSecurity(DatabaseSecurity databaseSecurity) throws CouchDbException {
		instance.preAuthorize();
		instance.executeAndHandle(service.putSecurity(databaseSecurity));
	}

	@Override
	public BulkGetResponse getDocumentsBulk(BulkGetRequest request) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.getDocumentsBulk(request));
	}

	@Override
	public List<BulkDocumentResponse> postDocumentsBulk(BulkPostRequest request) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.postDocumentsBulk(request));
	}

	// region attachment
	private AttachmentInfo parseAttachmentInfo(Response response) {
		if (!response.isSuccessful()) {
			throw new IllegalStateException("response must be successful to use this method!");
		}
		String acceptRangeHeaderValue = response.header("Accept-Ranges"); // on CouchDB this value is never null, but on PouchDB it is null
		AcceptRange acceptRange = acceptRangeHeaderValue == null ? AcceptRange.NONE : AcceptRange.createFromValue(acceptRangeHeaderValue);
//		AcceptRange acceptRange = AcceptRange.createFromValue(requireNonNull(response.header("Accept-Ranges"), "Accept-Ranges not present"));

		String contentEncodingStringOrNull = response.header("Content-Encoding");
		ContentEncoding contentEncoding = contentEncodingStringOrNull == null ? null : ContentEncoding.fromName(contentEncodingStringOrNull);
		int contentLength = Integer.parseInt(requireNonNull(response.header("Content-Length"), "Content-Length not present"));
		String base64EncodedDigest = CouchDbUtil.trimDoubleQuotes(requireNonNull(response.header("ETag"), "ETag not present"));
		return new AttachmentInfo(acceptRange, contentEncoding, contentLength, base64EncodedDigest);
	}

	private Response doAttachmentRequest(AttachmentGet attachmentGet, boolean head) throws CouchDbException {
		String documentId = attachmentGet.getDocumentId();
		String attachmentName = attachmentGet.getAttachmentName();
		String attachmentDigest = attachmentGet.getAttachmentDigest();
		String documentRevision = attachmentGet.getDocumentRevision();
		instance.preAuthorize();
		final HttpUrl requestUrl;
		{
			HttpUrl.Builder urlBuilder = url.newBuilder()
					.addEncodedPathSegments(encodeDocumentId(documentId))
					.addEncodedPathSegments(encodeAttachmentName(attachmentName));
			if (documentRevision != null) {
				urlBuilder.addQueryParameter("rev", documentRevision);
			}
			requestUrl = urlBuilder.build();
		}
		final Request request;
		{
			Request.Builder builder = new Request.Builder()
					.url(requestUrl);
			if (head) {
				// https://docs.couchdb.org/en/stable/api/document/attachments.html#head--db-docid-attname
				builder.head();
			}
			// if not head: https://docs.couchdb.org/en/stable/api/document/attachments.html#get--db-docid-attname
			if (attachmentDigest != null) {
				builder.header("If-None-Match", attachmentDigest);
			}
			// We used to add the If-Match header here, but we instead use the rev query parameter, which PouchDB supports
			// TODO PouchDB may allow a weak validator
			request = builder.build();
		}
		return instance.executeCall(instance.getClient().newCall(request));
	}

	@Override
	public @NotNull AttachmentInfo getAttachmentInfo(@NotNull AttachmentGet attachmentGet) throws CouchDbException {
		Response response = doAttachmentRequest(attachmentGet, true);
		if (response.isSuccessful()) {
			return parseAttachmentInfo(response);
		}
		throw OkHttpUtil.createExceptionFromResponse(response);
	}

	@Override
	public @NotNull AttachmentData getAttachment(@NotNull AttachmentGet attachmentGet) throws CouchDbException {
		Response response = doAttachmentRequest(attachmentGet, false);
		if (response.isSuccessful()) {
			AttachmentInfo info = parseAttachmentInfo(response);
			ResponseBody body = requireNonNull(response.body());
			String contentType = Objects.toString(body.contentType(), null);
			long contentLength = body.contentLength();
			if (contentLength < 0) {
				throw new AssertionError("The implementation should know how many bytes it gave us back!");
			}
			return new AttachmentData(info, contentType, contentLength, body.source());
		}
		throw OkHttpUtil.createExceptionFromResponse(response);

	}

	@Override
	public @NotNull DocumentResponse putAttachment(@NotNull String documentId, @NotNull String attachmentName, @NotNull Source dataSource, @Nullable String documentRevision, @Nullable String contentType) throws CouchDbException {
		requireNonNull(documentId);
		requireNonNull(attachmentName);
		requireNonNull(dataSource);
		instance.preAuthorize();
		MediaType mediaType = contentType == null ? null : MediaType.get(contentType);
		RequestBody body = new RequestBody() {
			@Nullable
			@Override
			public MediaType contentType() {
				return mediaType;
			}

			@Override
			public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
				bufferedSink.writeAll(dataSource);
			}

			@Override
			public boolean isOneShot() {
				// This is one shot because once writeTo is called, the Source is consumed.
				//   This is important especially while debugging because it guarantees that HttpLoggingInterceptor
				//   will not call writeTo() for debugging purposes.
				// TODO maybe base the return value of isOneShot on the type of dataSource
				//   (Maybe a buffered source doesn't have to be a one shot)
				return true;
			}
		};
		String revisionEncodedOrNull = documentRevision == null ? null : encodeRevisionForHeader(documentRevision);
		return instance.executeAndHandle(service.putAttachment(encodeDocumentId(documentId), encodeAttachmentName(attachmentName), revisionEncodedOrNull, body), OkHttpCouchDbDatabase::transformDocumentResponse);
	}

	@Override
	public @NotNull DocumentResponse deleteAttachment(@NotNull String documentId, @NotNull String attachmentName, @NotNull String documentRevision, boolean batch) throws CouchDbException {
		requireNonNull(documentId);
		requireNonNull(attachmentName);
		requireNonNull(documentRevision);
		instance.preAuthorize();
		String batchString = batch ? "ok" : null;
		return instance.executeAndHandle(service.deleteAttachment(documentId, attachmentName, documentRevision, batchString), OkHttpCouchDbDatabase::transformDocumentResponse);
	}

	// endregion

	private class OkHttpCouchDbShared implements CouchDbShared {
		private final String prefix;

		/**
		 * @param prefix The already encoded prefix
		 */
		private OkHttpCouchDbShared(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public ViewResponse queryView(String designDoc, String viewName, ViewQueryParams viewQueryParams) throws CouchDbException {
			requireNonNull(designDoc);
			requireNonNull(viewName);
			requireNonNull(viewQueryParams);
			designDoc = designDoc.replaceAll("_design/", ""); // Just in case the user added _design/ to this, let's make that valid
			instance.preAuthorize();
			return instance.executeAndHandle(service.queryView(prefix, encodeDocumentId(designDoc), viewName, viewQueryParams));
		}

		@Override
		public ViewResponse allDocs(ViewQueryParams viewQueryParams) throws CouchDbException {
			requireNonNull(viewQueryParams);
			instance.preAuthorize();
			return instance.executeAndHandle(service.queryView(prefix, viewQueryParams));
		}
	}
}
