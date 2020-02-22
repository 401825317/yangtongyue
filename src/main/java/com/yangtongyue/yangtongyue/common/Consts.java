package com.yangtongyue.yangtongyue.common;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.yangtongyue.yangtongyue.entity.Config;
import com.yangtongyue.yangtongyue.intefaces.ConfigRepository;

@Component
public class Consts {

	@Autowired
	private ConfigRepository config;
	public  String secretId="";
	public  String secretKey ="";
	
	@PostConstruct
	public void init() {
		List<Config> configlist=config.findAll();
		Map<String,Config> m=Maps.newHashMap();
		for (Config config : configlist) {
			m.put(config.getKey(), config);
		}
		secretId=m.get("secretId").getValue();
		secretKey=m.get("secretKey").getValue();

	}

}
