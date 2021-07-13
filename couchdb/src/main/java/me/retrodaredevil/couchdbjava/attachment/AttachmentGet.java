package me.retrodaredevil.couchdbjava.attachment;

import static java.util.Objects.requireNonNull;

public final class AttachmentGet {
	private final String documentId;
	private final String attachmentName;
	private final String attachmentDigest;
	private final String documentRevision;

	private AttachmentGet(String documentId, String attachmentName, String attachmentDigest, String documentRevision) {
		requireNonNull(this.documentId = documentId);
		requireNonNull(this.attachmentName = attachmentName);
		this.attachmentDigest = attachmentDigest;
		this.documentRevision = documentRevision;
	}
	public static AttachmentGet get(String documentId, String attachmentName, String documentRevision) {
		return new AttachmentGet(documentId, attachmentName, null, documentRevision);
	}
	public static AttachmentGet get(String documentId, String attachmentName) {
		return get(documentId, attachmentName, null);
	}
	public static AttachmentGet getIfUpdated(String documentId, String attachmentName, String attachmentDigest, String documentRevision) {
		return new AttachmentGet(documentId, attachmentName, attachmentDigest, documentRevision);
	}
	public static AttachmentGet getIfUpdated(String documentId, String attachmentName, String attachmentDigest) {
		return getIfUpdated(documentId, attachmentName, attachmentDigest, null);
	}

	public String getDocumentId() {
		return documentId;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public String getAttachmentDigest() {
		return attachmentDigest;
	}

	public String getDocumentRevision() {
		return documentRevision;
	}
}
