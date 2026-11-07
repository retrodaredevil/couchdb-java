package me.retrodaredevil.couchdbjava.response;

import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.tag.DocumentEntityTag;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * This does not represent data actually returned by CouchDB, it is just here to help group stuff together
 */
@NullMarked
public class DocumentData {
	private final String revision;
	private final JsonData json;
	private final DocumentEntityTag eTag;

	public DocumentData(String revision, JsonData json, DocumentEntityTag eTag) {
		this.revision = requireNonNull(revision);
		this.json = requireNonNull(json);
		this.eTag = requireNonNull(eTag);
	}

	public String getRevision() {
		return revision;
	}

	public JsonData getJsonData() {
		return json;
	}

	public DocumentEntityTag getETag() {
		return eTag;
	}
}
