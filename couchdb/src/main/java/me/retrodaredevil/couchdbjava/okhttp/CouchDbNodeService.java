package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface CouchDbNodeService {
	@GET("./")
	Call<CouchDbGetResponse> getInfo();

	@GET("_all_dbs/")
	Call<List<String>> getAllDatabaseNames();
}
