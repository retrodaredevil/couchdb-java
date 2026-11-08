package me.retrodaredevil.couchdbjava.json.jackson;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.json.JsonData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CouchDbJacksonUtil {
	private CouchDbJacksonUtil() { throw new UnsupportedOperationException(); }

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static <T> T readValue(ObjectMapper objectMapper, JsonData jsonData, Class<T> clazz) {
		if (jsonData instanceof JacksonJsonData) {
			return objectMapper.treeToValue(((JacksonJsonData) jsonData).getNode(), clazz);
		}
		return objectMapper.readValue(jsonData.getJson(), clazz);
	}
	public static <T> T readValue(ObjectMapper objectMapper, JsonData jsonData, TypeReference<T> typeReference) {
		return objectMapper.readValue(jsonData.getJson(), typeReference);
	}
	public static JsonNode getNodeFrom(JsonData jsonData) {
		if (jsonData instanceof JacksonJsonData) {
			return ((JacksonJsonData) jsonData).getNode();
		}
		return MAPPER.readTree(jsonData.getJson());
	}
}
