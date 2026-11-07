package me.retrodaredevil.couchdbjava.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.couchdbjava.json.JsonData;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class BulkPostRequest {
	private final List<JsonData> docs;

	public BulkPostRequest(List<JsonData> docs) {
		this.docs = docs;
	}

	@JsonProperty("docs")
	public List<JsonData> getDocs() {
		return docs;
	}
}
