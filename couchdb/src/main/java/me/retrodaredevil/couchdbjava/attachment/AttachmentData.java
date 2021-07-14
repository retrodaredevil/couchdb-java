package me.retrodaredevil.couchdbjava.attachment;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

import static java.util.Objects.requireNonNull;

public final class AttachmentData {
	private final AttachmentInfo attachmentInfo;
	private final String contentType;
	private final long contentLength;
	private final Source dataSource;

	public AttachmentData(AttachmentInfo attachmentInfo, String contentType, long contentLength, Source dataSource) {
		requireNonNull(this.attachmentInfo = attachmentInfo);
		this.contentType = contentType;
		this.contentLength = contentLength;
		requireNonNull(this.dataSource = dataSource);
	}

	public @NotNull AttachmentInfo getAttachmentInfo() {
		return attachmentInfo;
	}

	public @Nullable String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}

	public @NotNull InputStream getInputStream() {
		if (dataSource instanceof BufferedSource) {
			return ((BufferedSource) dataSource).inputStream();
		}
		return Okio.buffer(dataSource).inputStream();
	}
	public @NotNull Source getDataSource() {
		return dataSource;
	}
}
