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

import static java.util.Objects.requireNonNull;
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
		assertEquals(DocumentEntityTag.fromRevision(originalRevision), originalETag);
		assertEquals("my_id", documentId);

		try {
			database.putDocument(documentId, new StringJsonData("{\"test\": 43}"));
			fail();
		} catch (CouchDbUpdateConflictException expected) {
		}

		if (databaseService == DatabaseService.COUCHDB) {
			try {
				database.getDocumentIfUpdated(documentId, originalETag);
				fail();
			} catch (CouchDbNotModifiedException expected) {
			}
		} else if (databaseService == DatabaseService.POUCHDB) {
			// When an If-Match or If-None-Match header is sent with the revision, it will never match.
			// So in this case, If-None-Match would be true, so a response will be sent back
			// We assert that we receive that response and are basically documenting how we expect PouchDB to behave here.
			DocumentData data = database.getDocumentIfUpdated(documentId, DocumentEntityTag.fromRevision(originalRevision));
			assertEquals(originalRevision, data.getRevision());
			assertTrue(data.getETag().isWeak());


			// When we send the weak ETag along, we expect PouchDB to match the weak ETag values
			try {
				database.getDocumentIfUpdated(documentId, data.getETag());
				fail();
			} catch (CouchDbNotModifiedException expected) {
			}

			// let's confirm that trying to update a document with a revision ETag does not work
			try {
				database.updateDocument(documentId, originalETag, new StringJsonData("{\"test\": 44}"), true); // remember originalETag is a revision ETag
				fail();
			} catch (CouchDbUpdateConflictException expected) {
			}
			// let's confirm (the weird) case that using a weak ETag from a GET request does not work for updating a document
			//   (PouchDB has some weird thing going on)
			final DocumentEntityTag theETagThatYouThinkWouldWorkBecauseYaKnowPouchDbGaveItToUs;
			try {
				// Yes, you would think we should assign actualOriginalTag to data.getETag(), but PouchDB doesn't work that way for some reason
				database.updateDocument(documentId, data.getETag(), new StringJsonData("{\"test\": 44}"), true);
				fail();
				throw new AssertionError(); // included here so that the compiler understands the actual code flow
			} catch (CouchDbUpdateConflictException expected) {
				theETagThatYouThinkWouldWorkBecauseYaKnowPouchDbGaveItToUs = requireNonNull(expected.getResponseETag(), "We always expect a PouchDB server to include the ETag header, even in a failed PUT request.");
			}
			try {
				// Above we talked about weird PouchDB behavior.
				//   I'm convinced that this behavior demonstrated is a bug, or maybe PouchDB just never ever supports the If-Match header
				database.updateDocument(documentId, theETagThatYouThinkWouldWorkBecauseYaKnowPouchDbGaveItToUs, new StringJsonData("{\"test\": 44}"), true);
			} catch (CouchDbUpdateConflictException expected) {
				assertEquals(theETagThatYouThinkWouldWorkBecauseYaKnowPouchDbGaveItToUs, expected.getResponseETag());
			}
		} else throw new AssertionError();

		// show that updating a document with rev query parameter works on all databases
		DocumentResponse updateResponse = database.updateDocument(documentId, originalRevision, new StringJsonData("{\"test\": 44}"));
		assertTrue(updateResponse.getRev().startsWith("2-"));

		if (databaseService == DatabaseService.COUCHDB) {
			// Using the If-Match header on CouchDB should work
			DocumentResponse secondUpdateResponse = database.updateDocument(documentId, updateResponse.getETag(), new StringJsonData("{\"test\": 44}"), true);
			assertTrue(secondUpdateResponse.getRev().startsWith("3-"));
		}
	}

}
