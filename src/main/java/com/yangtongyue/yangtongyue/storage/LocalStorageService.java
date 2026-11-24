package com.yangtongyue.yangtongyue.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yangtongyue.yangtongyue.config.AppProperties;

/** Stores files on local disk and serves them under {@code /files/**}. Meant for development. */
@Configuration
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local")
public class LocalStorageService implements StorageService, WebMvcConfigurer {

	public static final String URL_PREFIX = "/files/";

	private final Path root;

	public LocalStorageService(AppProperties props) throws IOException {
		this.root = Path.of(props.storage().local().dir()).toAbsolutePath().normalize();
		Files.createDirectories(root);
	}

	@Override
	public String store(String key, InputStream content, long size, String contentType) throws IOException {
		Path target = resolve(key);
		Files.createDirectories(target.getParent());
		Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
		return URL_PREFIX + key;
	}

	@Override
	public void delete(String key) throws IOException {
		Files.deleteIfExists(resolve(key));
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(URL_PREFIX + "**").addResourceLocations(root.toUri().toString());
	}

	private Path resolve(String key) {
		Path p = root.resolve(key).normalize();
		if (!p.startsWith(root)) {
			throw new IllegalArgumentException("Invalid storage key");
		}
		return p;
	}
}
