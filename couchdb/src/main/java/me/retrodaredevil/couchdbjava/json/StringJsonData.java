package me.retrodaredevil.couchdbjava.json;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

@NullMarked
public class StringJsonData implements JsonData {
	private final String json;

	public StringJsonData(String json) {
		this.json = requireNonNull(json);
	}

	@JsonValue
	@JsonRawValue
	@Override
	public String getJson() {
		return json;
	}
}
