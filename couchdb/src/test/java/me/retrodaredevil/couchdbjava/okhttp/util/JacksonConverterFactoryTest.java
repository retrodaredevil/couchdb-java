package me.retrodaredevil.couchdbjava.okhttp.util;

import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import retrofit2.Converter;
import retrofit2.Retrofit;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class JacksonConverterFactoryTest {
	private final Retrofit retrofit = new Retrofit.Builder()
			.baseUrl("http://localhost/")
			.addConverterFactory(JacksonConverterFactory.create())
			.build();
	private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

	@Test
	void readsGenericResponse() throws IOException {
		Converter<ResponseBody, List<Map<String, Integer>>> converter = retrofit.responseBodyConverter(
				new TypeReference<List<Map<String, Integer>>>() {}.getType(), NO_ANNOTATIONS);
		assertEquals(List.of(Map.of("value", 42)), converter.convert(
				ResponseBody.create("[{\"value\":42}]", MediaType.get("application/json"))));
	}

	@Test
	void malformedResponseIsAnIoException() {
		Converter<ResponseBody, Map<String, Object>> converter = retrofit.responseBodyConverter(Map.class, NO_ANNOTATIONS);
		assertThrows(IOException.class, () -> converter.convert(
				ResponseBody.create("{", MediaType.get("application/json"))));
	}

	@Test
	void writesJsonDataWithoutQuotingOrWrapping() throws IOException {
		Converter<JsonData, RequestBody> converter = retrofit.requestBodyConverter(
				JsonData.class, NO_ANNOTATIONS, NO_ANNOTATIONS);
		RequestBody body = converter.convert(new StringJsonData("{\"value\":42}"));
		assertNotNull(body);
		Buffer buffer = new Buffer();
		body.writeTo(buffer);
		assertEquals("{\"value\":42}", buffer.readUtf8());
	}
}
