package me.retrodaredevil.couchdbjava.integration;

import me.retrodaredevil.couchdbjava.CouchDbConfig;
import me.retrodaredevil.couchdbjava.CouchDbInstance;
import me.retrodaredevil.couchdbjava.CouchDbNode;
import me.retrodaredevil.couchdbjava.TestConstants;
import me.retrodaredevil.couchdbjava.exception.CouchDbException;
import me.retrodaredevil.couchdbjava.response.MembershipResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag(TestConstants.INTEGRATION_TEST)
public class ConfigEndpointTest {
	@Test
	void test() throws CouchDbException {
		CouchDbInstance instance = TestUtil.createInstance();
		MembershipResponse membership = instance.membership();
		// the configured CouchDB from the compose file is not clustered and even if it was it should only handle 1 node
		assertEquals(1, membership.getClusterNodes().size());
		assertEquals(membership.getClusterNodes(), membership.getAllNodes()); // since it's not clustered these should be the same

		String nodeName = membership.getClusterNodes().get(0); // should be something like nonode@nohost
		CouchDbNode node = instance.getNodeByName(nodeName);
		node.getInfo(); // since we did not use "_local", this should succeed
		// valid options to configure here: https://docs.couchdb.org/en/stable/config/http.html#http-server-options
		CouchDbConfig config = node.getConfig();
		config.test(); // send a head request to confirm that works
		System.out.println(config.query().getJson()); // confirm we can query the entire thing
		assertEquals("5984", config.queryValue("chttpd", "port"));

		assertNotNull(config.putValue("chttpd", "bind_address", "0.0.0.0"));
		System.out.println(config.queryValue("chttpd", "bind_address"));
		assertEquals("", config.putValue("admins", "newadmin", "coolpass"));
		assertTrue(config.deleteValue("admins", "newadmin").startsWith("-pbkdf2-"));
	}
}
