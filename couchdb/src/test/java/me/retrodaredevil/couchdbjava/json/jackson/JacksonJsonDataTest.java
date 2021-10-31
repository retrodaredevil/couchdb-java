package me.retrodaredevil.couchdbjava.json.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdbjava.json.JsonData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonJsonDataTest {

	@Test
	void test() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{\"test\": 43}";
		JacksonJsonData jsonData = (JacksonJsonData) mapper.readValue(jsonString, JsonData.class);
		ObjectNode objectNode = (ObjectNode) jsonData.getNode();
		assertEquals(43, objectNode.get("test").asInt());
	}
	@Test
	void testWithAbsent() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{}";
		mapper.readValue(jsonString, ExpectingNullWhenAbsent.class);
	}
	@Test
	void testWithNull() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{\"value\":null}";
		mapper.readValue(jsonString, ExpectingJsonNullWhenNull.class);
	}
	@Test
	void testWithEmptyString() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{\"value\":\"\"}";
		mapper.readValue(jsonString, ExpectingEmptyStringWhenEmptyString.class);
	}

	private static class ExpectingNullWhenAbsent {

		@JsonCreator
		public ExpectingNullWhenAbsent(@JsonProperty("value") JsonData data) {
			assertNull(data);
		}
	}
	private static class ExpectingJsonNullWhenNull {

		@JsonCreator
		public ExpectingJsonNullWhenNull(@JsonProperty("value") JsonData data) {
			assertEquals("null", data.getJson());
		}
	}
	private static class ExpectingEmptyStringWhenEmptyString {

		@JsonCreator
		public ExpectingEmptyStringWhenEmptyString(@JsonProperty("value") JsonData data) {
			assertEquals("\"\"", data.getJson());
		}
	}
}
