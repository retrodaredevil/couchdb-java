package me.retrodaredevil.couchdbjava.exception;

import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import org.jetbrains.annotations.Nullable;

public class CouchDbBadRequestException extends CouchDbCodeException {
	public CouchDbBadRequestException(@Nullable ErrorResponse errorResponse) {
		super(CouchDbStatusCode.BAD_REQUEST, errorResponse);
	}

	public CouchDbBadRequestException(String message, @Nullable ErrorResponse errorResponse) {
		super(message, CouchDbStatusCode.BAD_REQUEST, errorResponse);
	}

	public CouchDbBadRequestException(String message, Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(message, cause, CouchDbStatusCode.BAD_REQUEST, errorResponse);
	}

	public CouchDbBadRequestException(Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(cause, CouchDbStatusCode.BAD_REQUEST, errorResponse);
	}

	public CouchDbBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, @Nullable ErrorResponse errorResponse) {
		super(message, cause, enableSuppression, writableStackTrace, CouchDbStatusCode.BAD_REQUEST, errorResponse);
	}
}
