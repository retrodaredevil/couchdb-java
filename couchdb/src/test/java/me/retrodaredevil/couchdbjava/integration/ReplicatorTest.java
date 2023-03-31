package me.retrodaredevil.couchdbjava.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.replicator.ReplicatorDocument;
import me.retrodaredevil.couchdbjava.replicator.SimpleReplicatorDocument;
import me.retrodaredevil.couchdbjava.replicator.source.ObjectReplicatorSource;
import me.retrodaredevil.couchdbjava.replicator.source.ReplicatorSource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class ReplicatorTest {
	private static final String SOURCE_DATABASE = "replicator_test_a";
	private static final String TARGET_DATABASE = "replicator_test_b";

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Test
	void test() throws CouchDbException, JsonProcessingException, InterruptedException {
		CouchDbInstance instance = TestUtil.createInstance();
		CouchDbDatabase replicator = instance.getReplicatorDatabase();
		CouchDbDatabase source = instance.getDatabase(SOURCE_DATABASE);
		CouchDbDatabase target = instance.getDatabase(TARGET_DATABASE);
		source.create();
		source.postNewDocument(new StringJsonData("{\"_id\": \"my_test_doc\", \"test\": 43}"));

		ReplicatorSource sourceSource = ObjectReplicatorSource.builder(URI.create("http://localhost:5984/" + SOURCE_DATABASE))
				.addAuthObject(TestUtil.AUTH) // remember this is only supported on >=3.2.0. We probably should not encourage users to use this
				.build();
		ReplicatorSource targetSource = ObjectReplicatorSource.builder(URI.create("http://localhost:5984/" + TARGET_DATABASE))
				.addAuthObject(TestUtil.AUTH)
				.build();
		ReplicatorDocument replicatorDocument = SimpleReplicatorDocument.builder(sourceSource, targetSource)
				.createTarget()
				.build();
		assertFalse(target.exists()); // target database does not yet exist
		replicator.createIfNotExists(); // replicator database is usually not there by default
		replicator.putDocument("my_replication", new StringJsonData(MAPPER.writeValueAsString(replicatorDocument)));
		{
			// confirm that we can deserialize data directly retrieved from CouchDB
			JsonData jsonData = replicator.getDocument("my_replication").getJsonData();
			ReplicatorDocument deserializedDocument = CouchDbJacksonUtil.readValue(MAPPER, jsonData, ReplicatorDocument.class);
			assertTrue(deserializedDocument.getSource() instanceof ObjectReplicatorSource);
		}


		for (int i = 0; i < 15 && !target.exists(); i++) { // 15 iterations is ~7.5 seconds maximum
			//noinspection BusyWait
			Thread.sleep(500);
		}
		assertTrue(target.exists());
		Thread.sleep(500);
		target.getDocument("my_test_doc"); // by calling this method, we assert this document now exists
	}
}
