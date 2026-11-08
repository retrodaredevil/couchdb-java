package me.retrodaredevil.couchdbjava.json.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.node.NullNode;
import me.retrodaredevil.couchdbjava.json.JsonData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(using = JacksonJsonData.Deserializer.class)
@JsonSerialize(using = JacksonJsonData.Serializer.class)
@NullMarked
public class JacksonJsonData implements JsonData {
	public static final JacksonJsonData JSON_NULL_DATA = new JacksonJsonData(NullNode.getInstance());
	private final JsonNode node;

	public JacksonJsonData(JsonNode node) {
		this.node = requireNonNull(node);
	}

	@Override
	public String getJson() {
		return node.toString();
	}
	public JsonNode getNode() {
		return node;
	}

	@Override
	public boolean isKnownToBeValid() {
		return true;
	}

	public static class Deserializer extends ValueDeserializer<JacksonJsonData> {
		@Override
		public JacksonJsonData deserialize(JsonParser p, DeserializationContext ctxt) {
			JsonNode node = ctxt.readValue(p, JsonNode.class);
			return new JacksonJsonData(node);
		}

		@Override
		public @Nullable Object getAbsentValue(DeserializationContext ctxt) {
			return null;
		}

		@Override
		public JacksonJsonData getNullValue(DeserializationContext ctxt) {
			return JSON_NULL_DATA;
		}
	}
	public static class Serializer extends ValueSerializer<JacksonJsonData> {
		@Override
		public void serialize(JacksonJsonData value, JsonGenerator gen, SerializationContext serializers) {
			serializers.writeTree(gen, value.node);
		}
	}
}
