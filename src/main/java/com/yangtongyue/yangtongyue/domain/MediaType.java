package com.yangtongyue.yangtongyue.domain;

public enum MediaType {
	IMAGE, VIDEO, AUDIO, OTHER;

	/** Legacy integer codes stored in the {@code image.type} column. */
	public int code() {
		return this == OTHER ? -1 : ordinal();
	}

	public static MediaType fromCode(int code) {
		return code >= 0 && code < 3 ? values()[code] : OTHER;
	}

	public static MediaType fromContentType(String contentType) {
		if (contentType == null) {
			return OTHER;
		}
		if (contentType.startsWith("image/")) {
			return IMAGE;
		}
		if (contentType.startsWith("video/")) {
			return VIDEO;
		}
		if (contentType.startsWith("audio/")) {
			return AUDIO;
		}
		return OTHER;
	}
}
