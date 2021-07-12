package me.retrodaredevil.couchdbjava.json.jackson;

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
}
