package me.retrodaredevil.couchdbjava.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.NullNode;
import me.retrodaredevil.couchdbjava.json.JsonData;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@JsonDeserialize(using = JacksonJsonData.Deserializer.class)
@JsonSerialize(using = JacksonJsonData.Serializer.class)
public class JacksonJsonData implements JsonData {
	public static final JacksonJsonData JSON_NULL_DATA = new JacksonJsonData(NullNode.getInstance());
	private final JsonNode node;

	public JacksonJsonData(JsonNode node) {
		requireNonNull(this.node = node);
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

	public static class Deserializer extends JsonDeserializer<JacksonJsonData> {
		@Override
		public JacksonJsonData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode node = p.readValueAs(JsonNode.class);
			return new JacksonJsonData(node);
		}

		@Override
		public Object getAbsentValue(DeserializationContext ctxt) throws JsonMappingException {
			// Note that this method is not available for Jackson versions below 2.13, so using 2.13 is the only thing that makes the deserialization of this work as expected
			return null;
		}

		@Override
		public JacksonJsonData getNullValue(DeserializationContext ctxt) throws JsonMappingException {
			return JSON_NULL_DATA;
		}
	}
	public static class Serializer extends JsonSerializer<JacksonJsonData> {
		// NOTE: This does not seem to work (at least when used as `@Body JsonData data` in a retrofit service) TODO figure out why
		@Override
		public void serialize(JacksonJsonData value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeTree(value.node);
		}
	}
}
