package com.yangtongyue.yangtongyue.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yangtongyue.yangtongyue.domain.Album;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
}
