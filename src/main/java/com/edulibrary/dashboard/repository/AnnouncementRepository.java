package com.edulibrary.dashboard.repository;

import com.edulibrary.dashboard.model.AnnouncementItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<AnnouncementItem, String> {
    List<AnnouncementItem> findTop5ByOrderByCreatedAtDesc();
}
