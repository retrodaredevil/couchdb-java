package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class BulkDocumentResponse {
	private final String id;
	private final @Nullable Boolean ok;
	private final @Nullable String revision;
	private final @Nullable String error;
	private final @Nullable String reason;

	@JsonCreator
	public BulkDocumentResponse(
			@JsonProperty(value = "id", required = true) String id,
			@JsonProperty("ok") @Nullable Boolean ok,
			@JsonProperty("rev") @Nullable String revision,
			@JsonProperty("error") @Nullable String error,
			@JsonProperty("reason") @Nullable String reason) {
		this.id = requireNonNull(id);
		this.ok = ok;
		this.revision = revision;
		this.error = error;
		this.reason = reason;
	}

	public String getId() {
		return id;
	}
	public boolean isOk() {
		return Boolean.TRUE.equals(ok);
	}

	public @Nullable String getRevision() {
		return revision;
	}

	public @Nullable String getError() {
		return error;
	}

	public @Nullable String getReason() {
		return reason;
	}
}
