package me.retrodaredevil.couchdbjava.integration;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.OkHttpCouchDbInstance;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

@Tag(TestConstants.INTEGRATION_TEST)
public class DatabaseCreateTest {
	@Test
	void test() {
		System.out.println(System.getProperty("dockerCompose.servicesInfos"));
		CouchDbInstance instance =  new OkHttpCouchDbInstance(
				new OkHttpClient.Builder()
						.build(),
				new HttpUrl.Builder()
						.scheme("http")
						.host("localhost")
						.port(5984)
						.build(),
				new BasicAuthHandler(CouchDbAuth.create("admin", "password"))
		);
		try {
			instance.getAllDatabaseNames();
		} catch (CouchDbException e) {
			fail(e);
		}
	}
}
