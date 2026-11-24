package com.yangtongyue.yangtongyue.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.yangtongyue.yangtongyue.domain.Media;
import com.yangtongyue.yangtongyue.domain.MediaType;
import com.yangtongyue.yangtongyue.repo.MediaRepository;
import com.yangtongyue.yangtongyue.storage.StorageService;

@Service
public class MediaService {

	private static final Logger LOG = LoggerFactory.getLogger(MediaService.class);

	private final MediaRepository mediaRepository;
	private final StorageService storage;

	public MediaService(MediaRepository mediaRepository, StorageService storage) {
		this.mediaRepository = mediaRepository;
		this.storage = storage;
	}

	public List<Media> timeline(Integer albumId) {
		return albumId == null ? mediaRepository.findTimeline() : mediaRepository.findTimelineByAlbum(albumId);
	}

	/**
	 * @param fallbackTakenAt used when the file has no EXIF capture date (typically the browser's
	 *                        {@code File.lastModified})
	 */
	@Transactional
	public Media upload(MultipartFile file, Integer albumId, Long fallbackTakenAt, String caption) throws IOException {
		String originalName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "file";
		String extension = StringUtils.getFilenameExtension(originalName);
		String id = UUID.randomUUID().toString().replace("-", "");
		String key = "media/" + id + (extension == null ? "" : "." + extension.toLowerCase());
		MediaType type = MediaType.fromContentType(file.getContentType());

		Long takenAt = type == MediaType.IMAGE ? readExifTakenAt(file) : null;
		if (takenAt == null) {
			takenAt = fallbackTakenAt;
		}

		String url;
		try (InputStream in = file.getInputStream()) {
			url = storage.store(key, in, file.getSize(), file.getContentType());
		}

		Media m = new Media();
		m.setImageid(id);
		m.setName(originalName);
		m.setSuffixName(extension == null ? "" : "." + extension);
		m.setMediaType(type);
		m.setUploadpath(url);
		m.setStorageKey(key);
		m.setUploadtime(System.currentTimeMillis());
		m.setTakenAt(takenAt);
		m.setAlbumId(albumId);
		m.setCaption(caption);
		return mediaRepository.save(m);
	}

	@Transactional
	public void delete(Media m) {
		if (m.getStorageKey() != null) {
			try {
				storage.delete(m.getStorageKey());
			} catch (Exception e) {
				LOG.warn("Failed to delete stored object {}", m.getStorageKey(), e);
			}
		}
		mediaRepository.delete(m);
	}

	private static Long readExifTakenAt(MultipartFile file) {
		try (InputStream in = file.getInputStream()) {
			Metadata metadata = ImageMetadataReader.readMetadata(in);
			ExifSubIFDDirectory exif = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			if (exif == null) {
				return null;
			}
			Date date = exif.getDateOriginal(TimeZone.getDefault());
			return date == null ? null : date.getTime();
		} catch (Exception e) {
			return null;
		}
	}
}
