package me.retrodaredevil.couchdbjava.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonAutoDetect(
		getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE,
		creatorVisibility = JsonAutoDetect.Visibility.NONE
)
@NullMarked
public class SecurityGroup {
	public static SecurityGroup BLANK = new SecurityGroup(null, null);

	private final @Nullable List<String> names;
	private final @Nullable List<String> roles;

	@JsonCreator
	public SecurityGroup(
			@JsonProperty("names") @Nullable List<String> names,
			@JsonProperty("roles") @Nullable List<String> roles) {
		this.names = names == null ? null : Collections.unmodifiableList(names);
		this.roles = roles == null ? null : Collections.unmodifiableList(roles);
	}

	@JsonProperty("names")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public @Nullable List<String> getNames() {
		return names;
	}
	public List<String> getNamesOrEmpty() {
		return names == null ? Collections.emptyList() : names;
	}

	@JsonProperty("roles")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public @Nullable List<String> getRoles() {
		return roles;
	}
	public List<String> getRolesOrEmpty() {
		return roles == null ? Collections.emptyList() : roles;
	}

	public SecurityGroup withName(@Nullable String name) {
		if (name == null || getNamesOrEmpty().contains(name)) {
			return this;
		}
		List<String> newNames = new ArrayList<>(getNamesOrEmpty());
		newNames.add(name);
		return new SecurityGroup(newNames, roles);
	}
	public SecurityGroup withRole(@Nullable String role) {
		if (role == null || getRolesOrEmpty().contains(role)) {
			return this;
		}
		List<String> newRoles = new ArrayList<>(getRolesOrEmpty());
		newRoles.add(role);
		return new SecurityGroup(names, newRoles);
	}
}
