package com.yangtongyue.yangtongyue.storage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import com.yangtongyue.yangtongyue.config.AppProperties;
import com.yangtongyue.yangtongyue.domain.Config;
import com.yangtongyue.yangtongyue.repo.ConfigRepository;

import jakarta.annotation.PreDestroy;

/** Tencent Cloud COS storage. Credentials come from env, falling back to the legacy {@code config} table. */
@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "cos", matchIfMissing = true)
public class CosStorageService implements StorageService {

	private final COSClient client;
	private final String bucket;
	private final String baseUrl;

	public CosStorageService(AppProperties props, ConfigRepository configRepository) {
		AppProperties.Cos cos = props.storage().cos();
		String secretId = cos.secretId();
		String secretKey = cos.secretKey();
		if (!StringUtils.hasText(secretId) || !StringUtils.hasText(secretKey)) {
			Map<String, String> legacy = new HashMap<>();
			for (Config c : configRepository.findAll()) {
				legacy.put(c.getKey(), c.getValue());
			}
			secretId = legacy.get("secretId");
			secretKey = legacy.get("secretKey");
		}
		if (!StringUtils.hasText(secretId) || !StringUtils.hasText(secretKey)) {
			throw new IllegalStateException(
					"COS credentials missing: set COS_SECRET_ID / COS_SECRET_KEY, or use STORAGE_TYPE=local");
		}
		ClientConfig clientConfig = new ClientConfig(new Region(cos.region()));
		clientConfig.setHttpProtocol(HttpProtocol.https);
		this.client = new COSClient(new BasicCOSCredentials(secretId, secretKey), clientConfig);
		this.bucket = cos.bucket();
		this.baseUrl = StringUtils.hasText(cos.publicBaseUrl()) ? stripSlash(cos.publicBaseUrl())
				: "https://" + bucket + ".cos." + cos.region() + ".myqcloud.com";
	}

	@Override
	public String store(String key, InputStream content, long size, String contentType) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(size);
		if (contentType != null) {
			metadata.setContentType(contentType);
		}
		client.putObject(bucket, key, content, metadata);
		return baseUrl + "/" + key;
	}

	@Override
	public void delete(String key) {
		client.deleteObject(bucket, key);
	}

	@PreDestroy
	void shutdown() {
		client.shutdown();
	}

	private static String stripSlash(String s) {
		return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
	}
}
