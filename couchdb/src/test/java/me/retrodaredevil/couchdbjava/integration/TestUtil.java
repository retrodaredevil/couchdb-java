package me.retrodaredevil.couchdbjava.integration;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public final class TestUtil {
	private TestUtil() { throw new UnsupportedOperationException(); }

	public static CouchDbInstance createInstance() {
		return new OkHttpCouchDbInstance(
				new OkHttpClient.Builder()
						.build(),
				new HttpUrl.Builder()
						.scheme("http")
						.host("localhost")
						.port(5984)
						.build(),
				new BasicAuthHandler(CouchDbAuth.create("admin", "password"))
		);
	}
}
