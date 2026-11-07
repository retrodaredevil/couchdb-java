package me.retrodaredevil.couchdbjava.option;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
@NullMarked
public class DatabaseCreationOption {

	private static final DatabaseCreationOption DEFAULT_OPTION = builder().build();

	private final @Nullable Integer shards;
	private final @Nullable Integer replicas;
	private final @Nullable Boolean partitioned;

	public DatabaseCreationOption(@Nullable Integer shards, @Nullable Integer replicas, @Nullable Boolean partitioned) {
		this.shards = shards;
		this.replicas = replicas;
		this.partitioned = partitioned;
	}
	public static DatabaseCreationOption createDefault() {
		return DEFAULT_OPTION;
	}
	public static DatabaseCreationOption.Builder builder() {
		return new DatabaseCreationOption.Builder();
	}

	public @Nullable Integer getShards() {
		return shards;
	}

	public @Nullable Integer getReplicas() {
		return replicas;
	}

	public @Nullable Boolean getPartitioned() {
		return partitioned;
	}

	public static final class Builder {
		private @Nullable Integer shards;
		private @Nullable Integer replicas;
		private @Nullable Boolean partitioned;

		private Builder() {
		}

		public Builder shards(@Nullable Integer shards) {
			this.shards = shards;
			return this;
		}
		public Builder replicas(@Nullable Integer replicas) {
			this.replicas = replicas;
			return this;
		}
		public Builder partitioned(@Nullable Boolean partitioned) {
			this.partitioned = partitioned;
			return this;
		}
		public Builder partitioned() {
			return partitioned(true);
		}

		public DatabaseCreationOption build() {
			return new DatabaseCreationOption(shards, replicas, partitioned);
		}
	}
}
