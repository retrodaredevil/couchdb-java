package me.retrodaredevil.couchdbjava.replicator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.replicator.source.StringReplicatorSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReplicatorDocumentTest {

	@Test
	void test() throws JsonProcessingException {
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
		assertEquals("https://localhost:5984/a", tree.get("source").asText());
		assertEquals("https://localhost:5984/b", tree.get("target").asText());
		assertTrue(tree.get("continuous").asBoolean());

		ReplicatorDocument deserializedDocument = mapper.treeToValue(tree, ReplicatorDocument.class);
		assertEquals(document, deserializedDocument);
	}
}
