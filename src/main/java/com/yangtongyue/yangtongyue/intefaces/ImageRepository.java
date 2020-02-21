package com.yangtongyue.yangtongyue.intefaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yangtongyue.yangtongyue.entity.Image;

public interface ImageRepository extends JpaRepository<Image, String> {

}
