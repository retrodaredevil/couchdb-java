package me.retrodaredevil.couchdbjava.replicator.source;

import com.fasterxml.jackson.annotation.JsonValue;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class StringReplicatorSource implements ReplicatorSource {
	@JsonValue
	private final URI uri;

	public static StringReplicatorSource fromString(String uri) {
		return new StringReplicatorSource(URI.create(uri));
	}
	public StringReplicatorSource(URI uri) {
		this.uri = requireNonNull(uri);
	}

	@Override
	public URI getUrl() {
		return uri;
	}

	@Override
	public Map<String, String> getHeaders() {
		return Collections.emptyMap();
	}

	@Override
	public @NotNull CouchDbAuth getAuth() {
		String userInfo = uri.getUserInfo();
		if (userInfo == null) {
			return CouchDbAuth.createNoAuth();
		}
		return authFromUserInfo(userInfo);
	}

	public static CouchDbAuth authFromUserInfo(String userInfo) {
		String[] split = userInfo.split(":", 2);
		// if split.length is not 2, then userInfo does not conform to RFC 2617, but we will still interpret `username` as `username:` (a blank password)
		return CouchDbAuth.create(
				split[0],
				split.length == 2 ? split[1] : "" // interpret as empty password if not specified
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StringReplicatorSource that = (StringReplicatorSource) o;
		return uri.equals(that.uri);
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}
}

