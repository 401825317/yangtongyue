package com.yangtongyue.yangtongyue.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "image")
public class Image implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1643167618716243318L;
	@Id	//主键id
	@GeneratedValue(strategy=GenerationType.IDENTITY)//主键生成策略
	private int lid;
	private String imageid;
	private String groupid;
	private String categeryid;
	private String name;
	private String uploadpath;
	private long uploadtime;
	
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public String getCategeryid() {
		return categeryid;
	}
	public void setCategeryid(String categeryid) {
		this.categeryid = categeryid;
	}
	public String getImageid() {
		return imageid;
	}
	public void setImageid(String imageid) {
		this.imageid = imageid;
	}
	public int getLid() {
		return lid;
	}
	public void setLid(int lid) {
		this.lid = lid;
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
	public long getUploadtime() {
		return uploadtime;
	}
	public void setUploadtime(long uploadtime) {
		this.uploadtime = uploadtime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categeryid == null) ? 0 : categeryid.hashCode());
		result = prime * result + ((groupid == null) ? 0 : groupid.hashCode());
		result = prime * result + ((imageid == null) ? 0 : imageid.hashCode());
		result = prime * result + lid;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uploadpath == null) ? 0 : uploadpath.hashCode());
		result = prime * result + (int) (uploadtime ^ (uploadtime >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Image other = (Image) obj;
		if (categeryid == null) {
			if (other.categeryid != null)
				return false;
		} else if (!categeryid.equals(other.categeryid))
			return false;
		if (groupid == null) {
			if (other.groupid != null)
				return false;
		} else if (!groupid.equals(other.groupid))
			return false;
		if (imageid == null) {
			if (other.imageid != null)
				return false;
		} else if (!imageid.equals(other.imageid))
			return false;
		if (lid != other.lid)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uploadpath == null) {
			if (other.uploadpath != null)
				return false;
		} else if (!uploadpath.equals(other.uploadpath))
			return false;
		if (uploadtime != other.uploadtime)
			return false;
		return true;
	}
	
}
