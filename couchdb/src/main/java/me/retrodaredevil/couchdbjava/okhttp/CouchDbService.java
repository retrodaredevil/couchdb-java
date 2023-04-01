package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.MembershipResponse;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;
import me.retrodaredevil.couchdbjava.response.SimpleStatus;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;
import java.util.Map;

public interface CouchDbService {
	@GET("./")
	Call<CouchDbGetResponse> getInfo();

	@GET("_all_dbs/")
	Call<List<String>> getAllDatabaseNames();

	@POST("_dbs_info/")
	Call<List<DatabaseInfo>> getDatabaseInfos(@Body Map<String, List<String>> keysObjectMap);

	@GET("_membership")
	Call<MembershipResponse> membership();

	@GET("_session/")
	Call<SessionGetResponse> getSessionInfo();

	/** https://docs.couchdb.org/en/stable/api/server/common.html#up */
	@GET("_up")
	Call<SimpleStatus> getUp();

}
