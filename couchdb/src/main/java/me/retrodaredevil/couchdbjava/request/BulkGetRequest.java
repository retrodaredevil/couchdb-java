package me.retrodaredevil.couchdbjava.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@NullMarked
public class BulkGetRequest {
	private final List<Doc> docs;

	public BulkGetRequest(List<Doc> docs) {
		this.docs = Collections.unmodifiableList(docs);
	}
	public static BulkGetRequest of(String... documentIds) {
		List<Doc> docs = new ArrayList<>(documentIds.length);
		for (String documentId : documentIds) {
			docs.add(new Doc(documentId));
		}
		return new BulkGetRequest(docs);
	}
	public static BulkGetRequest from(Collection<String> documentIds) {
		List<Doc> docs = new ArrayList<>(documentIds.size());
		for (String documentId : documentIds) {
			docs.add(new Doc(documentId));
		}
		return new BulkGetRequest(docs);
	}

	@JsonProperty("docs")
	public List<Doc> getDocs() {
		return docs;
	}

	public static class Doc {
		private final String documentId;
		private final @Nullable String revision;

		public Doc(String documentId, @Nullable String revision) {
			this.documentId = requireNonNull(documentId);
			this.revision = revision;
		}
		public Doc(String documentId) {
			this(documentId, null);
		}

		@JsonProperty("id")
		public String getDocumentId() {
			return documentId;
		}

		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonProperty("rev")
		public @Nullable String getRevision() {
			return revision;
		}
	}
}
