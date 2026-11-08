package me.retrodaredevil.couchdbjava.replicator.source;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.jspecify.annotations.NullMarked;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
class ReplicatorSourceTest {

	@Test
	void testStringReplicatorSource() {
		ObjectMapper mapper = new ObjectMapper();
		StringReplicatorSource source = new StringReplicatorSource(URI.create("https://github.com"));
		JsonNode tree = mapper.valueToTree(source);
		assertTrue(tree.isString());
		assertEquals("https://github.com", tree.asString());

		ReplicatorSource deserializedSource = mapper.treeToValue(tree, ReplicatorSource.class);
		assertTrue(deserializedSource instanceof StringReplicatorSource);
		assertEquals(source, deserializedSource);
	}

	@Test
	void testObjectReplicatorSource() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectReplicatorSource source = new ObjectReplicatorSource(
				URI.create("https://github.com"),
				null,
				null
		);
		JsonNode tree = mapper.valueToTree(source);
		assertTrue(tree.isObject());
		assertEquals(1, tree.size()); // confirm that the null fields become undefined and are not serialized
		assertEquals("https://github.com", tree.get("url").asString());

		ReplicatorSource deserializedSource = mapper.treeToValue(tree, ReplicatorSource.class);
		assertTrue(deserializedSource instanceof ObjectReplicatorSource);
		assertEquals(source, deserializedSource);
	}
}
