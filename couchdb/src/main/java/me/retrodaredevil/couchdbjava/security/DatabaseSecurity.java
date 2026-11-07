package me.retrodaredevil.couchdbjava.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@JsonAutoDetect(
		getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE,
		creatorVisibility = JsonAutoDetect.Visibility.NONE
)
@NullMarked
public class DatabaseSecurity {
	private final @Nullable SecurityGroup admins;
	private final @Nullable SecurityGroup members;

	@JsonCreator
	public DatabaseSecurity(@JsonProperty("admins") @Nullable SecurityGroup admins, @JsonProperty("members") @Nullable SecurityGroup members) {
		this.admins = admins;
		this.members = members;
	}

	@JsonProperty("admins")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public @Nullable SecurityGroup getAdmins() {
		return admins;
	}
	public SecurityGroup getAdminsOrBlank() {
		return admins == null ? SecurityGroup.BLANK : admins;
	}

	@JsonProperty("members")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public SecurityGroup getMembers() {
		return members == null ? SecurityGroup.BLANK : members;
	}
}
