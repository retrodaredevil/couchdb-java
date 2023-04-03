package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.couchdbjava.tag.DocumentEntityTag;

import static java.util.Objects.requireNonNull;

/**
 * Represents the body of a document response along with other values (such as header values)
 * that we (retrodaredevil, couchdb-java author) deemed worthy of exposing as a public API.
 */
public class DocumentResponse {
	private final String id;
	private final boolean ok;
	private final String rev;
	private final DocumentEntityTag eTag;

	public DocumentResponse(
			String id,
			boolean ok,
			String rev,
			DocumentEntityTag eTag) {
		this.id = requireNonNull(id);
		this.ok = ok;
		this.rev = requireNonNull(rev);
		this.eTag = requireNonNull(eTag);
	}
	public static DocumentResponse create(DocumentResponse.Body body, DocumentEntityTag eTag) {
		return new DocumentResponse(body.id, body.ok, body.rev, eTag);
	}

	public String getId() {
		return id;
	}

	public boolean isOk() {
		return ok;
	}

	public String getRev() {
		return rev;
	}

	public DocumentEntityTag getETag() {
		return eTag;
	}

	/**
	 * This class represents the actual response body that is returned by many endpoints.
	 *
	 */
	public static final class Body {
		private final String id;
		private final boolean ok;
		private final String rev;

		@JsonCreator
		public Body(
				@JsonProperty("id") String id,
				@JsonProperty("ok") boolean ok,
				@JsonProperty("rev") String rev) {
			this.id = requireNonNull(id);
			this.ok = ok;
			this.rev = requireNonNull(rev);
		}

		public String getId() {
			return id;
		}

		public boolean isOk() {
			return ok;
		}

		public String getRev() {
			return rev;
		}
	}
}
