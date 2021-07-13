package me.retrodaredevil.couchdbjava.attachment;

import static java.util.Objects.requireNonNull;

/**
 * Used in HEAD request do /db/doc/attachment: https://docs.couchdb.org/en/stable/api/document/attachments.html#db-doc-attachment
 */
public final class AttachmentInfo {
	private final AcceptRange acceptRange;
	private final ContentEncoding contentEncoding;
	private final int contentLength;
	/** MD5 Binary digest */
	private final String base64EncodedDigest;

	public AttachmentInfo(
			AcceptRange acceptRange, ContentEncoding contentEncoding, int contentLength, String base64EncodedDigest) {
		requireNonNull(this.acceptRange = acceptRange);
		requireNonNull(this.contentEncoding = contentEncoding);
		this.contentLength = contentLength;
		requireNonNull(this.base64EncodedDigest = base64EncodedDigest);
	}
}
