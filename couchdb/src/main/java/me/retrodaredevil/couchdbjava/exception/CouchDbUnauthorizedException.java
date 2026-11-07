package me.retrodaredevil.couchdbjava.exception;

import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class CouchDbUnauthorizedException extends CouchDbCodeException {
	public CouchDbUnauthorizedException(@Nullable ErrorResponse errorResponse) {
		super(CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}

	public CouchDbUnauthorizedException(String message, @Nullable ErrorResponse errorResponse) {
		super(message, CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}

	public CouchDbUnauthorizedException(String message, Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(message, cause, CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}

	public CouchDbUnauthorizedException(Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(cause, CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}

	public CouchDbUnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, @Nullable ErrorResponse errorResponse) {
		super(message, cause, enableSuppression, writableStackTrace, CouchDbStatusCode.UNAUTHORIZED, errorResponse);
	}
}
