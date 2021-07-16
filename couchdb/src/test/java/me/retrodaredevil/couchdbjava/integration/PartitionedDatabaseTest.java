package me.retrodaredevil.couchdbjava.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbBadRequestException;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.option.DatabaseCreationOption;
import me.retrodaredevil.couchdbjava.request.ViewQueryParamsBuilder;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import me.retrodaredevil.couchdbjava.response.ViewResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class PartitionedDatabaseTest {
	private static final String DATABASE = "test_partitioned_database";

	@Test
	void test() throws CouchDbException, JsonProcessingException {
		CouchDbInstance instance = TestUtil.createInstance();
		CouchDbDatabase database = instance.getDatabase(DATABASE);
		database.create(new DatabaseCreationOption(null, null, true));
		assertTrue(database.getDatabaseInfo().getProperties().isPartitioned());

		try {
			database.postNewDocument(new StringJsonData("{\"test\": 43}"));
			fail();
		} catch (CouchDbBadRequestException e) {
			ErrorResponse error = requireNonNull(e.getErrorResponse());
			assertEquals("illegal_docid", error.getError());
			assertEquals("Doc id must be of form partition:id", error.getReason());
		}
		DocumentResponse putResponse = database.putDocument("my_partition:cool_id", new StringJsonData("{\"test\": 43}"));
		assertEquals("my_partition:cool_id", putResponse.getId());

		ObjectMapper mapper = new ObjectMapper();
		database.putDocument("_design/test_design", new StringJsonData(mapper.writeValueAsString(TestUtil.createIdViewDesignDocument(false))));
		database.putDocument("_design/test_design_partitioned", new StringJsonData(mapper.writeValueAsString(TestUtil.createIdViewDesignDocument(true))));

		ViewResponse viewResponse = database.queryView(
				"test_design",
				"id_view",
				new ViewQueryParamsBuilder()
						.key("my_partition:cool_id")
						.build()
		);

		ViewResponse partitionedViewResponse = database.getPartition("my_partition").queryView(
				"test_design_partitioned",
				"id_view",
				new ViewQueryParamsBuilder()
						.key("my_partition:cool_id")
						.build()
		);
		for (ViewResponse response : new ViewResponse[] { viewResponse, partitionedViewResponse }) {
			assertEquals(0, response.getOffset());
			assertEquals(1, response.getTotalRows());
			List<ViewResponse.DocumentEntry> entries = response.getRows();
			assertEquals(1, entries.size());
			ViewResponse.DocumentEntry entry = entries.get(0);
			assertEquals("my_partition:cool_id", entry.getId());
			assertEquals("\"my_partition:cool_id\"", entry.getKey().getJson());
		}

		// show that querying a partitioned view without specifying a partition will fail
		try {
			database.queryView(
					"test_design_partitioned",
					"id_view",
					new ViewQueryParamsBuilder()
							.key("my_partition:cool_id")
							.build()
			);
			fail();
		} catch (CouchDbBadRequestException e) {
			ErrorResponse error = requireNonNull(e.getErrorResponse());
			assertEquals("query_parse_error", error.getError());
		}

		// show that if you specify a partition and query a non-partitioned view, it fails
		try {
			database.getPartition("my_partition").queryView(
					"test_design",
					"id_view",
					new ViewQueryParamsBuilder()
							.key("my_partition:cool_id")
							.build()
			);
			fail();
		} catch (CouchDbBadRequestException e) {
			ErrorResponse error = requireNonNull(e.getErrorResponse());
			assertEquals("query_parse_error", error.getError());
		}
	}
}