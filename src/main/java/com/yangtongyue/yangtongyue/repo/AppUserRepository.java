package com.yangtongyue.yangtongyue.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yangtongyue.yangtongyue.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

	Optional<AppUser> findByUsername(String username);

	boolean existsByUsername(String username);
}
