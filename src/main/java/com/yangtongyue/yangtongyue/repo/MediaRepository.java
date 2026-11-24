package com.yangtongyue.yangtongyue.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yangtongyue.yangtongyue.domain.Media;

public interface MediaRepository extends JpaRepository<Media, Integer> {

	@Query("select m from Media m order by coalesce(m.takenAt, m.uploadtime) desc, m.lid desc")
	List<Media> findTimeline();

	@Query("select m from Media m where m.albumId = :albumId order by coalesce(m.takenAt, m.uploadtime) desc, m.lid desc")
	List<Media> findTimelineByAlbum(Integer albumId);

	long countByAlbumId(Integer albumId);

	@Modifying
	@Query("update Media m set m.albumId = null where m.albumId = :albumId")
	void clearAlbum(Integer albumId);
}
