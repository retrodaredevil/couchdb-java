package me.retrodaredevil.couchdbjava.replicator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.retrodaredevil.couchdbjava.replicator.source.ReplicatorSource;
import me.retrodaredevil.couchdbjava.replicator.source.StringReplicatorSource;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @see <a href="https://docs.couchdb.org/en/stable/json-structure.html#replication-settings">docs.couchdb.org/en/stable/json-structure.html#replication-settings</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
		getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE,
		creatorVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
public class SimpleReplicatorDocument implements ReplicatorDocument {
	@JsonProperty("source")
	private final ReplicatorSource source;
	@JsonProperty("target")
	private final ReplicatorSource target;
	@JsonProperty("cancel")
	private final Boolean cancel;
	@JsonProperty("checkpoint_interval")
	private final Long checkpointIntervalMillis;
	@JsonProperty("continuous")
	private final Boolean continuous;
	@JsonProperty("create_target")
	private final Boolean createTarget;
	@JsonProperty("doc_ids")
	private final List<String> documentIds;
	@JsonProperty("filter")
	private final String filterFunctionName;
	@JsonProperty("source_proxy")
	private final ReplicatorSource sourceProxy;
	@JsonProperty("target_proxy")
	private final ReplicatorSource targetProxy;
	@JsonProperty("query_params")
	private final Map<String, Object> queryParameters;
	@JsonProperty("selector")
	private final Object selector; // TODO figure out what type this is supposed to be
	@JsonProperty("since_seq")
	private final String sinceSequence;
	@JsonProperty("use_checkpoints")
	private final Boolean useCheckpoints;
	@JsonProperty("winning_revs_only")
	private final Boolean winningRevisionsOnly;
	@JsonProperty("use_bulk_get")
	private final Boolean useBulkGet;

	@JsonProperty("create_target_params")
	private final Object createTargetParameters;

	// note there are some other fields automatically added to the replicator document
	//   These fields include: owner (string), _replication_state (string), _replication_state_time (string ISO formatted like Instant), _replication_stats (object)
	// We might consider adding these fields in the future, as they might be useful for *retrieval*, but for uploading design documents they don't make sense

	@JsonCreator
	public SimpleReplicatorDocument(
			@JsonProperty(value = "source", required = true) ReplicatorSource source,
			@JsonProperty(value = "target", required = true) ReplicatorSource target,
			@JsonProperty("cancel") Boolean cancel,
			@JsonProperty("checkpoint_interval") Long checkpointIntervalMillis,
			@JsonProperty("continuous") Boolean continuous,
			@JsonProperty("create_target") Boolean createTarget,
			@JsonProperty("doc_ids") List<String> documentIds,
			@JsonProperty("filter") String filterFunctionName,
			@JsonProperty("source_proxy") ReplicatorSource sourceProxy,
			@JsonProperty("target_proxy") ReplicatorSource targetProxy,
			@JsonProperty("query_params") Map<String, Object> queryParameters,
			@JsonProperty("selector") Object selector,
			@JsonProperty("since_seq") String sinceSequence,
			@JsonProperty("use_checkpoints") Boolean useCheckpoints,
			@JsonProperty("winning_revs_only") Boolean winningRevisionsOnly,
			@JsonProperty("use_bulk_get") Boolean useBulkGet,
			@JsonProperty("create_target_params") Object createTargetParameters) {
		this.source = requireNonNull(source);
		this.target = requireNonNull(target);
		this.cancel = cancel;
		this.checkpointIntervalMillis = checkpointIntervalMillis;
		this.continuous = continuous;
		this.createTarget = createTarget;
		this.documentIds = documentIds;
		this.filterFunctionName = filterFunctionName;
		this.sourceProxy = sourceProxy;
		this.targetProxy = targetProxy;
		this.queryParameters = queryParameters;
		this.selector = selector;
		this.sinceSequence = sinceSequence;
		this.useCheckpoints = useCheckpoints;
		this.winningRevisionsOnly = winningRevisionsOnly;
		this.useBulkGet = useBulkGet;
		this.createTargetParameters = createTargetParameters;
	}


	@Override
	public ReplicatorSource getSource() {
		return source;
	}

	@Override
	public ReplicatorSource getTarget() {
		return target;
	}

	@Override
	public boolean isCancel() {
		return Boolean.TRUE.equals(cancel);
	}

	@Override
	public Long getCheckpointIntervalMillis() {
		return checkpointIntervalMillis;
	}

	@Override
	public boolean isContinuous() {
		return Boolean.TRUE.equals(continuous);
	}

	@Override
	public boolean isCreateTarget() {
		return Boolean.TRUE.equals(createTarget);
	}

	@Override
	public List<String> getDocumentIds() {
		return documentIds == null ? Collections.emptyList() : documentIds;
	}

	@Override
	public String getFilterFunctionName() {
		return filterFunctionName;
	}

	@Override
	public ReplicatorSource getSourceProxy() {
		return sourceProxy;
	}

	@Override
	public ReplicatorSource getTargetProxy() {
		return targetProxy;
	}

	@Override
	public Map<String, Object> getQueryParameters() {
		return queryParameters;
	}

	@Override
	public Object getSelector() {
		return selector;
	}

	@Override
	public String getSinceSequence() {
		return sinceSequence;
	}

	@Override
	public boolean isUseCheckpoints() {
		return Boolean.TRUE.equals(useCheckpoints);
	}

	@Override
	public boolean isWinningRevisionsOnly() {
		return Boolean.TRUE.equals(winningRevisionsOnly);
	}

	@Override
	public boolean isUseBulkGet() {
		return Boolean.TRUE.equals(useBulkGet);
	}

	@Override
	public Object getCreateTargetParameters() {
		return createTargetParameters;
	}

	public static Builder builder(ReplicatorSource source, ReplicatorSource target) {
		return new Builder(source, target);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SimpleReplicatorDocument that = (SimpleReplicatorDocument) o;
		return source.equals(that.source) && target.equals(that.target) && Objects.equals(cancel, that.cancel) && Objects.equals(checkpointIntervalMillis, that.checkpointIntervalMillis) && Objects.equals(continuous, that.continuous) && Objects.equals(createTarget, that.createTarget) && Objects.equals(documentIds, that.documentIds) && Objects.equals(filterFunctionName, that.filterFunctionName) && Objects.equals(sourceProxy, that.sourceProxy) && Objects.equals(targetProxy, that.targetProxy) && Objects.equals(queryParameters, that.queryParameters) && Objects.equals(selector, that.selector) && Objects.equals(sinceSequence, that.sinceSequence) && Objects.equals(useCheckpoints, that.useCheckpoints) && Objects.equals(winningRevisionsOnly, that.winningRevisionsOnly) && Objects.equals(useBulkGet, that.useBulkGet) && Objects.equals(createTargetParameters, that.createTargetParameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source, target, cancel, checkpointIntervalMillis, continuous, createTarget, documentIds, filterFunctionName, sourceProxy, targetProxy, queryParameters, selector, sinceSequence, useCheckpoints, winningRevisionsOnly, useBulkGet, createTargetParameters);
	}

	@SuppressWarnings("unused")
	public static final class Builder {
		private final ReplicatorSource source;
		private final ReplicatorSource target;
		private Boolean cancel;
		private Long checkpointIntervalMillis;
		private Boolean continuous;
		private Boolean createTarget;
		private List<String> documentIds;
		private String filterFunctionName;
		private ReplicatorSource sourceProxy;
		private ReplicatorSource targetProxy;
		private Map<String, Object> queryParameters;
		private Object selector;
		private String sinceSequence;
		private Boolean useCheckpoints;
		private Boolean winningRevisionsOnly;
		private Boolean useBulkGet;
		private Object createTargetParameters;

		private Builder(ReplicatorSource source, ReplicatorSource target) {
			this.source = requireNonNull(source);
			this.target = requireNonNull(target);
		}
		public SimpleReplicatorDocument build() {
			return new SimpleReplicatorDocument(source, target, cancel, checkpointIntervalMillis, continuous, createTarget, documentIds, filterFunctionName, sourceProxy, targetProxy, queryParameters, selector, sinceSequence, useCheckpoints, winningRevisionsOnly, useBulkGet, createTargetParameters);
		}

		public Builder cancel() {
			cancel = true;
			return this;
		}
		public Builder checkpointInterval(Duration checkpointInterval) {
			checkpointIntervalMillis = checkpointInterval.toMillis();
			return this;
		}
		public Builder continuous() {
			continuous = true;
			return this;
		}
		public Builder createTarget() {
			createTarget = true;
			return this;
		}
		public Builder documentIds(List<String> documentIds) {
			this.documentIds = documentIds;
			return this;
		}
		public Builder filter(String filterFunctionName) {
			this.filterFunctionName = filterFunctionName;
			return this;
		}
		public Builder sourceProxy(URI sourceProxy) {
			this.sourceProxy = new StringReplicatorSource(sourceProxy);
			return this;
		}
		public Builder targetProxy(URI targetProxy) {
			this.targetProxy = new StringReplicatorSource(targetProxy);
			return this;
		}
		public Builder queryParameters(Map<String, Object> queryParameters) {
			this.queryParameters = queryParameters;
			return this;
		}
		public Builder selector(Object selector) {
			this.selector = selector;
			return this;
		}
		public Builder sinceSequence(String sinceSequence) {
			this.sinceSequence = sinceSequence;
			return this;
		}
		public Builder useCheckpoints() {
			this.useCheckpoints = true;
			return this;
		}
		public Builder winningRevisionsOnly() {
			this.winningRevisionsOnly = true;
			return this;
		}
		public Builder useBulkGet() {
			this.useBulkGet = true;
			return this;
		}
		public Builder createTargetParameters(Object createTargetParameters) {
			this.createTargetParameters = createTargetParameters;
			return this;
		}
	}
}
