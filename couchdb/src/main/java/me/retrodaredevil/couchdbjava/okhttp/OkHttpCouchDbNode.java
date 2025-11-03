package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbConfig;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbNode;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import okhttp3.HttpUrl;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class OkHttpCouchDbNode implements CouchDbNode {

	private final String name;
	private final OkHttpCouchDbInstance instance;
	private final HttpUrl url;
	private final CouchDbNodeService service;

	public OkHttpCouchDbNode(String name, HttpUrl url, OkHttpCouchDbInstance instance) {
		this.name = requireNonNull(name);
		this.instance = requireNonNull(instance);
		this.url = requireNonNull(url);

		Retrofit retrofit = new Retrofit.Builder()
				.client(instance.getClient())
				.baseUrl(url)
				.addConverterFactory(JacksonConverterFactory.create())
				.addConverterFactory(ScalarsConverterFactory.create())
				.build()
				;
//		System.out.println(retrofit.baseUrl());
		service = retrofit.create(CouchDbNodeService.class);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public CouchDbDatabase getDatabase(String pathPrefix, String databaseName) {
		HttpUrl databaseUrl = url.newBuilder().addEncodedPathSegments(pathPrefix).addPathSegment(databaseName).build();
		return new OkHttpCouchDbDatabase(databaseName, databaseUrl, instance);
	}

	// TODO this method does not work with a node name of _local
	@Override
	public CouchDbGetResponse getInfo() throws CouchDbException {
		return instance.executeAndHandle(service.getInfo());
	}

	@Override
	public List<String> getAllDatabaseNames() throws CouchDbException {
		return instance.executeAndHandle(service.getAllDatabaseNames());
	}

	@Override
	public CouchDbConfig getConfig(String pathPrefix, String name) {
		HttpUrl nodeUrl = url.newBuilder()
				.addPathSegments(pathPrefix)
				.addPathSegment(name)
				.addEncodedPathSegments("") // trailing /
				.build();
		return new OkHttpCouchDbConfig(name, nodeUrl, instance);
	}
}
