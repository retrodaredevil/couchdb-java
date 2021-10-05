package me.retrodaredevil.couchdbjava;

import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.request.ViewQuery;
import me.retrodaredevil.couchdbjava.request.ViewQueryParams;
import me.retrodaredevil.couchdbjava.response.ViewResponse;

/**
 * Represents functions that can be used on the root of a given database, or a particular partition of a given database
 */
public interface CouchDbShared {

	/**
	 *
	 * @param designDoc The design doc name. Something like "myDesign". Note that "_design/myDesign" is allowed, but you can simply do "myDesign".
	 * @param viewName The name of the view
	 * @param viewQueryParams
	 * @return
	 * @throws CouchDbException
	 */
	ViewResponse queryView(String designDoc, String viewName, ViewQueryParams viewQueryParams) throws CouchDbException;
	default ViewResponse queryView(ViewQuery viewQuery) throws CouchDbException {
		return queryView(viewQuery.getDesignDoc(), viewQuery.getViewName(), viewQuery.getParams());
	}

	/**
	 * Queries all documents.
	 * @return
	 * @throws CouchDbException
	 * @see <a href="https://docs.couchdb.org/en/stable/api/database/bulk-api.html#get--db-_all_docs">_all_docs</a>
	 * @see <a href="https://docs.couchdb.org/en/stable/api/partitioned-dbs.html#db-partition-partition-all-docs">partition _all_docs</a>
	 */
	ViewResponse allDocs(ViewQueryParams viewQueryParams) throws CouchDbException;

	// TODO implement:
	// _find: https://docs.couchdb.org/en/stable/api/database/find.html#api-db-find
}
