package com.yangtongyue.yangtongyue.common;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.yangtongyue.yangtongyue.entity.Config;
import com.yangtongyue.yangtongyue.intefaces.ConfigRepository;

@Component
public class Consts {

	@Autowired
	private ConfigRepository config;
	@Value("${COS_SECRET_ID:}")
	public  String secretId="";
	@Value("${COS_SECRET_KEY:}")
	public  String secretKey ="";
	
	@PostConstruct
	public void init() {
		if (!secretId.isEmpty() && !secretKey.isEmpty()) {
			return;
		}
		List<Config> configlist=config.findAll();
		Map<String,Config> m=Maps.newHashMap();
		for (Config config : configlist) {
			m.put(config.getKey(), config);
		}
		secretId=m.get("secretId").getValue();
		secretKey=m.get("secretKey").getValue();

	}

}
