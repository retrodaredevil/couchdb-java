package me.retrodaredevil.couchdbjava.attachment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public enum AcceptRange {
	BYTES("bytes"),
	NONE("none"),
	;

	private final String name;

	AcceptRange(String name) {
		this.name = name;
	}
	@JsonCreator
	public static AcceptRange createFromValue(@NotNull String name) {
		requireNonNull(name);
		if ("bytes".equals(name)) {
			return BYTES;
		} else if ("none".equals(name)) {
			return NONE;
		}
		throw new IllegalArgumentException("Unknown value: " + name);
	}

	@JsonValue
	public String getName() {
		return name;
	}
}
