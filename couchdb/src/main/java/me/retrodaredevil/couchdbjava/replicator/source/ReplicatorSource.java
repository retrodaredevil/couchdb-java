package me.retrodaredevil.couchdbjava.replicator.source;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.StringNode;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import org.jspecify.annotations.NullMarked;

import java.net.URI;
import java.util.Map;

/**
 * Represents a replicator source/target
 */
@JsonDeserialize(using = ReplicatorSource.Deserializer.class)
@NullMarked
public interface ReplicatorSource {
	URI getUrl();
	Map<String, String> getHeaders();

	/**
	 * @return The auth or {@link CouchDbAuth#createNoAuth()}
	 */
	CouchDbAuth getAuth();

	class Deserializer extends ValueDeserializer<ReplicatorSource> {

		@Override
		public ReplicatorSource deserialize(JsonParser p, DeserializationContext ctxt) {
			if (p.currentToken().isStructStart()) {
				return ctxt.readValue(p, ObjectReplicatorSource.class);
			}
			StringNode node = ctxt.readValue(p, StringNode.class);
			return StringReplicatorSource.fromString(node.asString());
		}
	}
}
