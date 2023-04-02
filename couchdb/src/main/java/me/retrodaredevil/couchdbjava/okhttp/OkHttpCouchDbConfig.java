package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbConfig;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static java.util.Objects.requireNonNull;

public class OkHttpCouchDbConfig implements CouchDbConfig {
	private final String name;
	private final OkHttpCouchDbInstance instance;
	private final CouchDbConfigService service;

	public OkHttpCouchDbConfig(String name, HttpUrl url, OkHttpCouchDbInstance instance) {
		this.name = requireNonNull(name); // it may be worth doing some validation on this in the future
		this.instance = requireNonNull(instance);


		Retrofit retrofit = new Retrofit.Builder()
				.client(instance.getClient())
				.baseUrl(url)
				.addConverterFactory(JacksonConverterFactory.create())
				.build()
				;
//		System.out.println(retrofit.baseUrl());
		service = retrofit.create(CouchDbConfigService.class);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void test() throws CouchDbException {
		instance.preAuthorize();
		instance.executeAndHandle(service.checkExists());
	}

	@Override
	public JsonData query() throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.queryConfig());
	}

	@Override
	public JsonData querySection(String section) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.querySection(section));
	}

	@Override
	public @NotNull String queryValue(String section, String key) throws CouchDbException {
		instance.preAuthorize();
		return instance.executeAndHandle(service.queryValue(section, key));
	}

	@Override
	public String putValue(String section, String key, String value) throws CouchDbException {
		instance.preAuthorize();
		if (value == null) {
			return instance.executeAndHandle(service.delete(section, key));
		}
		return instance.executeAndHandle(service.put(section, key, value));
	}

	@Override
	public void reload() throws CouchDbException {
		instance.preAuthorize();
		instance.executeAndHandle(service.reload());
	}
}
