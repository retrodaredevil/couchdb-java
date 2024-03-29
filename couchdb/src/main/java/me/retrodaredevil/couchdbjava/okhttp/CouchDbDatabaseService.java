package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.request.BulkGetRequest;
import me.retrodaredevil.couchdbjava.request.BulkPostRequest;
import me.retrodaredevil.couchdbjava.request.ViewQueryParams;
import me.retrodaredevil.couchdbjava.response.BulkDocumentResponse;
import me.retrodaredevil.couchdbjava.response.BulkGetResponse;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.SimpleStatus;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import me.retrodaredevil.couchdbjava.security.DatabaseSecurity;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface CouchDbDatabaseService {

	@HEAD("./")
	Call<Void> checkExists();

	/**
	 * https://docs.couchdb.org/en/stable/api/database/common.html#put--db
	 */
	@PUT("./")
	Call<SimpleStatus> createDatabase(@QueryMap Map<String, Object> queryMap);

	@GET("./")
	Call<DatabaseInfo> getInfo();

	@DELETE("./")
	Call<SimpleStatus> deleteDatabase();

	/**
	 * <a href="https://docs.couchdb.org/en/stable/api/database/common.html#post--db">docs.couchdb.org/en/stable/api/database/common.html#post--db</a>
	 * <p>
	 * Puts the document into the database. If the JSON data contains a `_id` field, that will be used
	 * for the ID. Otherwise, a random ID will be generated.
	 * <p>
	 * This does not support updating an existing document
	 * <p>
	 * This is not supported by PouchDB.
	 */
	@POST("./")
	Call<DocumentResponse.Body> postDocument(@Body RequestBody jsonRequestBody);

	/**
	 * <a href="https://docs.couchdb.org/en/stable/api/document/common.html#put--db-docid">docs.couchdb.org/en/stable/api/document/common.html#put--db-docid</a>
	 * <p>
	 * Puts the document into the database with the given id.
	 * <p>
	 * Note: Not necessarily compatible with PouchDB.
	 * @param eTagHeaderValue The {@link me.retrodaredevil.couchdbjava.tag.DocumentEntityTag#getRawValue()} of the existing document or null if this is a new document. This could also be null if you put the revision in the body.
	 */
	@PUT("{docid}")
	Call<DocumentResponse.Body> putDocumentWithIfMatch(@Path(value = "docid", encoded = true) String docid, @Header("If-Match") String eTagHeaderValue, @Body RequestBody jsonRequestBody);

	/**
	 * @see {@link #putDocumentWithIfMatch(String, String, RequestBody)}
	 */
	@PUT("{docid}")
	Call<DocumentResponse.Body> putDocumentWithRevQuery(@Path(value = "docid", encoded = true) String docid, @Query("rev") String revision, @Body RequestBody jsonRequestBody);

	@DELETE("{docid}")
	Call<DocumentResponse.Body> deleteDocument(@Path(value = "docid", encoded = true) String docid, @Header("If-Match") String revision); // TODO make If-Match compatible with PouchDB


	@HTTP(method = "COPY", path = "{docid}")
	Call<DocumentResponse.Body> copyToDocument(@Path(value = "docid", encoded = true) String docid, @Header("Destination") String newDocumentId);

	@HTTP(method = "COPY", path = "{docid}")
	Call<DocumentResponse.Body> copyFromRevisionToDocument(@Path(value = "docid", encoded = true) String docid, @Query("rev") String revision, @Header("Destination") String newDocumentId);




	@POST("{prefix}_design/{ddoc}/_view/{view}")
	Call<ViewResponse> queryView(@Path(value = "prefix", encoded = true) String prefix, @Path(value = "ddoc", encoded = true) String designDoc, @Path("view") String viewName, @Body ViewQueryParams viewQueryParams);

	@POST("{prefix}_all_docs")
	Call<ViewResponse> queryView(@Path(value = "prefix", encoded = true) String prefix, @Body ViewQueryParams viewQueryParams);

	@GET("_security")
	Call<DatabaseSecurity> getSecurity();

	@PUT("_security")
	Call<SimpleStatus> putSecurity(@Body DatabaseSecurity databaseSecurity);

	@POST("_bulk_get")
	Call<BulkGetResponse> getDocumentsBulk(@Body BulkGetRequest request);

	@POST("_bulk_docs")
	Call<List<BulkDocumentResponse>> postDocumentsBulk(@Body BulkPostRequest request);

	@PUT("{docid}/{attachment}")
	Call<DocumentResponse.Body> putAttachment(
			@Path(value = "docid", encoded = true) String docid,
			@Path(value = "attachment", encoded = true) String attachment,
			@Header("If-Match") String revision, // TODO change for PouchDB compatibility
			@Body RequestBody body
	);

	/**
	 * https://docs.couchdb.org/en/stable/api/document/attachments.html#delete--db-docid-attname
	 */
	@DELETE("{docid}/{attachment}")
	Call<DocumentResponse.Body> deleteAttachment(
			@Path(value = "docid", encoded = true) String docid,
			@Path(value = "attachment", encoded = true) String attachment,
//			@Header("If-Match") String revision,
			@Query("rev") String revision, // CouchDB supports If-Match header here, but PouchDB does not
			@Query("batch") String batch
	);

	/**
	 * https://docs.couchdb.org/en/stable/api/database/compact.html
	 * https://docs.couchdb.org/en/stable/maintenance/compaction.html#database-compaction
	 */
	@Headers({"Content-Type: application/json"})
	@POST("_compact")
	Call<SimpleStatus> compact();

	@Headers({"Content-Type: application/json"})
	@POST("_compact/{design-doc}")
	Call<SimpleStatus> compactDesign(@Path("design-doc") String designDocument);

	@GET("_revs_limit")
	Call<Integer> getRevsLimit();
	@PUT("_revs_limit")
	Call<SimpleStatus> setRevsLimit(@Body int revsLimit);


	// TODO active tasks: https://docs.couchdb.org/en/stable/api/server/common.html#api-server-active-tasks
}
