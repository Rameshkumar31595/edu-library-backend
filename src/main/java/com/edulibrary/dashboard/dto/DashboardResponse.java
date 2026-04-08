package com.edulibrary.dashboard.dto;

import com.edulibrary.dashboard.model.AnnouncementItem;
import com.edulibrary.dashboard.model.DepartmentItem;
import com.edulibrary.dashboard.model.LearningItem;
import com.edulibrary.dashboard.model.Metrics;
import com.edulibrary.dashboard.model.ResourceItem;

import java.time.Instant;
import java.util.List;

public record DashboardResponse(
        StudentSummaryResponse student,
        Metrics metrics,
        List<ResourceItem> recentResources,
        List<LearningItem> learningItems,
        List<DepartmentItem> departments,
        List<AnnouncementItem> announcements,
        Instant generatedAt
) {
}
