package com.edulibrary.dashboard.controller;

import com.edulibrary.dashboard.dto.ApiEnvelope;
import com.edulibrary.dashboard.dto.CreateAnnouncementRequest;
import com.edulibrary.dashboard.dto.UpdateProgressRequest;
import com.edulibrary.dashboard.exception.UnauthorizedException;
import com.edulibrary.dashboard.service.StudentDashboardService;
import jakarta.validation.Valid;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    public StudentDashboardController(StudentDashboardService service, JdbcTemplate jdbcTemplate) {
        this.service = service;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("ok", true, "service", "edu-library-student-dashboard-api");
    }

    @GetMapping("/health/db")
    public Map<String, Object> dbHealth() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return Map.of("ok", result != null && result == 1, "database", "connected");
        } catch (Exception ex) {
            return Map.of("ok", false, "database", "disconnected", "message", ex.getMessage());
        }
    }

    @GetMapping("/student/dashboard")
    public Object getDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        return service.getDashboard(authentication.getName());
    }

    @PatchMapping("/student/learning/{itemId}/progress")
    public ApiEnvelope updateProgress(Authentication authentication, @PathVariable String itemId, @Valid @RequestBody UpdateProgressRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        return new ApiEnvelope(true, service.updateProgress(authentication.getName(), itemId, request.getProgress()));
    }

    @PostMapping("/student/announcements")
    public ApiEnvelope createAnnouncement(@Valid @RequestBody CreateAnnouncementRequest request) {
        return new ApiEnvelope(true, service.createAnnouncement(request));
    }
}
