package me.retrodaredevil.couchdbjava.json.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdbjava.json.JsonData;
import org.junit.jupiter.api.Test;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class JacksonJsonDataTest {

	@Test
	void test() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{\"test\": 43}";
		JacksonJsonData jsonData = (JacksonJsonData) mapper.readValue(jsonString, JsonData.class);
		ObjectNode objectNode = (ObjectNode) jsonData.getNode();
		assertEquals(43, objectNode.get("test").asInt());
	}
	@Test
	void testWithAbsent() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{}";
		mapper.readValue(jsonString, ExpectingNullWhenAbsent.class);
	}
	@Test
	void testWithNull() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{\"value\":null}";
		mapper.readValue(jsonString, ExpectingJsonNullWhenNull.class);
	}
	@Test
	void testWithEmptyString() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "{\"value\":\"\"}";
		mapper.readValue(jsonString, ExpectingEmptyStringWhenEmptyString.class);
	}

	private static class ExpectingNullWhenAbsent {

		@JsonCreator
		public ExpectingNullWhenAbsent(@JsonProperty("value") @Nullable JsonData data) {
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
