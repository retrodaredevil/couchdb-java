package me.retrodaredevil.couchdbjava.attachment;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public enum ContentEncoding {
	GZIP("gzip"),
	COMPRESS("compress"),
	/** zlib */
	DEFLATE("deflate"),
	/** Not used in Content-Encoding header */
	@Deprecated
	IDENTITY("identity")
	;
	private final String name;

	ContentEncoding(String name) {
		this.name = name;
	}

	public static ContentEncoding fromName(String name) {
		requireNonNull(name);
		for (ContentEncoding encoding : values()) {
			if (encoding.name.equals(name)) {
				return encoding;
			}
		}
		throw new IllegalArgumentException("Unknown content encoding: " + name);
	}
}
