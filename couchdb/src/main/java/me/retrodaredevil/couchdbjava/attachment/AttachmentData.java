package me.retrodaredevil.couchdbjava.attachment;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class AttachmentData {
	private final AttachmentInfo attachmentInfo;
	private final @Nullable String contentType;
	private final long contentLength;
	private final Source dataSource;

	public AttachmentData(AttachmentInfo attachmentInfo, @Nullable String contentType, long contentLength, Source dataSource) {
		this.attachmentInfo = requireNonNull(attachmentInfo);
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.dataSource = requireNonNull(dataSource);
	}

	public AttachmentInfo getAttachmentInfo() {
		return attachmentInfo;
	}

	public @Nullable String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}

	public InputStream getInputStream() {
		if (dataSource instanceof BufferedSource bufferedSource) {
			return bufferedSource.inputStream();
		}
		return Okio.buffer(dataSource).inputStream();
	}
	public Source getDataSource() {
		return dataSource;
	}
}
