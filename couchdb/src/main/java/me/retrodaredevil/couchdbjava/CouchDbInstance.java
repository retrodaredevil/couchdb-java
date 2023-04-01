package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.CouchDbGetResponse;
import me.retrodaredevil.couchdbjava.response.DatabaseInfo;
import me.retrodaredevil.couchdbjava.response.MembershipResponse;
import me.retrodaredevil.couchdbjava.response.SessionGetResponse;

import java.util.List;

public interface CouchDbInstance extends CouchDbRoot {
	CouchDbDatabase getReplicatorDatabase();
	CouchDbDatabase getUsersDatabase();


	SessionGetResponse getSessionInfo() throws CouchDbException;

	List<DatabaseInfo> getDatabaseInfos(List<String> databaseNames) throws CouchDbException;

	MembershipResponse membership() throws CouchDbException;

	/**
	 * Gets a {@link CouchDbNode} by name.
	 * @param name The name of the node
	 * @return The node
	 */
	CouchDbNode getNodeByName(String name);
}
