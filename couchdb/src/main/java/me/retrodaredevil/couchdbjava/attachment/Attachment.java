package me.retrodaredevil.couchdbjava.attachment;

import static java.util.Objects.requireNonNull;

public final class Attachment {
	private final AttachmentInfo attachmentInfo;
	private final byte[] data;

	public Attachment(AttachmentInfo attachmentInfo, byte[] data) {
		requireNonNull(this.attachmentInfo = attachmentInfo);
		requireNonNull(this.data = data);
	}

	public AttachmentInfo getAttachmentInfo() {
		return attachmentInfo;
	}

	public byte[] getData() {
		return data;
	}
}
