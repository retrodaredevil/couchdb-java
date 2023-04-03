package me.retrodaredevil.couchdbjava.integration;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TestUtil {
	private TestUtil() { throw new UnsupportedOperationException(); }
	public static final CouchDbAuth AUTH = CouchDbAuth.create("admin", "password");

	public static CouchDbInstance createInstance(DatabaseService databaseService) {
		return createInstance(databaseService, false);
	}
	public static CouchDbInstance createDebugInstance(DatabaseService databaseService) {
		return createInstance(databaseService, true);
	}

	private static CouchDbInstance createInstance(DatabaseService databaseService, boolean debug) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		if (debug){
			builder.addInterceptor(new HttpLoggingInterceptor(System.out::println).setLevel(HttpLoggingInterceptor.Level.BODY));
		}
		return new OkHttpCouchDbInstance(
				builder.build(),
				new HttpUrl.Builder()
						.scheme("http")
						.host(databaseService.getHost())
						.port(databaseService.getPort())
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

	public static DocumentResponse postDocumentCompatibility(DatabaseService databaseService, CouchDbDatabase database, JsonData jsonData) throws CouchDbException {
		if (databaseService == DatabaseService.POUCHDB) {
			// PouchDB does not support POST /db/
			return database.putDocument(UUID.randomUUID().toString(), jsonData);
		}
		return database.postNewDocument(jsonData);
	}
}
