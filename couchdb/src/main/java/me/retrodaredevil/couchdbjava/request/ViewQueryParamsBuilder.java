package me.retrodaredevil.couchdbjava.request;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.StringJsonData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

@NullMarked
public class ViewQueryParamsBuilder {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private @Nullable Boolean conflicts = null;
	private @Nullable Boolean descending = null;
	private @Nullable JsonData endKeyJson = null;
	private @Nullable String endKeyDocId = null;
	private @Nullable Boolean group = null;
	private @Nullable Integer groupLevel = null;
	private @Nullable Boolean includeDocs = null;
	private @Nullable Boolean attachments = null;
	private @Nullable Boolean includeEncodingInfo = null;
	private @Nullable Boolean inclusiveEnd = null;
	private @Nullable JsonData keyJson = null;
	private @Nullable List<JsonData> keysJsonList = null;
	private @Nullable Integer limit = null;
	private @Nullable Boolean reduce = null;
	private @Nullable Integer skip = null;
	private @Nullable Boolean sorted = null;
	private @Nullable Boolean stable = null;
	// "stale" is deprecated
	private @Nullable JsonData startKeyJson;
	private @Nullable String startKeyDocId;
	private @Nullable Update update = null;
	private @Nullable Boolean updateSequence = null;

	public ViewQueryParams build() {
		return new ViewQueryParams(conflicts, descending, endKeyJson, endKeyDocId, group, groupLevel, includeDocs, attachments, includeEncodingInfo,
				inclusiveEnd, keyJson, keysJsonList, limit, reduce, skip, sorted, stable, startKeyJson, startKeyDocId, update, updateSequence);
	}

	public ViewQueryParamsBuilder conflicts(@Nullable Boolean conflicts) {
		this.conflicts = conflicts;
		if (Boolean.TRUE.equals(conflicts)) {
			includeDocs(true);
		}
		return this;
	}
	public ViewQueryParamsBuilder descending(@Nullable Boolean descending) { this.descending = descending; return this; }
	public ViewQueryParamsBuilder descending() { return descending(true); }
	// endKey defined below
	public ViewQueryParamsBuilder endKeyDocId(@Nullable String endKeyDocId) { this.endKeyDocId = endKeyDocId; return this; }
	public ViewQueryParamsBuilder group(@Nullable Boolean group) { this.group = group; return this; }
	public ViewQueryParamsBuilder groupLevel(@Nullable Integer groupLevel) {
		this.groupLevel = groupLevel;
		if (groupLevel != null) {
			group(true);
		}
		return this;
	}
	public ViewQueryParamsBuilder includeDocs(@Nullable Boolean includeDocs) { this.includeDocs = includeDocs; return this; }
	public ViewQueryParamsBuilder attachments(@Nullable Boolean attachments) {
		this.attachments = attachments;
		if (Boolean.TRUE.equals(attachments)) {
			includeDocs(true);
		}
		return this;
	}
	public ViewQueryParamsBuilder includeEncodingInfo(@Nullable Boolean includeEncodingInfo) {
		this.includeEncodingInfo = includeEncodingInfo;
		if (Boolean.TRUE.equals(includeEncodingInfo)) {
			includeDocs(true);
		}
		return this;
	}

	/**
	 * Defaults to true
	 */
	public ViewQueryParamsBuilder inclusiveEnd(@Nullable Boolean inclusiveEnd) { this.inclusiveEnd = inclusiveEnd; return this; }
	// key defined below
	// keys defined below
	public ViewQueryParamsBuilder limit(@Nullable Integer limit) { this.limit = limit; return this; }
	public ViewQueryParamsBuilder reduce(@Nullable Boolean reduce) { this.reduce = reduce; return this; }
	public ViewQueryParamsBuilder skip(@Nullable Integer skip) { this.skip = skip; return this; }
	public ViewQueryParamsBuilder sorted(@Nullable Boolean sorted) { this.sorted = sorted; return this; }
	public ViewQueryParamsBuilder stable(@Nullable Boolean stable) { this.stable = stable; return this; }
	// startKey defined below
	public ViewQueryParamsBuilder startKeyDocId(@Nullable String startKeyDocId) { this.startKeyDocId = startKeyDocId; return this; }
	public ViewQueryParamsBuilder update(@Nullable Update update) { this.update = update; return this; }
	public ViewQueryParamsBuilder updateSequence(@Nullable Boolean updateSequence) { this.updateSequence = updateSequence; return this; }

	// region endKey
	public ViewQueryParamsBuilder endKey(long endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(int endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(float endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(double endKey) {
		return endKeyJson(numberToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(boolean endKey) {
		return endKeyJson(booleanToJson(endKey));
	}
	public ViewQueryParamsBuilder endKey(String endKey) {
		return endKeyJson(stringToJson(endKey));
	}
	public ViewQueryParamsBuilder endKeyJson(@Nullable JsonData endKeyJson) {
		this.endKeyJson = endKeyJson;
		return this;
	}
	// endregion
	// region key
	public ViewQueryParamsBuilder key(long key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryParamsBuilder key(int key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryParamsBuilder key(float key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryParamsBuilder key(double key) {
		return keyJson(numberToJson(key));
	}
	public ViewQueryParamsBuilder key(boolean key) {
		return keyJson(booleanToJson(key));
	}
	public ViewQueryParamsBuilder key(String key) {
		return keyJson(stringToJson(key));
	}
	public ViewQueryParamsBuilder keyJson(@Nullable JsonData keyJson) {
		this.keyJson = keyJson;
		return this;
	}
	// endregion
	// region keys
	// Note: This reason is different from the other similar regions since this deals with a list/arrays
	public ViewQueryParamsBuilder keys(long... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (long key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keys(int... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (int key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keys(double... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (double key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keys(float... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (float key : keys) {
			keysJsonList.add(numberToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keys(boolean... keys) {
		List<JsonData> keysJsonList = new ArrayList<>();
		for (boolean key : keys) {
			keysJsonList.add(booleanToJson(key));
		}
		this.keysJsonList = keysJsonList;
		return this;
	}
	public ViewQueryParamsBuilder keysJson(JsonData... keysJsonList) { return keysJson(Arrays.asList(keysJsonList)); }
	public ViewQueryParamsBuilder keysJson(@Nullable List<JsonData> keysJsonList) { this.keysJsonList = keysJsonList; return this; }
	// endregion
	// region startKey
	public ViewQueryParamsBuilder startKey(long startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(int startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(float startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(double startKey) {
		return startKeyJson(numberToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(boolean startKey) {
		return startKeyJson(booleanToJson(startKey));
	}
	public ViewQueryParamsBuilder startKey(String startKey) {
		return startKeyJson(stringToJson(startKey));
	}
	public ViewQueryParamsBuilder startKeyJson(@Nullable JsonData startKeyJson) {
		this.startKeyJson = startKeyJson;
		return this;
	}
	// endregion

	private static JsonData stringToJson(String string) {
		try {
			return new StringJsonData(MAPPER.writeValueAsString(string));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	private static JsonData numberToJson(Number number) {
		requireNonNull(number);
		try {
			return new StringJsonData(MAPPER.writeValueAsString(number));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	private static JsonData booleanToJson(boolean b) {
		try {
			return new StringJsonData(MAPPER.writeValueAsString(b));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public enum Update {
		TRUE("true"), FALSE("false"), LAZY("lazy");
		@JsonValue
		private final String value;

		Update(String value) {
			this.value = value;
		}
	}
}
