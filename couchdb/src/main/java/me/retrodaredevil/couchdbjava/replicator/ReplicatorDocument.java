package me.retrodaredevil.couchdbjava.replicator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.couchdbjava.replicator.source.ReplicatorSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@JsonDeserialize(as = SimpleReplicatorDocument.class)
public interface ReplicatorDocument {
	ReplicatorSource getSource();

	ReplicatorSource getTarget();
	boolean isCancel();

	@Nullable Long getCheckpointIntervalMillis();

	/**
	 * @see <a href="https://docs.couchdb.org/en/stable/replication/replicator.html#normal-vs-continuous-replications">docs.couchdb.org/en/stable/replication/replicator.html#normal-vs-continuous-replications</a>
	 */
	boolean isContinuous();

	boolean isCreateTarget();

	List<String> getDocumentIds();

	@Nullable String getFilterFunctionName();

	/**
	 * Address of a proxy server through which replication from the source should occur.
	 * @return The ReplicatorSource. This should usually be a {@link me.retrodaredevil.couchdbjava.replicator.source.StringReplicatorSource}
	 */
	@Nullable ReplicatorSource getSourceProxy();
	/**
	 * Address of a proxy server through which replication from the target should occur.
	 * @return The ReplicatorSource. This should usually be a {@link me.retrodaredevil.couchdbjava.replicator.source.StringReplicatorSource}
	 */

	@Nullable ReplicatorSource getTargetProxy();

	Map<String, Object> getQueryParameters();

	@Nullable Object getSelector();

	@Nullable String getSinceSequence();
	boolean isUseCheckpoints();
	/**
	 * @see <a href="https://docs.couchdb.org/en/stable/replication/replicator.html#replicate-winning-revisions-only">docs.couchdb.org/en/stable/replication/replicator.html#replicate-winning-revisions-only</a>
	 */
	boolean isWinningRevisionsOnly();

	boolean isUseBulkGet();

	/**
	 * Note: This is mostly undocumented in CouchDB docs
	 * @return The create_target_parameters, usually a map or an {@link me.retrodaredevil.couchdbjava.option.DatabaseCreationOption}
	 */
	@Nullable Object getCreateTargetParameters();
}
