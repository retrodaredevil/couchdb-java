package me.retrodaredevil.couchdbjava.attachment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Used in HEAD request do /db/doc/attachment: https://docs.couchdb.org/en/stable/api/document/attachments.html#db-doc-attachment
 */
@NullMarked
public final class AttachmentInfo {
	private final AcceptRange acceptRange;
	private final @Nullable ContentEncoding contentEncoding;
	private final int contentLength;
	/** MD5 Binary digest */
	private final String base64EncodedDigest;

	public AttachmentInfo(
			AcceptRange acceptRange, @Nullable ContentEncoding contentEncoding, int contentLength, String base64EncodedDigest) {
		this.acceptRange = requireNonNull(acceptRange);
		this.contentEncoding = contentEncoding;
		this.contentLength = contentLength;
		this.base64EncodedDigest = requireNonNull(base64EncodedDigest);
	}

	public AcceptRange getAcceptRange() {
		return acceptRange;
	}

	public @Nullable ContentEncoding getContentEncoding() {
		return contentEncoding;
	}

	public int getContentLength() {
		return contentLength;
	}

	public String getBase64EncodedDigest() {
		return base64EncodedDigest;
	}

	@Override
	public boolean equals(@Nullable Object o) {
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
