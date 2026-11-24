package com.yangtongyue.yangtongyue.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yangtongyue.yangtongyue.domain.ShareLink;

public interface ShareLinkRepository extends JpaRepository<ShareLink, Integer> {

	Optional<ShareLink> findByToken(String token);

	List<ShareLink> findAllByOrderByCreatedAtDesc();
}
