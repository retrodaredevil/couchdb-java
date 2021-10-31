package me.retrodaredevil.couchdbjava.instance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.retrodaredevil.couchdbjava.response.BulkGetResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class SolarThingBulkGetTest {
	@Test
	void test() throws JsonProcessingException {
		for (String name : new String[] {
				"bulk_get_data_2021-10-29.json",
				"bulk_get_data_2021-10-30.json",
		}) {
			InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
			requireNonNull(stream, "Could not get resource: " + name);
			String data = new BufferedReader(new InputStreamReader(stream)).lines().parallel().collect(Collectors.joining("\n"));
			ObjectMapper mapper = new ObjectMapper();
			mapper.readValue(data, BulkGetResponse.class);
		}
		// This test relies on the correct deserialization of JacksonJsonData.
		//   This test was originally added because JSON undefined (not present), was being given to InnerResult's constructor has a JSON null, rather than a Java null
	}
}
