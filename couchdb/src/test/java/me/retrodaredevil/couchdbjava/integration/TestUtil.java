package me.retrodaredevil.couchdbjava.integration;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public final class TestUtil {
	private TestUtil() { throw new UnsupportedOperationException(); }

	public static CouchDbInstance createInstance() {
		return createInstance(false);
	}
	public static CouchDbInstance createDebugInstance() {
		return createInstance(true);
	}

	private static CouchDbInstance createInstance(boolean debug) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		if (debug){
			builder.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY));
		}
		return new OkHttpCouchDbInstance(
				builder.build(),
				new HttpUrl.Builder()
						.scheme("http")
						.host("localhost")
						.port(5984)
						.build(),
				new BasicAuthHandler(CouchDbAuth.create("admin", "password"))
		);
	}
}
