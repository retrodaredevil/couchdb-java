package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.request.BulkGetRequest;
import me.retrodaredevil.couchdbjava.request.BulkPostRequest;
import me.retrodaredevil.couchdbjava.request.ViewQueryParams;
import me.retrodaredevil.couchdbjava.response.*;
import me.retrodaredevil.couchdbjava.security.DatabaseSecurity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	 * Puts the document into the database. If the JSON data contains a `_id` field, that will be used
	 * for the ID. Otherwise, a random ID will be generated.
	 *
	 * This does not support updating an existing document
	 */
	@POST("./")
	Call<DocumentResponse> postDocument(@Body RequestBody jsonRequestBody);

	/**
	 * Puts the document into the database with the given id.
	 * @param revision The revision of the existing document or null if this is a new document. This could also be null if you put the revision in the body.
	 */
	@PUT("{docid}")
	Call<DocumentResponse> putDocument(@Path(value = "docid", encoded = true) String docid, @Header("If-Match") String revision, @Body RequestBody jsonRequestBody);

	@DELETE("{docid}")
	Call<DocumentResponse> deleteDocument(@Path(value = "docid", encoded = true) String docid, @Header("If-Match") String revision);


	@HTTP(method = "COPY", path = "{docid}")
	Call<DocumentResponse> copyToDocument(@Path(value = "docid", encoded = true) String docid, @Header("Destination") String newDocumentId);

	@HTTP(method = "COPY", path = "{docid}")
	Call<DocumentResponse> copyFromRevisionToDocument(@Path(value = "docid", encoded = true) String docid, @Query("rev") String revision, @Header("Destination") String newDocumentId);




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
	Call<DocumentResponse> putAttachment(
			@Path(value = "docid", encoded = true) String docid,
			@Path(value = "attachment", encoded = true) String attachment,
			@Header("If-Match") String revision,
			@Body RequestBody body
	);
	@DELETE("{docid}/{attachment}")
	Call<DocumentResponse> deleteAttachment(
			@Path(value = "docid", encoded = true) String docid,
			@Path(value = "attachment", encoded = true) String attachment,
			@Header("If-Match") String revision,
			@Query("batch") String batch
	);

	/**
	 * https://docs.couchdb.org/en/stable/api/database/compact.html
	 * https://docs.couchdb.org/en/stable/maintenance/compaction.html#database-compaction
	 */
	@POST("_compact")
	Call<SimpleStatus> compact();


	// TODO active tasks: https://docs.couchdb.org/en/stable/api/server/common.html#api-server-active-tasks
}
