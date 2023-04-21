package me.retrodaredevil.couchdbjava.option;

public class DatabaseCreationOption {

	private static final DatabaseCreationOption DEFAULT_OPTION = builder().build();

	private final Integer shards;
	private final Integer replicas;
	private final Boolean partitioned;

	public DatabaseCreationOption(Integer shards, Integer replicas, Boolean partitioned) {
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

	public Integer getShards() {
		return shards;
	}

	public Integer getReplicas() {
		return replicas;
	}

	public Boolean getPartitioned() {
		return partitioned;
	}


	public static final class Builder {
		private Integer shards;
		private Integer replicas;
		private Boolean partitioned;

		private Builder() {
		}

		public Builder shards(Integer shards) {
			this.shards = shards;
			return this;
		}
		public Builder replicas(Integer replicas) {
			this.replicas = replicas;
			return this;
		}
		public Builder partitioned(Boolean partitioned) {
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
