package me.retrodaredevil.couchdbjava.integration;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.HashMap;
import java.util.Map;

public final class TestUtil {
	private TestUtil() { throw new UnsupportedOperationException(); }
	public static final CouchDbAuth AUTH = CouchDbAuth.create("admin", "password");

	public static CouchDbInstance createInstance() {
		return createInstance("couchdb", false);
	}
	public static CouchDbInstance createDebugInstance() {
		return createInstance("couchdb", true);
	}

	private static CouchDbInstance createInstance(String serviceName, boolean debug) {
		// The gradle compose plugin sets system properties for the services defined in the docker compose file:
		//   https://github.com/avast/gradle-docker-compose-plugin
		String portString = System.getProperty(serviceName + ".tcp.5984");
		String host = System.getProperty(serviceName + ".host");

		int port = Integer.parseInt(portString);
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		if (debug){
			builder.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY));
		}
		return new OkHttpCouchDbInstance(
				builder.build(),
				new HttpUrl.Builder()
						.scheme("http")
						.host(host)
						.port(port)
						.build(),
				new BasicAuthHandler(AUTH)
		);
	}
	public static Map<String, Object> createIdViewDesignDocument() {
		return createIdViewDesignDocument(true);
	}

	public static Map<String, Object> createIdViewDesignDocument(boolean partitioned) {
		Map<String, Object> idViewMap = new HashMap<>();
		idViewMap.put("map", "function(doc) {\n  emit(doc._id, null);\n}");
		Map<String, Object> viewsMap = new HashMap<>();
		viewsMap.put("id_view", idViewMap);
		Map<String, Object> designDocumentMap = new HashMap<>();
		designDocumentMap.put("language", "javascript");
		designDocumentMap.put("views", viewsMap);
		if (!partitioned) {
			// a design is, by default, partitioned
			Map<String, Object> optionsMap = new HashMap<>();
			optionsMap.put("partitioned", false);
			designDocumentMap.put("options", optionsMap);
		}
		return designDocumentMap;
	}

}
