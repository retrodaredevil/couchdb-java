package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.util.Objects.requireNonNull;

public final class CouchDbUtil {
	private CouchDbUtil() { throw new UnsupportedOperationException(); }
	public static String trimDoubleQuotes(String string) {
		if (string.charAt(0) != '"') {
			throw new IllegalArgumentException("First character is not a quote! string: " + string);
		}
		if (string.charAt(string.length() - 1) != '"') {
			throw new IllegalArgumentException("Last character is not a quote! string: " + string);
		}
		return string.substring(1, string.length() - 1); // trim off the double quotes
	}
	public static String revisionFromJson(JsonData jsonData) {
		final JsonNode jsonNode;
		try {
			jsonNode = CouchDbJacksonUtil.getNodeFrom(jsonData);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Could not parse JSON data that the database gave us!", e);
		}
		if (!jsonNode.isObject()) {
			throw new IllegalArgumentException("Database gave us a non-object! jsonNode: " + jsonNode);
		}
		JsonNode revisionNode = jsonNode.get("_rev");
		if (revisionNode == null) {
			throw new IllegalArgumentException("_rev not present on returned data! jsonNode: " + jsonNode);
		}
		if (!revisionNode.isTextual()) {
			throw new IllegalArgumentException("_rev is not a string! revisionNow: " + revisionNode);
		}
		String revision = revisionNode.asText();
		if (revision.length() < 34) { // revision should usually be exactly 34 characters, but we'll give it the benefit of the doubt
			throw new IllegalArgumentException("_rev is not valid! responseRevision: " + revision);
		}
		return revision;
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
