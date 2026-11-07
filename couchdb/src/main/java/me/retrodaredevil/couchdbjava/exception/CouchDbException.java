package me.retrodaredevil.couchdbjava.exception;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class CouchDbException extends Exception {
	public CouchDbException() {
	}

	public CouchDbException(String message) {
		super(message);
	}

	public CouchDbException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouchDbException(Throwable cause) {
		super(cause);
	}

	public CouchDbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
