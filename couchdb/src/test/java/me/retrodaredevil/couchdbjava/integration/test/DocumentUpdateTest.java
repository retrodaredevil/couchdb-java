package me.retrodaredevil.couchdbjava.integration.test;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotModifiedException;
import me.retrodaredevil.couchdbjava.exception.CouchDbUpdateConflictException;
import me.retrodaredevil.couchdbjava.integration.DatabaseService;
import me.retrodaredevil.couchdbjava.integration.TestUtil;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import me.retrodaredevil.couchdbjava.tag.DocumentEntityTag;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class DocumentUpdateTest {
	private static final String DATABASE = "test_document_update_database";


	@ParameterizedTest
	@MethodSource("me.retrodaredevil.couchdbjava.integration.DatabaseService#values")
	void test(DatabaseService databaseService) throws CouchDbException, IOException {
		CouchDbInstance instance = TestUtil.createDebugInstance(databaseService);
		CouchDbDatabase database = instance.getDatabase(DATABASE);
		database.create();

		DocumentResponse originalResponse = database.putDocument("my_id", new StringJsonData("{\"test\": 43}"));
		String documentId = originalResponse.getId();
		String originalRevision = originalResponse.getRev();
		DocumentEntityTag originalETag = originalResponse.getETag();
		assertTrue(originalETag.isRevision()); // also true for PouchDB for recently created documents
		assertEquals(originalRevision, originalETag.getValue());
		assertEquals(DocumentEntityTag.createFromRevision(originalRevision), originalETag);
		assertEquals("my_id", documentId);

		try {
			database.putDocument(documentId, new StringJsonData("{\"test\": 43}"));
			fail();
		} catch (CouchDbUpdateConflictException expected) {
		}

		final DocumentEntityTag actualOriginalETag;
		if (databaseService == DatabaseService.COUCHDB) {
			actualOriginalETag = originalETag;
			try {
				database.getDocumentIfUpdated(documentId, originalETag);
				fail();
			} catch (CouchDbNotModifiedException expected) {
			}
		} else if (databaseService == DatabaseService.POUCHDB) {
			// When an If-Match or If-None-Match header is sent with the revision, it will never match.
			// So in this case, If-None-Match would be true, so a response will be sent back
			// We assert that we receive that response and are basically documenting how we expect PouchDB to behave here.
			DocumentData data = database.getDocumentIfUpdated(documentId, DocumentEntityTag.createFromRevision(originalRevision));
			assertEquals(originalRevision, data.getRevision());
			assertTrue(data.getETag().isWeak());

			// When we send the weak ETag along, we expect PouchDB to match the weak ETag values
			try {
				database.getDocumentIfUpdated(documentId, data.getETag());
				fail();
			} catch (CouchDbNotModifiedException expected) {
			}
			actualOriginalETag = data.getETag();
		}

		// TODO update document
		// the above logic figured out the ETag needed to update the document.
//		DocumentResponse updateResponse = database.putDocument("my_id", new StringJsonData("{\"test\": 44}"));
	}

}
