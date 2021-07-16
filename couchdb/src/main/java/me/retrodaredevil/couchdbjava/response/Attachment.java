package me.retrodaredevil.couchdbjava.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Represents the structure of an element of _attachments from a get request on a document
 * @see <a href="https://docs.couchdb.org/en/stable/api/document/common.html#attachments">_attachments strucutre docs</a>
 */
public class Attachment {
	private final String contentType;
	private final @Nullable byte[] data;
	private final String digest;
	private final @Nullable Integer encodedLength;
	private final @Nullable String encoding;
	private final @Nullable Integer length;
	private final String revision;
	private final @Nullable Boolean stub;
	private final @Nullable Boolean follows;

	@JsonCreator
	public Attachment(
			@JsonProperty(value = "content_type", required = true) String contentType,
			@JsonProperty("data") @Nullable byte[] data,
			@JsonProperty(value = "digest", required = true) String digest,
			@JsonProperty("encoded_length") Integer encodedLength,
			@JsonProperty("encoding") @Nullable String encoding,
			@JsonProperty("length") @Nullable Integer length,
			@JsonProperty(value = "revpos", required = true) String revision,
			@JsonProperty("stub") @Nullable Boolean stub,
			@JsonProperty("follows") @Nullable Boolean follows) {
		this.follows = follows;
		requireNonNull(this.contentType = contentType);
		this.data = data;
		this.digest = digest;
		this.encodedLength = encodedLength;
		this.encoding = encoding;
		this.length = length;
		this.revision = revision;
		this.stub = stub;
	}

	@JsonProperty("content_type")
	public @NotNull String getContentType() {
		return contentType;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("data")
	public @Nullable byte[] getData() {
		return data;
	}

	@JsonProperty("digest")
	public String getDigest() {
		return digest;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("encoded_length")
	public Integer getEncodedLength() {
		return encodedLength;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("encoding")
	public String getEncoding() {
		return encoding;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("length")
	public Integer getLength() {
		return length;
	}

	@JsonProperty("revpos")
	public String getRevision() {
		return revision;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("stub")
	public Boolean getStubRaw() {
		return stub;
	}
	public boolean getStub() {
		return Boolean.TRUE.equals(stub);
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("follows")
	public Boolean getFollowsRaw() {
		return follows;
	}

	/**
	 * @return true if this is a multipart response and the attachment's data is below
	 */
	public boolean getFollows() {
		return Boolean.TRUE.equals(follows);
	}
}
