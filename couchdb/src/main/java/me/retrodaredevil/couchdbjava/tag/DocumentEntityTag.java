package me.retrodaredevil.couchdbjava.tag;

import me.retrodaredevil.couchdbjava.CouchDbUtil;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import okhttp3.Response;

/**
 * Represents an entity tag for a document.
 * Do not use this class to represent general ETag values as
 * validation may be applied to values inside this class specific to <b>documents</b> in a CouchDB-like database.
 */
public final class DocumentEntityTag {
	private final String rawValue;
	private final boolean weak;
	private final String value;

	private DocumentEntityTag(String rawValue, boolean weak, String value) {
		this.rawValue = rawValue;
		this.weak = weak;
		this.value = value;
	}

	public String getRawValue() {
		return rawValue;
	}
	public boolean isWeak() {
		return weak;
	}

	/**
	 * For a non-{@link #isWeak()} tag, this value is the revision.
	 * <p>
	 * For a {@link #isWeak()} tag, this value is something to do with whatever the heck PouchDB has going on
	 * @return The ETag value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * If true, that means that {@link #getValue()} returns the revision.
	 * @return {@code !{@link #isWeak()}}.
	 */
	public boolean isRevision() {
		return !weak;
	}

	/**
	 *
	 * @param headerValue The value of the header.
	 * @return The document tag
	 * @throws IllegalArgumentException If formatted incorrectly
	 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/ETag">mozilla developer docs for ETag header</a>
	 */
	public static DocumentEntityTag parseETag(String headerValue) {
		if (headerValue.startsWith("W/")) {
			String quotedValue = headerValue.substring(2);
			return new DocumentEntityTag(headerValue, true, CouchDbUtil.trimDoubleQuotes(quotedValue));
		}
		if (headerValue.length() < 36) { // minimum length of a revision is 34, plus two for the two double quotes
			throw new IllegalArgumentException("Revision length is too small! headerValue: " + headerValue);
		}
		return new DocumentEntityTag(headerValue, false, CouchDbUtil.trimDoubleQuotes(headerValue));
	}
	public static DocumentEntityTag fromDocumentResponse(Response response) {
		String eTag = response.header("ETag");
		if (eTag == null) {
			throw new IllegalArgumentException("ETag header was not present!");
		}
		return parseETag(eTag);
	}
	public static DocumentEntityTag createFromRevision(String revision) {
		return new DocumentEntityTag('"' + revision + '"', false, revision);
	}

}
