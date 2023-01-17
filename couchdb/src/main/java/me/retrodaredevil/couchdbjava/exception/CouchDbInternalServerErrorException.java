package me.retrodaredevil.couchdbjava.exception;

import me.retrodaredevil.couchdbjava.CouchDbStatusCode;
import me.retrodaredevil.couchdbjava.response.ErrorResponse;
import org.jetbrains.annotations.Nullable;

public class CouchDbInternalServerErrorException extends CouchDbCodeException {
	public CouchDbInternalServerErrorException(@Nullable ErrorResponse errorResponse) {
		super(CouchDbStatusCode.INTERNAL_SERVER_ERROR, errorResponse);
	}

	public CouchDbInternalServerErrorException(String message, @Nullable ErrorResponse errorResponse) {
		super(message, CouchDbStatusCode.INTERNAL_SERVER_ERROR, errorResponse);
	}

	public CouchDbInternalServerErrorException(String message, Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(message, cause, CouchDbStatusCode.INTERNAL_SERVER_ERROR, errorResponse);
	}

	public CouchDbInternalServerErrorException(Throwable cause, @Nullable ErrorResponse errorResponse) {
		super(cause, CouchDbStatusCode.INTERNAL_SERVER_ERROR, errorResponse);
	}

	public CouchDbInternalServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, @Nullable ErrorResponse errorResponse) {
		super(message, cause, enableSuppression, writableStackTrace, CouchDbStatusCode.INTERNAL_SERVER_ERROR, errorResponse);
	}
}
