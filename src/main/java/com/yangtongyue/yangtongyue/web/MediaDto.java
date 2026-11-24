package com.yangtongyue.yangtongyue.web;

import com.yangtongyue.yangtongyue.domain.Media;
import com.yangtongyue.yangtongyue.domain.MediaType;

public record MediaDto(Integer id, MediaType type, String url, String name, String caption, Integer albumId,
		long time, Long takenAt, long uploadedAt) {

	public static MediaDto of(Media m) {
		return new MediaDto(m.getId(), m.getMediaType(), m.getUploadpath(), m.getName(), m.getCaption(),
				m.getAlbumId(), m.effectiveTime(), m.getTakenAt(), m.getUploadtime());
	}
}
