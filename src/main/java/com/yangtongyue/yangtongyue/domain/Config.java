package com.yangtongyue.yangtongyue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Legacy key/value table; only read as a fallback for COS credentials. */
@Entity
@Table(name = "config")
public class Config {

	@Id
	private Integer lid;
	@Column(name = "`key`")
	private String key;
	@Column(name = "`value`")
	private String value;

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
