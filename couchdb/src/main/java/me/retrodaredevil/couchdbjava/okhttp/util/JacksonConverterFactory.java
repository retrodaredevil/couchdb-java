package me.retrodaredevil.couchdbjava.okhttp.util;

import me.retrodaredevil.couchdbjava.json.JsonData;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jspecify.annotations.NullMarked;
import retrofit2.Converter;
import retrofit2.Retrofit;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Converts Retrofit JSON bodies using Jackson 3.
 * <p>
 * This custom converter was added because Retrofit's Jackson 3 support
 * <a href="https://github.com/lysine-dev/retrofit/issues/4674">issue #4674</a> is still open.
 * Related work: <a href="https://github.com/lysine-dev/retrofit/pull/4552">PR #4552: Add a Jackson 3 converter</a>.
 */
@NullMarked
public final class JacksonConverterFactory extends Converter.Factory {

	private final ObjectMapper mapper;

	private JacksonConverterFactory(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public static JacksonConverterFactory create() {
		return new JacksonConverterFactory(JsonMapper.builder().build());
	}

	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		ObjectReader reader = mapper.readerFor(mapper.constructType(type));
		return body -> {
			try (body) {
				return reader.readValue(body.byteStream());
			} catch (JacksonException e) {
				throw new IOException("Could not deserialize JSON response", e);
			}
		};
	}

	@Override
	public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
			Annotation[] methodAnnotations, Retrofit retrofit) {
		ObjectWriter writer = mapper.writerFor(mapper.constructType(type));
		return value -> {
			try {
				if (value instanceof JsonData jsonData) {
					return OkHttpUtil.createJsonRequestBody(jsonData);
				}
				return OkHttpUtil.createJsonRequestBody(writer.writeValueAsString(value));
			} catch (JacksonException e) {
				throw new IOException("Could not serialize JSON request", e);
			}
		};
	}
}
