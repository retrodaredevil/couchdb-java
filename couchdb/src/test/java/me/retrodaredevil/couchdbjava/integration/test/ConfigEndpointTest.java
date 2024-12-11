package me.retrodaredevil.couchdbjava.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import me.retrodaredevil.couchdbjava.CouchDbConfig;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.CouchDbNode;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.integration.DatabaseService;
import me.retrodaredevil.couchdbjava.integration.TestUtil;
import me.retrodaredevil.couchdbjava.json.JsonData;
import me.retrodaredevil.couchdbjava.json.jackson.CouchDbJacksonUtil;
import me.retrodaredevil.couchdbjava.response.MembershipResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class ConfigEndpointTest {

	@ParameterizedTest
	@MethodSource("me.retrodaredevil.couchdbjava.integration.DatabaseService#values")
	void test(DatabaseService databaseService) throws CouchDbException, JsonProcessingException, InterruptedException {
		CouchDbInstance instance = TestUtil.createInstance(databaseService);
		MembershipResponse membership = instance.membership();
		// the configured CouchDB from the compose file is not clustered and even if it was it should only handle 1 node
		assertEquals(1, membership.getClusterNodes().size());
		assertEquals(membership.getClusterNodes(), membership.getAllNodes()); // since it's not clustered these should be the same

		String nodeName = membership.getClusterNodes().get(0); // should be something like nonode@nohost
		CouchDbNode node = instance.getNodeByName(nodeName);
		node.getInfo(); // since we did not use "_local", this should succeed

		// valid options to configure here: https://docs.couchdb.org/en/stable/config/http.html#http-server-options
		// PouchDB allows you to access config via /_node/node1@127.0.0.1/, but it won't let you update values there
		CouchDbConfig config = databaseService == DatabaseService.POUCHDB ? instance.getConfig() : node.getConfig();
		config.test(); // send a head request to confirm that works
		JsonData entireConfig = config.query(); // confirm we can query the entire thing
		JsonNode configTree = CouchDbJacksonUtil.getNodeFrom(entireConfig);
		String httpSectionName = databaseService == DatabaseService.COUCHDB ? "chttpd" : "httpd";
		assertEquals("5984", config.queryValue(httpSectionName, "port"));

		if (databaseService == DatabaseService.COUCHDB) {
			// only for CouchDB because PouchDB doesn't seem to return the old value like CouchDB does
			assertEquals(configTree.get(httpSectionName).get("bind_address").asText(), config.putValue(httpSectionName, "bind_address", "0.0.0.0"));
			assertEquals("0.0.0.0", config.queryValue(httpSectionName, "bind_address"));
		}
		assertEquals("", config.putValue("admins", "newadmin", "coolpass"));
		// NOTE: As of CouchDB 3.4, values under the "admins" section are not hashed immediately, hence this sleep // https://github.com/apache/couchdb/issues/5358
		Thread.sleep(500);
		// NOTE: As of CouchDB 3.4, hashed passwords may be hashed differently. The "-pbkdf2-" prefix is no longer reliable, but "-pbkdf2" prefix is
		assertTrue(config.queryValue("admins", "newadmin").startsWith("-pbkdf2"));
		if (databaseService == DatabaseService.COUCHDB) {
			// only for CouchDB because PouchDB gives us a "unknown_config_value" error, which doesn't make any sense
			assertTrue(config.deleteValue("admins", "newadmin").startsWith("-pbkdf2"));
		}
	}
}
