package com.edulibrary.dashboard.controller;

import com.edulibrary.dashboard.dto.ApiEnvelope;
import com.edulibrary.dashboard.dto.CreateAnnouncementRequest;
import com.edulibrary.dashboard.dto.UpdateProgressRequest;
import com.edulibrary.dashboard.service.StudentDashboardService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StudentDashboardController {

    private final StudentDashboardService service;

    public StudentDashboardController(StudentDashboardService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("ok", true, "service", "edu-library-student-dashboard-api");
    }

    @GetMapping("/student/dashboard")
    public Object getDashboard(Authentication authentication) {
        return service.getDashboard(authentication.getName());
    }

    @PatchMapping("/student/learning/{itemId}/progress")
    public ApiEnvelope updateProgress(Authentication authentication, @PathVariable String itemId, @Valid @RequestBody UpdateProgressRequest request) {
        return new ApiEnvelope(true, service.updateProgress(authentication.getName(), itemId, request.getProgress()));
    }

    @PostMapping("/student/announcements")
    public ApiEnvelope createAnnouncement(@Valid @RequestBody CreateAnnouncementRequest request) {
        return new ApiEnvelope(true, service.createAnnouncement(request));
    }
}
