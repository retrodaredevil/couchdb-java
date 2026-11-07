package me.retrodaredevil.couchdbjava.okhttp;

import me.retrodaredevil.couchdbjava.CouchDbAuth;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.okhttp.auth.BasicAuthHandler;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
class OkHttpCouchDbInstanceTest {
	private final HttpUrl url = HttpUrl.get("http://localhost/");
	private final OkHttpCouchDbInstance instance = new OkHttpCouchDbInstance(
			new OkHttpClient(), url, new BasicAuthHandler(CouchDbAuth.createNoAuth()));

	@Test
	void missingRequiredBodyThrowsCouchDbException() {
		assertThrows(CouchDbException.class, () -> instance.executeAndHandle(call(204, "")));
	}

	@Test
	void emptyBodyAllowedWhenOnlyStatusIsNeeded() {
		assertDoesNotThrow(() -> instance.executeAndHandleWithoutBody(call(204, "")));
	}

	@Test
	void responseBodyIsReturned() throws Exception {
		try (ResponseBody body = instance.executeAndHandle(call(200, "hello"))) {
			assertEquals("hello", body.string());
		}
	}

	private Call<ResponseBody> call(int status, String body) {
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> new Response.Builder()
				.request(chain.request())
				.protocol(Protocol.HTTP_1_1)
				.code(status)
				.message("test")
				.body(ResponseBody.create(body, MediaType.get("text/plain")))
				.build()).build();
		return new Retrofit.Builder().baseUrl(url).client(client).build().create(TestService.class).get();
	}

	interface TestService {
		@GET("/")
		Call<ResponseBody> get();
	}
}
