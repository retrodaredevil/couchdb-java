package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @see <a href=https://docs.couchdb.org/en/stable/api/server/common.html#membership">docs.couchdb.org/en/stable/api/server/common.html#membership</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
public class MembershipResponse {
	private final List<String> allNodes;
	private final List<String> clusterNodes;

	@JsonCreator
	public MembershipResponse(
			@JsonProperty(value = "all_nodes", required = true) List<String> allNodes,
			@JsonProperty(value = "cluster_nodes", required = true) List<String> clusterNodes) {
		this.allNodes = Collections.unmodifiableList(new ArrayList<>(allNodes));
		this.clusterNodes = Collections.unmodifiableList(new ArrayList<>(clusterNodes));
	}

	public List<String> getAllNodes() {
		return allNodes;
	}

	public List<String> getClusterNodes() {
		return clusterNodes;
	}
}
