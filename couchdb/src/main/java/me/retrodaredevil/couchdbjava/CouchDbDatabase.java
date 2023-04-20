package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.attachment.AttachmentData;
import me.retrodaredevil.couchdbjava.attachment.AttachmentGet;
import me.retrodaredevil.couchdbjava.attachment.AttachmentInfo;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.option.DatabaseCreationOption;
import me.retrodaredevil.couchdbjava.request.BulkGetRequest;
import me.retrodaredevil.couchdbjava.request.BulkPostRequest;
import me.retrodaredevil.couchdbjava.response.*;
import me.retrodaredevil.couchdbjava.security.DatabaseSecurity;
import me.retrodaredevil.couchdbjava.tag.DocumentEntityTag;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CouchDbDatabase extends CouchDbShared {
	String getName();

	CouchDbShared getPartition(String partitionName);

	boolean exists() throws CouchDbException;
	void create(DatabaseCreationOption databaseCreationOption) throws CouchDbException;
	default void create() throws CouchDbException { create(DatabaseCreationOption.createDefault()); }
	boolean createIfNotExists(DatabaseCreationOption databaseCreationOption) throws CouchDbException;
	default boolean createIfNotExists() throws CouchDbException { return createIfNotExists(DatabaseCreationOption.createDefault()); }
	void deleteDatabase() throws CouchDbException;

	DatabaseInfo getDatabaseInfo() throws CouchDbException;

	/**
	 * Note: Does not work on PouchDB. Fails with "only_multipart_accepted"
	 * <p>
	 * Note that the {@link DocumentResponse#getETag()} value was not actually part of any header,
	 * but was assumed from the revision. Since this does not work on PouchDB, the implementation is free to
	 * always return a {@link DocumentEntityTag} with {@link DocumentEntityTag#isRevision() isRevision()} {@code == true}.
	 * @return The document response
	 */
	DocumentResponse postNewDocument(JsonData jsonData) throws CouchDbException;

	/**
	 * Puts a document with the given id in the database. This will either create a new document,
	 * or may update an existing one. If updating an existing one, the json must have a `_rev` field
	 * with the value of the current revision of the desired document to update.
	 */
	DocumentResponse putDocument(String id, JsonData jsonData) throws CouchDbException;

	/**
	 * Similar to {@link #putDocument(String, JsonData)}, except this is only for updating existing
	 * documents
	 * <p>
	 * Uses the "If-Match" header to update this document
	 */
	default DocumentResponse updateDocument(String id, String revision, JsonData jsonData) throws CouchDbException {
		return updateDocument(id, DocumentEntityTag.fromRevision(revision), jsonData, false);
	}
	default DocumentResponse updateDocument(String id, DocumentEntityTag eTag, JsonData jsonData) throws CouchDbException {
		return updateDocument(id, eTag, jsonData, false);
	}
	DocumentResponse updateDocument(String id, DocumentEntityTag eTag, JsonData jsonData, boolean forceETagUse) throws CouchDbException;

	DocumentResponse deleteDocument(String id, String revision) throws CouchDbException;

	DocumentData getDocument(String id) throws CouchDbException;

	/**
	 * Note: Some implementations may support {@code revision} being null, some may not
	 * <p>
	 * Note: On PouchDB, this method will not result in a {@link me.retrodaredevil.couchdbjava.exception.CouchDbNotModifiedException} because PouchDB cannot match revision ETags
	 * @param id The id of the document
	 * @param revision The revision of the document. If this is the latest revision, {@link me.retrodaredevil.couchdbjava.exception.CouchDbNotModifiedException} is thrown.
	 * @return DocumentData containing the data and revision of the retreived document
	 * @throws me.retrodaredevil.couchdbjava.exception.CouchDbNotModifiedException Thrown if the specified document is still at the given revision
	 * @throws CouchDbException May represent a connection error or that a document wasn't found, permission error, etc.
	 */
	default DocumentData getDocumentIfUpdated(String id, String revision) throws CouchDbException {
		return getDocumentIfUpdated(id, revision == null ? null : DocumentEntityTag.fromRevision(revision));
	}

	DocumentData getDocumentIfUpdated(String id, DocumentEntityTag eTag) throws CouchDbException;

	/**
	 * @throws UnsupportedOperationException Thrown if the response received does not contain a revision ETag.
	 * You should not catch this exception. If you expect this be possibly thrown, you should use {@link #getCurrentETag(String)} instead.
	 */
	default String getCurrentRevision(String id) throws CouchDbException {
		DocumentEntityTag eTag = getCurrentETag(id);
		if (eTag.isRevision()) {
			return eTag.getValue();
		}
		throw new UnsupportedOperationException("ETag is not a revision ETag!");
	}
	DocumentEntityTag getCurrentETag(String id) throws CouchDbException;

	DocumentResponse copyToNewDocument(String id, String newDocumentId) throws CouchDbException;
	DocumentResponse copyFromRevisionToNewDocument(String id, String revision, String newDocumentId) throws CouchDbException;
	DocumentResponse copyToExistingDocument(String id, String targetDocumentId, String targetDocumentRevision) throws CouchDbException;
	DocumentResponse copyFromRevisionToExistingDocument(String id, String revision, String targetDocumentId, String targetDocumentRevision) throws CouchDbException;



	DatabaseSecurity getSecurity() throws CouchDbException;
	void setSecurity(DatabaseSecurity databaseSecurity) throws CouchDbException;

	BulkGetResponse getDocumentsBulk(BulkGetRequest request) throws CouchDbException;
	List<BulkDocumentResponse> postDocumentsBulk(BulkPostRequest request) throws CouchDbException;

	/**
	 * Gets information about an attachment on a document
	 * @see <a href="https://docs.couchdb.org/en/stable/api/document/attachments.html#head--db-docid-attname">HEAD Documentation</a>
	 */
	@NotNull AttachmentInfo getAttachmentInfo(@NotNull AttachmentGet attachmentGet) throws CouchDbException;
	@NotNull AttachmentData getAttachment(@NotNull AttachmentGet attachmentGet) throws CouchDbException;

	@NotNull DocumentResponse putAttachment(@NotNull String documentId, @NotNull String attachmentName, @NotNull Source dataSource, @Nullable String documentRevision, @Nullable String contentType) throws CouchDbException;
	default @NotNull DocumentResponse putAttachment(@NotNull String documentId, @NotNull String attachmentName, @NotNull Source dataSource, @Nullable String documentRevision) throws CouchDbException {
		return putAttachment(documentId, attachmentName, dataSource, documentRevision, null);
	}
	default @NotNull DocumentResponse putAttachmentOnNewDocument(@NotNull String documentId, @NotNull String attachmentName, @NotNull Source dataSource) throws CouchDbException {
		return putAttachment(documentId, attachmentName, dataSource, null, null);
	}
	default @NotNull DocumentResponse putAttachmentOnNewDocument(@NotNull String documentId, @NotNull String attachmentName, @NotNull Source dataSource, @Nullable String contentType) throws CouchDbException {
		return putAttachment(documentId, attachmentName, dataSource, null, contentType);
	}

	@NotNull DocumentResponse deleteAttachment(@NotNull String documentId, @NotNull String attachmentName, @NotNull String documentRevision, boolean batch) throws CouchDbException;
	default @NotNull DocumentResponse deleteAttachment(@NotNull String documentId, @NotNull String attachmentName, @NotNull String documentRevision) throws CouchDbException {
		return deleteAttachment(documentId, attachmentName, documentRevision, false);
	}

	void compact() throws CouchDbException;

	// TODO implement:
	// _local_docs: https://docs.couchdb.org/en/stable/api/local.html#db-local-docs
	// _local/id: https://docs.couchdb.org/en/stable/api/local.html#db-local-id
	// maybe even just put a parameter in existing methods to say isLocal: true|false
}
