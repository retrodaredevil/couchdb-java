package me.retrodaredevil.couchdbjava.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.couchdbjava.json.JsonData;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NullMarked
public class ViewQueryParams {
	@JsonProperty("conflicts")
	private final @Nullable Boolean conflicts;
	@JsonProperty("descending")
	private final @Nullable Boolean descending;
	@JsonProperty("endkey")
	private final @Nullable JsonData endKeyJson;
	@JsonProperty("endkey_docid")
	private final @Nullable String endKeyDocId;
	@JsonProperty("group")
	private final @Nullable Boolean group;
	@JsonProperty("group_level")
	private final @Nullable Integer groupLevel;
	@JsonProperty("include_docs")
	private final @Nullable Boolean includeDocs;
	@JsonProperty("attachments")
	private final @Nullable Boolean attachments;
	@JsonProperty("att_encoding_info")
	private final @Nullable Boolean includeEncodingInfo;
	@JsonProperty("inclusive_end")
	private final @Nullable Boolean inclusiveEnd;
	@JsonProperty("key")
	private final @Nullable JsonData keyJson;
	@JsonProperty("keys")
	private final @Nullable List<JsonData> keysJsonList;
	@JsonProperty("limit")
	private final @Nullable Integer limit;
	@JsonProperty("reduce")
	private final @Nullable Boolean reduce;
	@JsonProperty("skip")
	private final @Nullable Integer skip;
	@JsonProperty("sorted")
	private final @Nullable Boolean sorted;
	@JsonProperty("stable")
	private final @Nullable Boolean stable;
	// "stale" is deprecated
	@JsonProperty("startkey")
	private final @Nullable JsonData startKeyJson;
	@JsonProperty("startkey_docid")
	private final @Nullable String startKeyDocId;
	@JsonProperty("update")
	private final ViewQueryParamsBuilder.@Nullable Update update;
	@JsonProperty("update_seq")
	private final @Nullable Boolean updateSequence;

	public ViewQueryParams(@Nullable Boolean conflicts, @Nullable Boolean descending, @Nullable JsonData endKeyJson, @Nullable String endKeyDocId, @Nullable Boolean group, @Nullable Integer groupLevel, @Nullable Boolean includeDocs, @Nullable Boolean attachments, @Nullable Boolean includeEncodingInfo, @Nullable Boolean inclusiveEnd, @Nullable JsonData keyJson, @Nullable List<JsonData> keysJsonList, @Nullable Integer limit, @Nullable Boolean reduce, @Nullable Integer skip, @Nullable Boolean sorted, @Nullable Boolean stable, @Nullable JsonData startKeyJson, @Nullable String startKeyDocId, ViewQueryParamsBuilder.@Nullable Update update, @Nullable Boolean updateSequence) {
		this.conflicts = conflicts;
		this.descending = descending;
		this.endKeyJson = endKeyJson;
		this.endKeyDocId = endKeyDocId;
		this.group = group;
		this.groupLevel = groupLevel;
		this.includeDocs = includeDocs;
		this.attachments = attachments;
		this.includeEncodingInfo = includeEncodingInfo;
		this.inclusiveEnd = inclusiveEnd;
		this.keyJson = keyJson;
		this.keysJsonList = keysJsonList;
		this.limit = limit;
		this.reduce = reduce;
		this.skip = skip;
		this.sorted = sorted;
		this.stable = stable;
		this.startKeyJson = startKeyJson;
		this.startKeyDocId = startKeyDocId;
		this.update = update;
		this.updateSequence = updateSequence;
	}
}
