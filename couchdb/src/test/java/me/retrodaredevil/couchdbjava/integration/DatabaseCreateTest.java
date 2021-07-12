package me.retrodaredevil.couchdbjava.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class DatabaseCreateTest {
	/*
	When running in IntelliJ, click the run button by the method you want to test, it will fail,
	then edit the configuration of what you just ran ("DatabaseCreateTest.test"), and in the tasks field,
	change ":couchdb:test" to ":couchdb:integration", then click the green run button by the edit configuration.

	Whenever you click the green run button by a method, it will reset what you just changed, so avoid that when possible.

	You also need to run this command unless you run as sudo:
	sudo usermod -aG docker $USER
	# Then log out and back in, I can't get this command below to work:
	exec su -l $USER
	 */
	@Test
	void test() throws CouchDbException, JsonProcessingException {
		CouchDbInstance instance = TestUtil.createInstance();
		assertFalse(instance.getAllDatabaseNames().contains("test_database"));
		CouchDbDatabase database = instance.getDatabase("test_database");
		database.create();

		DocumentResponse response = database.postNewDocument(new StringJsonData("{\"test\": 43}"));
		String id = response.getId();
		String expectedRev = response.getRev();

		DocumentData document = database.getDocument(id);
		assertEquals(expectedRev, document.getRevision());
		JsonData jsonData = document.getJsonData();
		JsonNode node = CouchDbJacksonUtil.getNodeFrom(jsonData);
		ObjectNode objectNode = (ObjectNode) node;
		@SuppressWarnings("NullableProblems")
		Set<String> fieldNames = StreamSupport.stream(((Iterable<String>) (objectNode::fieldNames)).spliterator(), false)
				.collect(Collectors.toSet());
		assertEquals(new HashSet<>(Arrays.asList("_id", "_rev", "test")), fieldNames);
		assertEquals(43, objectNode.get("test").numberValue().intValue());
		assertEquals(id, objectNode.get("_id").asText());
		assertEquals(expectedRev, objectNode.get("_rev").asText());
	}
}
