package com.yangtongyue.yangtongyue.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yangtongyue.yangtongyue.domain.Config;

public interface ConfigRepository extends JpaRepository<Config, Integer> {
}
