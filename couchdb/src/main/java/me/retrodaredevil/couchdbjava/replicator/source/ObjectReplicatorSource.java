package me.retrodaredevil.couchdbjava.replicator.source;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.retrodaredevil.couchdbjava.CouchDbAuth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(
		getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE,
		creatorVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonDeserialize(as = ObjectReplicatorSource.class) // this annotation required to "override" superinterface annotation
public class ObjectReplicatorSource implements ReplicatorSource {
	private final URI url;
	private final Map<String, String> headers;
	private final RawAuth auth;

	@JsonCreator
	public ObjectReplicatorSource(
			@JsonProperty(value = "url", required = true) URI ur,
			@JsonProperty("headers") Map<String, String> headers,
			@JsonProperty("auth") RawAuth auth
	) {
		this.url = requireNonNull(ur);
		this.headers = headers == null ? null : Collections.unmodifiableMap(new HashMap<>(headers));
		this.auth = auth;
	}

	@JsonProperty("url")
	@Override
	public URI getUrl() {
		return url;
	}

	@JsonProperty("headers")
	public @Nullable Map<String, String> getHeadersRaw() {
		return headers;
	}

	/**
	 * @return The auth object directly placed on this object
	 */
	@JsonProperty("auth")
	public @Nullable RawAuth getAuthRaw() {
		return auth;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers == null ? Collections.emptyMap() : headers;
	}

	@Override
	public @NotNull CouchDbAuth getAuth() {
		// https://docs.couchdb.org/en/stable/replication/replicator.html#specifying-usernames-and-passwords
		if (auth != null && auth.getBasic() != null) {
			return auth.getBasic().toAuth();
		}
		String urlUserInfo = url.getUserInfo();
		if (urlUserInfo != null) {
			return CouchDbAuth.fromDecodedBasicAuthString(urlUserInfo);
		}
		String header = getHeaders().get("Authorization");
		if (header != null) {
			CouchDbAuth auth = CouchDbAuth.fromEncodedAuthorizationHeaderOrNull(header);
			if (auth != null) {
				return auth;
			}
		}
		return CouchDbAuth.createNoAuth();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ObjectReplicatorSource that = (ObjectReplicatorSource) o;
		return url.equals(that.url) && Objects.equals(headers, that.headers) && Objects.equals(auth, that.auth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, headers, auth);
	}

	public static Builder builder(URI url) {
		return new Builder(url);
	}

	public static final class Builder {
		private final URI url;
		private final Map<String, String> headers = new HashMap<>();
		private RawAuth auth = null;

		private Builder(URI url) {
			this.url = requireNonNull(url);
		}
		public Builder clearHeaders() {
			headers.clear();
			return this;
		}
		public Builder addHeader(String key, String value) {
			headers.put(key, value);
			return this;
		}
		public Builder addHeaders(Map<? extends String, ? extends String> headers) {
			this.headers.putAll(headers);
			return this;
		}
		public Builder addAuthHeader(CouchDbAuth auth) {
			headers.put("Authorization", "Basic " + auth.getBasicAuthString());
			return this;
		}

		/**
		 * Only supported <a href="https://docs.couchdb.org/en/stable/replication/replicator.html#specifying-usernames-and-passwords">in CouchDB 3.2.0 and above</a>
		 */
		public Builder addAuthObject(CouchDbAuth auth) {
			if (auth.usesAuth()) {
				this.auth = new RawAuth(new RawAuthBasic(auth.getUsername(), auth.getPassword()));
			} else {
				this.auth = null;
			}
			return this;
		}
		public ObjectReplicatorSource build() {
			return new ObjectReplicatorSource(url, headers.isEmpty() ? null : headers, auth);
		}
	}

	public static final class RawAuth {
		private final RawAuthBasic basic;

		@JsonCreator
		public RawAuth(@JsonProperty("basic") RawAuthBasic basic) {
			this.basic = basic;
		}

		@JsonProperty("basic")
		public RawAuthBasic getBasic() {
			return basic;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			RawAuth rawAuth = (RawAuth) o;
			return Objects.equals(basic, rawAuth.basic);
		}

		@Override
		public int hashCode() {
			return Objects.hash(basic);
		}
	}
	public static final class RawAuthBasic {
		private final String username;
		private final String password;

		@JsonCreator
		public RawAuthBasic(
				@JsonProperty(value = "username", required = true) String username,
				@JsonProperty(value = "password", required = true) String password) {
			this.username = requireNonNull(username);
			this.password = requireNonNull(password);
		}

		@JsonProperty("username")
		public String getUsername() {
			return username;
		}

		@JsonProperty("password")
		public String getPassword() {
			return password;
		}

		public CouchDbAuth toAuth() {
			return CouchDbAuth.create(username == null ? "" : username, password == null ? "" : password);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			RawAuthBasic that = (RawAuthBasic) o;
			return username.equals(that.username) && password.equals(that.password);
		}

		@Override
		public int hashCode() {
			return Objects.hash(username, password);
		}
	}
}
