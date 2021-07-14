package me.retrodaredevil.couchdbjava.attachment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
		this.contentEncoding = contentEncoding;
		this.contentLength = contentLength;
		requireNonNull(this.base64EncodedDigest = base64EncodedDigest);
	}

	public @NotNull AcceptRange getAcceptRange() {
		return acceptRange;
	}

	public ContentEncoding getContentEncoding() {
		return contentEncoding;
	}

	public int getContentLength() {
		return contentLength;
	}

	public @NotNull String getBase64EncodedDigest() {
		return base64EncodedDigest;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AttachmentInfo that = (AttachmentInfo) o;
		return contentLength == that.contentLength && acceptRange == that.acceptRange && contentEncoding == that.contentEncoding && base64EncodedDigest.equals(that.base64EncodedDigest);
	}

	@Override
	public int hashCode() {
		return Objects.hash(acceptRange, contentEncoding, contentLength, base64EncodedDigest);
	}
}
