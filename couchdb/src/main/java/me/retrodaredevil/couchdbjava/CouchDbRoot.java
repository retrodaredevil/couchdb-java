package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;

import java.util.List;

/**
 * Endpoints that are supported on the root of a CouchDB instance (e.g. /)
 * and endpoints that are also supported on the node of a CouchDB instance (e.g. _node/nonode@nohost/)
 */
public interface CouchDbRoot {

	/**
	 *
	 * @param pathPrefix The encoded path prefix such as "a/b/c" or "/a/b/c" or "/a/b/c/" or "a/b/c/"
	 * @param databaseName The name of the database
	 * @return
	 */
	CouchDbDatabase getDatabase(String pathPrefix, String databaseName);
	default CouchDbDatabase getDatabase(String name) {
		return getDatabase("", name);
	}

	// TODO this does not work for _node/_local
	CouchDbGetResponse getInfo() throws CouchDbException;

	List<String> getAllDatabaseNames() throws CouchDbException;

	CouchDbConfig getConfig(String pathPrefix, String name);
	default CouchDbConfig getConfig(String path) {
		return getConfig("", path);
	}
	/**
	 * Note: If this is a {@link CouchDbInstance}, this only works on PouchDB. If this is a {@link CouchDbNode}, this works on most CouchDB installs.
	 * @return The default config.
	 */
	default CouchDbConfig getConfig() {
		return getConfig("_config");
	}

}
