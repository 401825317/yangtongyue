package com.yangtongyue.yangtongyue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Mapped onto the legacy {@code image} table so existing uploads keep showing up. */
@Entity
@Table(name = "image")
public class Media {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer lid;
	private String imageid;
	private String groupid;
	private String categeryid;
	private String name;
	@Column(length = 1024)
	private String uploadpath;
	private String suffixName;
	private int type;
	private long uploadtime;

	private Long takenAt;
	private Integer albumId;
	@Column(length = 1000)
	private String caption;
	@Column(length = 512)
	private String storageKey;

	public long effectiveTime() {
		return takenAt != null ? takenAt : uploadtime;
	}

	public Integer getId() {
		return lid;
	}

	public String getImageid() {
		return imageid;
	}

	public void setImageid(String imageid) {
		this.imageid = imageid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUploadpath() {
		return uploadpath;
	}

	public void setUploadpath(String uploadpath) {
		this.uploadpath = uploadpath;
	}

	public String getSuffixName() {
		return suffixName;
	}

	public void setSuffixName(String suffixName) {
		this.suffixName = suffixName;
	}

	public MediaType getMediaType() {
		return MediaType.fromCode(type);
	}

	public void setMediaType(MediaType mediaType) {
		this.type = mediaType.code();
	}

	public long getUploadtime() {
		return uploadtime;
	}

	public void setUploadtime(long uploadtime) {
		this.uploadtime = uploadtime;
	}

	public Long getTakenAt() {
		return takenAt;
	}

	public void setTakenAt(Long takenAt) {
		this.takenAt = takenAt;
	}

	public Integer getAlbumId() {
		return albumId;
	}

	public void setAlbumId(Integer albumId) {
		this.albumId = albumId;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getStorageKey() {
		return storageKey;
	}

	public void setStorageKey(String storageKey) {
		this.storageKey = storageKey;
	}
}
