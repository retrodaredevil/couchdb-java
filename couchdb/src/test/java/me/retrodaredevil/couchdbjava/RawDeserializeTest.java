package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.couchdbjava.json.jackson.JacksonJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.RawDeserializer;
import org.junit.jupiter.api.Test;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public class RawDeserializeTest {
	@Test
	void testRaw() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String nestedJson = "{\"a\":5}";
		String json = "{\"value\": " + nestedJson + "}";
		SimpleObject object = mapper.readValue(json, SimpleObject.class);
		assertEquals(nestedJson, object.rawValue);
	}
	@Test
	void testRawArray() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String nestedJson = "{\"a\":5}";
		String json = "{\"value\":[" + nestedJson + "]}";
		SimpleArrayObject object = mapper.readValue(json, SimpleArrayObject.class);
		List<JacksonJsonData> rawValues = java.util.Objects.requireNonNull(object.rawValues);
		assertEquals(1, rawValues.size());
		assertEquals(nestedJson, rawValues.get(0).getJson());

	}
	private static class SimpleObject {
		@JsonRawValue
		@JsonDeserialize(using = RawDeserializer.class)
		@JsonProperty("value")
		private @Nullable String rawValue;
	}
	private static class SimpleArrayObject {
		@JsonProperty("value")
		private @Nullable List<JacksonJsonData> rawValues;
	}
}
