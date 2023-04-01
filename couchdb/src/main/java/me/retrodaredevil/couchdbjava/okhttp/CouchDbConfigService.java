package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.response.SimpleStatus;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * @see <a href="ttps://docs.couchdb.org/en/stable/api/server/configuration.html#accessing-the-local-node-s-configuration ">docs.couchdb.org/en/stable/api/server/configuration.html#accessing-the-local-node-s-configuration</a>
 */
public interface CouchDbConfigService {
	@HEAD("./")
	Call<Void> checkExists();


	/**
	 * @see <a href="https://docs.couchdb.org/en/stable/api/server/configuration.html#node-node-name-config">docs.couchdb.org/en/stable/api/server/configuration.html#node-node-name-config</a>
	 */
	@GET("./")
	Call<JsonData> queryConfig();

	/**
	 * @see <a href="https://docs.couchdb.org/en/stable/api/server/configuration.html#node-node-name-config-section">docs.couchdb.org/en/stable/api/server/configuration.html#node-node-name-config-section</a>
	 */
	@GET("{section}")
	Call<JsonData> querySection(@Path("section") String section);

	@GET("{section}/{key}")
	Call<String> queryValue(@Path("section") String section, @Path("key") String key);

	@PUT("{section}/{key}")
	Call<String> put(@Path("section") String section, @Path("key") String key, @Body String value);

	@DELETE("{section}/{key}")
	Call<String> delete(@Path("section") String section, @Path("key") String key);

	/**
	 * @see <a href=https://docs.couchdb.org/en/stable/api/server/configuration.html#node-node-name-config-reload">docs.couchdb.org/en/stable/api/server/configuration.html#node-node-name-config-reload</a>
	 */
	@POST("_reload")
	Call<SimpleStatus> reload();

}
