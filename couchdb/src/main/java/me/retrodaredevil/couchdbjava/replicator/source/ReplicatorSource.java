package me.retrodaredevil.couchdbjava.replicator.source;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.TextNode;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Represents a replicator source/target
 */
@JsonDeserialize(using = ReplicatorSource.Deserializer.class)
public interface ReplicatorSource {
	URI getUrl();
	Map<String, String> getHeaders();

	/**
	 * @return The auth or {@link CouchDbAuth#createNoAuth()}
	 */
	@NotNull CouchDbAuth getAuth();

	class Deserializer extends JsonDeserializer<ReplicatorSource> {

		@Override
		public ReplicatorSource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			if (p.currentToken().isStructStart()) {
				return p.readValueAs(ObjectReplicatorSource.class);
			}
			TextNode node = p.readValueAs(TextNode.class);
			return StringReplicatorSource.fromString(node.asText());
		}
	}
}
