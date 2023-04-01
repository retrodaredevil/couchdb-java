package me.retrodaredevil.couchdbjava.okhttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import me.retrodaredevil.couchdbjava.CouchDbConfig;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.CouchDbNode;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.auth.OkHttpAuthHandler;
import me.retrodaredevil.couchdbjava.okhttp.util.OkHttpUtil;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.MembershipResponse;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class OkHttpCouchDbInstance implements CouchDbInstance {

	private final OkHttpClient client;
	private final HttpUrl url;
	private final OkHttpAuthHandler authHandler;

	private final CouchDbService service;

	private final OkHttpCouchDbDatabase replicatorDatabase;
	private final OkHttpCouchDbDatabase usersDatabase;

	public OkHttpCouchDbInstance(OkHttpClient client, HttpUrl url, OkHttpAuthHandler authHandler) {
		requireNonNull(this.url = url);
		requireNonNull(this.authHandler = authHandler);

		OkHttpClient.Builder builder = client.newBuilder();
		CookieJar cookieJar = authHandler.getCookieJar();
		if (cookieJar != null) {
			builder.cookieJar(cookieJar);
		}
		this.client = builder
				.addInterceptor(chain -> {
					Request.Builder requestBuilder = chain.request().newBuilder();
					authHandler.setAuthHeaders(OkHttpCouchDbInstance.this, requestBuilder);
					return chain.proceed(requestBuilder.build());
				})
//				.addInterceptor(new HeaderRequestInterceptor("Accept-Encoding", "deflate, gzip")) CouchDB doesn't support gzip by default, so we'll leave this out
				.build();

		Retrofit retrofit = new Retrofit.Builder()
				.client(this.client)
				.baseUrl(url)
				.addConverterFactory(JacksonConverterFactory.create())
				.addConverterFactory(ScalarsConverterFactory.create())
				.build()
				;
		service = retrofit.create(CouchDbService.class);

		replicatorDatabase = getDatabase("_replicator");
		usersDatabase = getDatabase("_users");
	}
	/*
	This createUrlBuilder... code was written in July 2021,
	and I don't remember why we need a "no query" variant or what query parameters
	would even be doing in the base URL.
	If we figure out why in the future, we should add a comment here explaining why to use one or the other

	I just changed a bunch of code to not use this. I will commit these now deprecated methods, so anyone reading this can feel free to remove these deprecated methods
	 */
	@Deprecated
	public HttpUrl.Builder createUrlBuilderNoQuery() {
		return new HttpUrl.Builder()
				.scheme(url.scheme())
				.host(url.host())
				.port(url.port())
				.encodedPath(url.encodedPath()) // this is necessary in case the root of the database is something like https://example.com/couchdb/
				;
	}
	@Deprecated
	public HttpUrl.Builder createUrlBuilder() {
		return new HttpUrl.Builder()
				.scheme(url.scheme())
				.host(url.host())
				.port(url.port())
				.encodedPath(url.encodedPath())
				.query(url.query())
				;
	}
	public HttpUrl getUrl() {
		return url;
	}
	public OkHttpClient getClient() {
		return client;
	}

	/**
	 * Simple helper method for executing a call to avoid try-cache boilerplate
	 */
	public Response executeCall(Call call) throws CouchDbException {
		try {
			return call.execute();
		} catch (IOException e) {
			throw new CouchDbException(e);
		}
	}
	public <T> T executeAndHandle(retrofit2.Call<T> call) throws CouchDbException {
		retrofit2.Response<T> response = executeCall(call);
		if (response.isSuccessful()) {
			return response.body();
		}

		throw OkHttpUtil.createExceptionFromResponse(response);
	}
	public <T> retrofit2.Response<T> executeCall(retrofit2.Call<T> call) throws CouchDbException {
		try {
			return call.execute();
		} catch (ValueInstantiationException e) {
			// This exception occurs when some RuntimeException is thrown upon the instantiation of an object.
			//   The JSON is valid, but whatever creator was called deemed something invalid.
			throw new CouchDbException("Internal deserialization error!", e);
		} catch (JsonProcessingException e) {
			// Catch most Jackson related errors, and report "JSON processing error"
			throw new CouchDbException("Internal JSON processing error!", e);
		} catch (IOException e) {
			// Catch any other exception. Most likely going to be a connection failure if it wasn't a Jackson exception
			throw new CouchDbException("Connection failed!", e);
		}
	}

	@Override
	public OkHttpCouchDbDatabase getDatabase(String pathPrefix, String databaseName) {
		HttpUrl databaseUrl = url.newBuilder()
				.addEncodedPathSegments(pathPrefix)
				.addPathSegment(databaseName)
				.addEncodedPathSegments("") // this is necessary to make sure there is a trailing /
				.build();
		return new OkHttpCouchDbDatabase(databaseName, databaseUrl, this);
	}
	@Override
	public OkHttpCouchDbDatabase getDatabase(String name) {
		return getDatabase("", name);
	}

	@Override
	public CouchDbConfig getConfig(String pathPrefix, String name) {
		HttpUrl nodeUrl = url.newBuilder()
				.addEncodedPathSegments(pathPrefix) // assume path prefix is already encoded
				.addPathSegment(name)
				.addEncodedPathSegments("") // trailing /
				.build();
		return new OkHttpCouchDbConfig(name, nodeUrl, this);
	}

	@Override
	public CouchDbNode getNodeByName(String name) {
		HttpUrl nodeUrl = url.newBuilder()
				.addPathSegment("_node")
				.addPathSegment(name)
				.addEncodedPathSegments("") // trailing /
				.build();
		return new OkHttpCouchDbNode(name, nodeUrl, this);
	}


	@Override
	public CouchDbDatabase getReplicatorDatabase() {
		return replicatorDatabase;
	}

	@Override
	public CouchDbDatabase getUsersDatabase() {
		return usersDatabase;
	}

	@Override
	public SessionGetResponse getSessionInfo() throws CouchDbException {
		preAuthorize();
		return executeAndHandle(service.getSessionInfo());
	}

	@Override
	public CouchDbGetResponse getInfo() throws CouchDbException {
		preAuthorize();
		return executeAndHandle(service.getInfo());
	}

	@Override
	public List<String> getAllDatabaseNames() throws CouchDbException {
		preAuthorize();
		return executeAndHandle(service.getAllDatabaseNames());
	}

	@Override
	public List<DatabaseInfo> getDatabaseInfos(List<String> databaseNames) throws CouchDbException {
		preAuthorize();
		Map<String, List<String>> map = new HashMap<>();
		map.put("keys", databaseNames);
		return executeAndHandle(service.getDatabaseInfos(map));
	}

	@Override
	public MembershipResponse membership() throws CouchDbException {
		preAuthorize();
		return executeAndHandle(service.membership());
	}

	void preAuthorize() throws CouchDbException {
		authHandler.preAuthorize(this);
	}
}
