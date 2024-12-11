package me.retrodaredevil.couchdbjava.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.integration.DatabaseService;
import me.retrodaredevil.couchdbjava.integration.TestUtil;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.replicator.ReplicatorDocument;
import me.retrodaredevil.couchdbjava.replicator.SimpleReplicatorDocument;
import me.retrodaredevil.couchdbjava.replicator.source.ObjectReplicatorSource;
import me.retrodaredevil.couchdbjava.replicator.source.ReplicatorSource;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class ReplicatorTest {
	private static final String SOURCE_DATABASE = "replicator_test_a";
	private static final String TARGET_DATABASE = "replicator_test_b";

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@ParameterizedTest
	@MethodSource("me.retrodaredevil.couchdbjava.integration.DatabaseService#values")
	void test(DatabaseService databaseService) throws CouchDbException, JsonProcessingException, InterruptedException {
		CouchDbInstance instance = TestUtil.createInstance(databaseService);
		CouchDbDatabase replicator = instance.getReplicatorDatabase();
		CouchDbDatabase source = instance.getDatabase(SOURCE_DATABASE);
		CouchDbDatabase target = instance.getDatabase(TARGET_DATABASE);
		source.create();
//		source.postNewDocument(new StringJsonData("{\"_id\": \"my_test_doc\", \"test\": 43}"));
		TestUtil.postDocumentCompatibility(databaseService, source, new StringJsonData("{\"_id\": \"my_test_doc\", \"test\": 43}"));

		// addAuthObject is only supported on CouchDB version >=3.2.0.
		//   We should encourage others to use this, but PouchDB cannot use it, so we stick with addAuthHeader
		ReplicatorSource sourceSource = ObjectReplicatorSource.builder(URI.create("http://localhost:5984/" + SOURCE_DATABASE))
				.addAuthHeader(TestUtil.AUTH)
				.build();
		ReplicatorSource targetSource = ObjectReplicatorSource.builder(URI.create("http://localhost:5984/" + TARGET_DATABASE))
				.addAuthHeader(TestUtil.AUTH)
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


		// NOTE: For CouchDB 3.3 and lower, we were able to get away with a timeout of about 7.5 seconds, but for CouchDB 3.4 we increased that
		for (int i = 0; i < 20 && !target.exists(); i++) { // 20 iterations is ~10 seconds maximum
			//noinspection BusyWait
			Thread.sleep(500);
		}
		assertTrue(target.exists());
		Thread.sleep(500);
		target.getDocument("my_test_doc"); // by calling this method, we assert this document now exists
	}
}
