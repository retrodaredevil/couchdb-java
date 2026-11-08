package me.retrodaredevil.couchdbjava.replicator;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.replicator.source.StringReplicatorSource;
import org.junit.jupiter.api.Test;
import org.jspecify.annotations.NullMarked;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class ReplicatorDocumentTest {

	@Test
	void test() {
		ReplicatorDocument document = SimpleReplicatorDocument.builder(
				StringReplicatorSource.fromString("https://localhost:5984/a"),
				StringReplicatorSource.fromString("https://localhost:5984/b")
		)
				.continuous()
				.build();
		assertTrue(document.isContinuous());
		assertFalse(document.isCreateTarget()); // we didn't specify this, so assert that it is false

		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree = mapper.valueToTree(document);
		assertTrue(tree.isObject());
		assertEquals(3, tree.size());
		assertEquals("https://localhost:5984/a", tree.get("source").asString());
		assertEquals("https://localhost:5984/b", tree.get("target").asString());
		assertTrue(tree.get("continuous").asBoolean());

		ReplicatorDocument deserializedDocument = mapper.treeToValue(tree, ReplicatorDocument.class);
		assertEquals(document, deserializedDocument);
	}
}
