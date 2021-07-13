package me.retrodaredevil.couchdbjava;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.util.Objects.requireNonNull;

public final class CouchDbUtil {
	private CouchDbUtil() { throw new UnsupportedOperationException(); }
	public static String trimDoubleQuotes(String string) {
		return string.substring(1, string.length() - 1); // trim off the double quotes
	}


	private static String urlEncode(String id) {
		try {
			return URLEncoder.encode(id, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Could not encode: '" + id + "'.", e);
		}
	}
	public static String encodeAttachmentName(String attachmentName) {
		return urlEncode(attachmentName);
	}
	public static String encodeDocumentId(String documentId) {
		if (documentId.startsWith("_design/")) {
			return "_design/" + urlEncode(documentId.substring("_design/".length()));
		}
		return urlEncode(documentId);
	}
	public static String encodeETagHeader(String revision) {
		requireNonNull(revision);
		return '"' + revision + '"';
	}
	public static String encodeRevisionForHeader(String revision) {
		return encodeETagHeader(revision);
	}
}
