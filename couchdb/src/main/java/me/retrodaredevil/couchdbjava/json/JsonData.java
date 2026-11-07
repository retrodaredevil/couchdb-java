package me.retrodaredevil.couchdbjava.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.couchdbjava.json.jackson.JacksonJsonData;
import org.jspecify.annotations.NullMarked;

/**
 * Represents unparsed JSON data. Some implementations may have already parsed it.
 */
@JsonDeserialize(using = JacksonJsonData.Deserializer.class)
@NullMarked
public interface JsonData {
	String getJson();

	/**
	 *
	 * @return true if this data from {{@link #getJson()}} is known to be valid JSON data, false if unknown
	 */
	default boolean isKnownToBeValid() {
		return false;
	}
}
