package me.retrodaredevil.couchdbjava.integration;

import me.retrodaredevil.couchdbjava.CouchDbDatabase;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.attachment.AcceptRange;
import me.retrodaredevil.couchdbjava.attachment.AttachmentData;
import me.retrodaredevil.couchdbjava.attachment.AttachmentGet;
import me.retrodaredevil.couchdbjava.attachment.AttachmentInfo;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.exception.CouchDbNotFoundException;
import me.retrodaredevil.couchdbjava.response.DocumentData;
import me.retrodaredevil.couchdbjava.response.DocumentResponse;
import okio.Okio;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class AttachmentTest {

	private static final String DATABASE = "test_attachment_database";

	@Test
	void test() throws CouchDbException, IOException {
		CouchDbInstance instance = TestUtil.createInstance();
		CouchDbDatabase database = instance.getDatabase(DATABASE);
		database.create();

		AttachmentGet attachmentGet = AttachmentGet.get("test_id", "test_attachment");
		try {
			database.getAttachmentInfo(attachmentGet);
			fail("This should not succeed!");
		} catch (CouchDbNotFoundException expected) {
		}
		try {
			database.getAttachment(attachmentGet);
			fail("This should not succeed!");
		} catch (CouchDbNotFoundException expected) {
		}
		String attachmentString = "Hello there this is some cool data\nIt even has multiple lines!";
		byte[] attachmentData = attachmentString.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(attachmentData);
		DocumentResponse response = database.putAttachmentOnNewDocument("test_id", "test_attachment", Okio.source(inputStream));
		assertTrue(response.isOk());
		assertEquals("test_id", response.getId());
		assertTrue(response.getRev().startsWith("1-"));

		AttachmentInfo attachmentInfo = database.getAttachmentInfo(attachmentGet);
		assertEquals(attachmentData.length, attachmentInfo.getContentLength());
		assertEquals(AcceptRange.BYTES, attachmentInfo.getAcceptRange());
		assertNull(attachmentInfo.getContentEncoding());

		AttachmentData data = database.getAttachment(attachmentGet);
		assertEquals(attachmentInfo, data.getAttachmentInfo());
		byte[] downloadedData = Okio.buffer(data.getDataSource()).readByteArray();
		assertArrayEquals(attachmentData, downloadedData);
		assertEquals("application/octet-stream", data.getContentType());

		DocumentResponse deleteResponse = database.deleteAttachment("test_id", "test_attachment", response.getRev());

		try {
			database.getAttachmentInfo(attachmentGet);
			fail("This should not succeed!");
		} catch (CouchDbNotFoundException expected) {
		}

		// assert that the blank document is still there
		DocumentData documentData = database.getDocument("test_id");
		assertEquals(deleteResponse.getRev(), documentData.getRevision());
	}
}
