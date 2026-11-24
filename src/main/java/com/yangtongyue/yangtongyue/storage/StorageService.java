package com.yangtongyue.yangtongyue.storage;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

	/** Stores the object and returns the URL browsers should load it from. */
	String store(String key, InputStream content, long size, String contentType) throws IOException;

	void delete(String key) throws IOException;
}
