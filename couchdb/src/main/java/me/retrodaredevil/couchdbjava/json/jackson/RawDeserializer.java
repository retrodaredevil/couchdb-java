package me.retrodaredevil.couchdbjava.json.jackson;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.JsonNode;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RawDeserializer extends ValueDeserializer<String> {
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) {
		JsonNode node = ctxt.readValue(p, JsonNode.class);
		return node.toString();
	}
}
