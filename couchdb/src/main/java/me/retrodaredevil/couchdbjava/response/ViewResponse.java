package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.couchdbjava.json.JsonData;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ViewResponse {
	private final int offset;
	private final List<DocumentEntry> rows;
	private final int totalRows;
	private final String updateSequence;

	@JsonCreator
	public ViewResponse(
			@JsonProperty(value = "offset", required = true) int offset,
			@JsonProperty(value = "rows", required = true) List<DocumentEntry> rows,
			@JsonProperty(value = "total_rows", required = true) int totalRows,
			@JsonProperty("update_seq") String updateSequence) {
		this.offset = offset;
		this.rows = Collections.unmodifiableList(rows);
		this.totalRows = totalRows;
		this.updateSequence = updateSequence;
	}

	/**
	 * @return Offset where document list started
	 */
	@JsonProperty("offset")
	public int getOffset() {
		return offset;
	}

	@JsonProperty("rows")
	public List<DocumentEntry> getRows() {
		return rows;
	}

	/**
	 * @return The number of documents in the database/view
	 */
	@JsonProperty("total_rows")
	public int getTotalRows() {
		return totalRows;
	}

	/**
	 * @return The update sequence if {@link me.retrodaredevil.couchdbjava.request.ViewQueryParamsBuilder#updateSequence(Boolean)} was set to true, otherwise returns null
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("update_seq")
	public @Nullable String getUpdateSequence() {
		return updateSequence;
	}


	public static class DocumentEntry {
		private final String id;
		private final JsonData keyJson;
		private final JsonData valueJson;

		private final JsonData doc;

		@JsonCreator
		public DocumentEntry(
				@JsonProperty(value = "id", required = true) String id,
				@JsonProperty(value = "key", required = true) JsonData keyJson,
				@JsonProperty(value = "value", required = true) JsonData valueJson,
				@JsonProperty("doc") JsonData doc) {
			requireNonNull(this.id = id);
			requireNonNull(this.keyJson = keyJson);
			requireNonNull(this.valueJson = valueJson);
			this.doc = doc;
		}

		@JsonProperty("id")
		public String getId() {
			return id;
		}

		@JsonProperty("key")
		public JsonData getKey() {
			return keyJson;
		}

		@JsonProperty("value")
		public JsonData getValue() {
			return valueJson;
		}

		/**
		 * Note this is non-null only if {@link me.retrodaredevil.couchdbjava.request.ViewQueryParamsBuilder#includeDocs(Boolean)} was set
		 * @return The document this entry was emitted from.
		 */
		@JsonInclude(JsonInclude.Include.NON_NULL)
		@JsonProperty("doc")
		public JsonData getDoc() {
			return doc;
		}
	}
}
