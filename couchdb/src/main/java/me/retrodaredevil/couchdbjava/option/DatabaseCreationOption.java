package me.retrodaredevil.couchdbjava.option;

public class DatabaseCreationOption {

	private static final DatabaseCreationOption DEFAULT_OPTION = builder().build();

	private final Integer shards;
	private final Integer replicas;
	private final Boolean partitioned;
	private final Integer revLimit;
	private final Boolean autoCompaction;

	public DatabaseCreationOption(Integer shards, Integer replicas, Boolean partitioned, Integer revLimit, Boolean autoCompaction) {
		this.shards = shards;
		this.replicas = replicas;
		this.partitioned = partitioned;
		this.revLimit = revLimit;
		this.autoCompaction = autoCompaction;
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

	public Integer getRevLimit() {
		return revLimit;
	}

	public Boolean getAutoCompaction() {
		return autoCompaction;
	}

	public static final class Builder {
		private Integer shards;
		private Integer replicas;
		private Boolean partitioned;
		private Integer revLimit;
		private Boolean autoCompaction;

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
		public Builder revLimit(Integer revLimit) {
			this.revLimit = revLimit;
			return this;
		}
		public Builder autoCompaction(Boolean autoCompaction) {
			this.autoCompaction = autoCompaction;
			return this;
		}
		public Builder autoCompaction() {
			return autoCompaction(true);
		}

		public DatabaseCreationOption build() {
			return new DatabaseCreationOption(shards, replicas, partitioned, revLimit, autoCompaction);
		}
	}
}
