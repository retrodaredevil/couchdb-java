package me.retrodaredevil.couchdbjava.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbBadRequestException;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotFoundException;
import me.retrodaredevil.couchdbjava.integration.DatabaseService;
import me.retrodaredevil.couchdbjava.integration.TestUtil;
import me.retrodaredevil.couchdbjava.option.DatabaseCreationOption;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class RevsLimitTest {

	private static final String DATABASE = "test_revs_limit";

	@ParameterizedTest
	@MethodSource("me.retrodaredevil.couchdbjava.integration.DatabaseService#values")
	void test(DatabaseService databaseService) throws CouchDbException {
		CouchDbInstance instance = TestUtil.createDebugInstance(databaseService);
		CouchDbDatabase database = instance.getDatabase(DATABASE);

		if (databaseService == DatabaseService.COUCHDB) {
			database.create(DatabaseCreationOption.builder().revLimit(10).build());
			assertEquals(1000, database.getRevsLimit()); // show that revLimit in creation options does not work
			database.getRevsLimit();
			database.setRevsLimit(5);
			assertEquals(5, database.getRevsLimit());
		} else if (databaseService == DatabaseService.POUCHDB) {
			database.create(DatabaseCreationOption.builder().revLimit(5).autoCompaction().build());
			assertThrows(CouchDbNotFoundException.class, () -> database.getRevsLimit());
		}
		database.getDatabaseInfo();
	}
}
