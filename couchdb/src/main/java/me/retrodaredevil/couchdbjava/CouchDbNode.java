package me.retrodaredevil.couchdbjava;

public interface CouchDbNode extends CouchDbRoot {

	String getName();

	// implement /_node/{node-name}/_stats https://docs.couchdb.org/en/stable/api/server/common.html#node-node-name-stats
	// implement /_node/{node-name}/_prometheus https://docs.couchdb.org/en/stable/api/server/common.html#node-node-name-prometheus
	// implement /_node/{node-name}/_system https://docs.couchdb.org/en/stable/api/server/common.html#node-node-name-system
	// implement /_node/{node-name}/_restart https://docs.couchdb.org/en/stable/api/server/common.html#node-node-name-restart
	// implement /_node/{node-name}/_versions https://docs.couchdb.org/en/stable/api/server/common.html#node-node-name-versions

	default CouchDbDatabase getDatabasesDatabase() {
		return getDatabase("_dbs");
	}
	default CouchDbDatabase getNodesDatabase() {
		return getDatabase("_nodes");
	}
	default CouchDbDatabase getUsersDatabase() {
		return getDatabase("_users");
	}
}
