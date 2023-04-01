package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.json.JsonData;

public interface CouchDbConfig {
	/**
	 * Typically this is "_config".
	 * <p>
	 * Note: Even if the path to this config is /_node/nonode@nohost/_config, the name is still "_config"
	 * @return The name of the config.
	 */
	String getName();

	void test() throws CouchDbException;

	JsonData query() throws CouchDbException;
	JsonData querySection(String section) throws CouchDbException;

	String queryValue(String section, String key) throws CouchDbException;

	/**
	 * @param value The new value or null to delete the value
	 * @return The old value or an empty string if it was not present
	 */
	String putValue(String section, String key, String value) throws CouchDbException;
	/**
	 * @return The old value
	 */
	default String deleteValue(String section, String key) throws CouchDbException {
		return putValue(section, key, null);
	}


	/**
	 *
	 * @throws CouchDbException
	 * @see <a href="https://docs.couchdb.org/en/stable/api/server/configuration.html#node-node-name-config-reload">docs.couchdb.org/en/stable/api/server/configuration.html#node-node-name-config-reload</a>
	 */
	void reload() throws CouchDbException;
}
