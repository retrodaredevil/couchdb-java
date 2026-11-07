package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class UpResponse {
	private final String status;

	@JsonCreator
	public UpResponse(@JsonProperty(value = "status", required = true) String status) {
		this.status = requireNonNull(status);
	}

	public String getStatus() {
		return status;
	}
}
