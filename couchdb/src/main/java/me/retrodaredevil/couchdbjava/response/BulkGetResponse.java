package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.couchdbjava.json.JsonData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

@NullMarked
public class BulkGetResponse {
	private final List<Result> results;

	@JsonCreator
	public BulkGetResponse(@JsonProperty("results") List<Result> results) {
		this.results = Collections.unmodifiableList(results);
	}

	public List<Result> getResults() {
		return results;
	}

	public static class Result {
		private final String documentId;
		private final List<JsonData> docs;
		private final @Nullable ErrorDoc error;

		@JsonCreator
		public Result(@JsonProperty("id") String documentId, @JsonProperty("docs") List<InnerResult> innerResultArray) {
			this.documentId = requireNonNull(documentId);
			if (innerResultArray.size() == 0) {
				throw new IllegalArgumentException("docs array's size is 0!");
			}
			if (innerResultArray.size() != 1) {
				List<JsonData> docs = new ArrayList<>(innerResultArray.size());
				for (InnerResult result : innerResultArray) {
					if (result.error != null) {
						throw new IllegalArgumentException("docs's size is " + innerResultArray.size() + " and one has an error! This is unexpected!");
					}
					docs.add(requireNonNull(result.jsonData));
				}
				this.docs = docs;
				error = null;
			} else {
				InnerResult result = innerResultArray.get(0);
				if (result.jsonData != null) {
					docs = Collections.singletonList(result.jsonData);
					error = null;
				} else {
					docs = Collections.emptyList();
					error = result.error;
				}
			}
			if (innerResultArray.size() != 1) {
				throw new IllegalArgumentException("docs array's size is not 1!! It's " + innerResultArray.size() + "!");
			}
		}

		public String getDocumentId() {
			return documentId;
		}
		public boolean isError() {
			return error != null;
		}
		public @Nullable ErrorDoc getError() {
			return error;
		}
		public boolean hasConflicts() {
			return docs.size() > 1;
		}
		public boolean hasNoConflictsAndNoErrors() {
			return !isError() && !hasConflicts();
		}
		public JsonData getJsonDataAssertNotConflicted() {
			if (isError()) {
				throw new IllegalStateException("This has an error!");
			}
			if (docs.size() != 1) {
				throw new IllegalArgumentException("This has a conflict!");
			}
			return docs.get(0);
		}
		public List<JsonData> getJsonDataList() {
			return docs;
		}
	}
	private static class InnerResult {
		private final @Nullable JsonData jsonData;
		private final @Nullable ErrorDoc error;

		@JsonCreator
		InnerResult(@JsonProperty("ok") @Nullable JsonData jsonData, @JsonProperty("error") @Nullable ErrorDoc error) {
			this.jsonData = jsonData;
			this.error = error;
			if (jsonData == null && error == null) {
				throw new IllegalArgumentException("Either \"ok\" or \"error\" must be present!");
			}
			if (jsonData != null && error != null) {
				throw new IllegalArgumentException("Both \"ok\" and \"error\" cannot be present at the same time! ok: " + jsonData.getJson() + " error: " + error);
			}
		}
	}
	public static class ErrorDoc {
		private final String documentId;
		private final String revision;
		private final String error;
		private final String reason;

		@JsonCreator
		public ErrorDoc(
				@JsonProperty(value = "id", required = true) String documentId,
				@JsonProperty(value = "rev", required = true) String revision,
				@JsonProperty(value = "error", required = true) String error,
				@JsonProperty(value = "reason", required = true) String reason) {
			this.documentId = requireNonNull(documentId);
			this.revision = requireNonNull(revision);
			this.error = requireNonNull(error);
			this.reason = requireNonNull(reason);
		}

		public String getDocumentId() {
			return documentId;
		}

		public String getRevision() {
			return revision;
		}

		public String getError() {
			return error;
		}

		public String getReason() {
			return reason;
		}
		public boolean hasRevision() {
			return !"undefined".equals(revision);
		}

		@Override
		public String toString() {
			return "ErrorDoc(" +
					"documentId='" + documentId + '\'' +
					", revision='" + revision + '\'' +
					", error='" + error + '\'' +
					", reason='" + reason + '\'' +
					')';
		}
	}
}
