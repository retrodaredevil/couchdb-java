package me.retrodaredevil.couchdbjava;

import com.fasterxml.jackson.core.Base64Variants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

/**
 * Authorization in CouchDB should follow <a href="https://www.ietf.org/rfc/rfc2617.txt">RFC2617</a>.
 * Characters in passwords are unrestricted, but usernames have certain restrictions.
 */
public class CouchDbAuth {
	private static final CouchDbAuth NO_AUTH = new CouchDbAuth(null, null);

	private final String username;
	private final String password;

	private CouchDbAuth(String username, String password) {
		this.username = username;
		this.password = password;
	}
	public static @NotNull CouchDbAuth create(String username, String password) {
		return new CouchDbAuth(requireNonNull(username), requireNonNull(password));
	}
	public static @NotNull CouchDbAuth createNoAuth() {
		return NO_AUTH;
	}
	public boolean usesAuth() {
		return username != null;
	}

	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}

	public String getBasicAuthString() {
		if (usesAuth()) {
			return Base64Variants.getDefaultVariant().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8));
		}
		return null;
	}

	/**
	 * Note: This is one of the few places that returns a <b>Nullable</b> {@link CouchDbAuth}.
	 * @param authorizationHeaderValue The header value such as `Basic dXNlcm5hbWU6cGFzc3dvcmQK`.
	 * @return The auth or null. null indicates an invalid header.
	 */
	public static @Nullable CouchDbAuth fromEncodedAuthorizationHeaderOrNull(String authorizationHeaderValue) {
		if (!authorizationHeaderValue.startsWith("Basic ")) {
			return null;
		}
		String base64Data = authorizationHeaderValue.substring(6);
		final byte[] data;
		try {
			data = Base64Variants.getDefaultVariant().decode(base64Data);
		} catch (IllegalArgumentException e) {
			return null;
		}
		String decoded = new String(data, StandardCharsets.UTF_8);
		return fromDecodedBasicAuthString(decoded);
	}
	/**
	 * Note that this method does not throw an exception if an invalid input is given
	 * @param basicAuthString The basic auth string (not including `{@code Basic }`). In the format of {@code username:password}. This should be RFC 2617 compliant.
	 * @return The CouchDbAuth
	 */
	public static CouchDbAuth fromDecodedBasicAuthString(String basicAuthString) {
		String[] split = basicAuthString.split(":", 2);
		// if split.length is not 2, then userInfo does not conform to RFC 2617, but we will still interpret `username` as `username:` (a blank password)
		return CouchDbAuth.create(
				split[0],
				split.length == 2 ? split[1] : "" // interpret as empty password if not specified
		);
	}

}
