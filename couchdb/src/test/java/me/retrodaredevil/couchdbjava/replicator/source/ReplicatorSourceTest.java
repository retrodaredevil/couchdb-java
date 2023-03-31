package me.retrodaredevil.couchdbjava.replicator.source;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplicatorSourceTest {

	@Test
	void testStringReplicatorSource() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		StringReplicatorSource source = new StringReplicatorSource(URI.create("https://github.com"));
		JsonNode tree = mapper.valueToTree(source);
		assertTrue(tree.isTextual());
		assertEquals("https://github.com", tree.asText());

		ReplicatorSource deserializedSource = mapper.treeToValue(tree, ReplicatorSource.class);
		assertTrue(deserializedSource instanceof StringReplicatorSource);
		assertEquals(source, deserializedSource);
	}

	@Test
	void testObjectReplicatorSource() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectReplicatorSource source = new ObjectReplicatorSource(
				URI.create("https://github.com"),
				null,
				null
		);
		JsonNode tree = mapper.valueToTree(source);
		assertTrue(tree.isObject());
		assertEquals(1, tree.size()); // confirm that the null fields become undefined and are not serialized
		assertEquals("https://github.com", tree.get("url").asText());

		ReplicatorSource deserializedSource = mapper.treeToValue(tree, ReplicatorSource.class);
		assertTrue(deserializedSource instanceof ObjectReplicatorSource);
		assertEquals(source, deserializedSource);
	}
}
