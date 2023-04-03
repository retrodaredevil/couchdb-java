package me.retrodaredevil.couchdbjava.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.integration.DatabaseService;
import me.retrodaredevil.couchdbjava.integration.TestUtil;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.request.ViewQueryParamsBuilder;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestConstants.INTEGRATION_TEST)
public class IncludeDocsViewTest {
	private static final String DATABASE = "test_include_docs_view_database";

	@ParameterizedTest
	@MethodSource("me.retrodaredevil.couchdbjava.integration.DatabaseService#values")
	void test(DatabaseService databaseService) throws CouchDbException, JsonProcessingException {
		CouchDbInstance instance = TestUtil.createInstance(databaseService);
		CouchDbDatabase database = instance.getDatabase(DATABASE);
		database.create();
		final String documentId;
		final String revision;
		{
			DocumentResponse response = TestUtil.postDocumentCompatibility(databaseService, database, new StringJsonData("{\"test\": 43}"));
			assertTrue(response.isOk());
			documentId = response.getId();
			revision = response.getRev();
		}
		ObjectMapper mapper = new ObjectMapper();
		database.putDocument("_design/test_design", new StringJsonData(mapper.writeValueAsString(TestUtil.createIdViewDesignDocument())));


		ViewResponse response = database.queryView(
				"test_design",
				"id_view",
				new ViewQueryParamsBuilder()
						.includeDocs(true)
						.key(documentId)
						.build()
		);
		assertEquals(0, response.getOffset());
		assertEquals(1, response.getTotalRows());
		assertEquals(1, response.getRows().size());
		ViewResponse.DocumentEntry entry = response.getRows().get(0);
		assertEquals(documentId, entry.getId());
		assertEquals('"' + documentId + '"', entry.getKey().getJson()); // as long as the random generation doesn't need characters escaped for JSON, this will work
		assertEquals("null", entry.getValue().getJson());

		JsonData doc = entry.getDoc();
		ObjectNode objectNode = (ObjectNode) CouchDbJacksonUtil.getNodeFrom(doc);
		assertEquals(documentId, objectNode.get("_id").asText());
		assertEquals(revision, objectNode.get("_rev").asText());
		assertEquals(43, objectNode.get("test").asInt());
	}
}
