package me.retrodaredevil.couchdbjava.exception;

import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import me.retrodaredevil.couchdbjava.tag.DocumentEntityTag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class CouchDbUpdateConflictException extends CouchDbCodeException {
	private final @Nullable DocumentEntityTag responseETag;

	public CouchDbUpdateConflictException(String message, @Nullable ErrorResponse errorResponse, @Nullable DocumentEntityTag responseETag) {
		super(message, CouchDbStatusCode.UPDATE_CONFLICT, errorResponse);
		this.responseETag = responseETag;
	}

	/**
	 * Note: This is only non-null on PouchDB servers. Also note that this value is a different ETag than if you were to make a GET request.
	 * (Using this value will not work if you using {@link me.retrodaredevil.couchdbjava.CouchDbDatabase#getDocumentIfUpdated(String, DocumentEntityTag)})
	 * @return The response ETag
	 */
	public @Nullable DocumentEntityTag getResponseETag() {
		return responseETag;
	}
	// if we need more constructors in the future, feel free to add them

}
