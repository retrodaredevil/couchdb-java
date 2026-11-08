package me.retrodaredevil.couchdbjava.json.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.json.JsonData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CouchDbJacksonUtil {
	private CouchDbJacksonUtil() { throw new UnsupportedOperationException(); }

	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * Deserializes the JSON data into the requested type.
	 *
	 * @throws JacksonException if the JSON data cannot be parsed or converted to the requested type
	 */
	public static <T> T readValue(ObjectMapper objectMapper, JsonData jsonData, Class<T> clazz) {
		if (jsonData instanceof JacksonJsonData jacksonJsonData) {
			return objectMapper.treeToValue(jacksonJsonData.getNode(), clazz);
		}
		return objectMapper.readValue(jsonData.getJson(), clazz);
	}
	/**
	 * Deserializes the JSON data into the requested type.
	 *
	 * @throws JacksonException if the JSON data cannot be parsed or converted to the requested type
	 */
	public static <T> T readValue(ObjectMapper objectMapper, JsonData jsonData, TypeReference<T> typeReference) {
		return objectMapper.readValue(jsonData.getJson(), typeReference);
	}
	/**
	 * Returns the JSON tree represented by the data.
	 *
	 * @throws JacksonException if the JSON data cannot be parsed
	 */
	public static JsonNode getNodeFrom(JsonData jsonData) {
		if (jsonData instanceof JacksonJsonData jacksonJsonData) {
			return jacksonJsonData.getNode();
		}
		return MAPPER.readTree(jsonData.getJson());
	}
}
