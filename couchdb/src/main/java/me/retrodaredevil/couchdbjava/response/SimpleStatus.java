package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SimpleStatus {
	private final boolean ok;

	@JsonCreator
	public SimpleStatus(@JsonProperty(value = "ok", required = true) boolean ok) {
		this.ok = ok;
	}

	@JsonProperty("ok")
	public boolean isOk() {
		return ok;
	}
}
