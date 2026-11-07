package me.retrodaredevil.couchdbjava.attachment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class AttachmentGet {
	private final String documentId;
	private final String attachmentName;
	private final @Nullable String attachmentDigest;
	private final @Nullable String documentRevision;

	private AttachmentGet(String documentId, String attachmentName, @Nullable String attachmentDigest, @Nullable String documentRevision) {
		this.documentId = requireNonNull(documentId);
		this.attachmentName = requireNonNull(attachmentName);
		this.attachmentDigest = attachmentDigest;
		this.documentRevision = documentRevision;
	}
	public static AttachmentGet get(String documentId, String attachmentName, @Nullable String documentRevision) {
		return new AttachmentGet(documentId, attachmentName, null, documentRevision);
	}
	public static AttachmentGet get(String documentId, String attachmentName) {
		return get(documentId, attachmentName, null);
	}
	public static AttachmentGet getIfUpdated(String documentId, String attachmentName, @Nullable String attachmentDigest, @Nullable String documentRevision) {
		return new AttachmentGet(documentId, attachmentName, attachmentDigest, documentRevision);
	}
	public static AttachmentGet getIfUpdated(String documentId, String attachmentName, @Nullable String attachmentDigest) {
		return getIfUpdated(documentId, attachmentName, attachmentDigest, null);
	}

	public String getDocumentId() {
		return documentId;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public @Nullable String getAttachmentDigest() {
		return attachmentDigest;
	}

	public @Nullable String getDocumentRevision() {
		return documentRevision;
	}
}
