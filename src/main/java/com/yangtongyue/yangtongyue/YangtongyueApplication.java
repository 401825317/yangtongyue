package com.yangtongyue.yangtongyue;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.yangtongyue.yangtongyue.common.qcolud.YTYCOSClient;

@SpringBootApplication
public class YangtongyueApplication {

	private static final Logger LOG = LoggerFactory.getLogger(YangtongyueApplication.class);
	@Autowired
	public YTYCOSClient  cosClient;
	
	public static void main(String[] args) {
		SpringApplication.run(YangtongyueApplication.class, args);
	}
	
	
	
//	@Bean
//	public void test() {
//		List<Bucket> buckets = cosClient.cosClient.listBuckets();
//		for (Bucket bucketElement : buckets) {
//		    String bucketName = bucketElement.getName();
//		    String bucketLocation = bucketElement.getLocation();
//		    LOG.info("bucketName="+bucketName);
//		    LOG.info("bucketLocation="+bucketLocation);
//		}
//		// 指定要上传的文件
//		File localFile = new File("C:\\Users\\Administrator\\Pictures\\杨橦栎\\微信图片_20200222001444.jpg");
//		// 指定要上传到的存储桶
//		String bucketName = "yangtongyue-1252240258";
//		// 指定要上传到 COS 上对象键
//		String key = "exampleobject";
//		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
//		PutObjectResult putObjectResult = cosClient.cosClient.putObject(putObjectRequest);
//		LOG.info("putObjectResult="+putObjectResult.getDateStr());
//		cosClient.cosClient.shutdown();
//	}
	
}
