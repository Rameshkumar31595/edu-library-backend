package com.edulibrary.dashboard.service;

import com.edulibrary.dashboard.dto.CreateAnnouncementRequest;
import com.edulibrary.dashboard.dto.DashboardResponse;
import com.edulibrary.dashboard.dto.StudentSummaryResponse;
import com.edulibrary.dashboard.exception.NotFoundException;
import com.edulibrary.dashboard.model.AnnouncementItem;
import com.edulibrary.dashboard.model.LearningItem;
import com.edulibrary.dashboard.model.Student;
import com.edulibrary.dashboard.repository.AnnouncementRepository;
import com.edulibrary.dashboard.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
public class StudentDashboardService {

    private final StudentRepository studentRepository;
    private final AnnouncementRepository announcementRepository;

    public StudentDashboardService(StudentRepository studentRepository, AnnouncementRepository announcementRepository) {
        this.studentRepository = studentRepository;
        this.announcementRepository = announcementRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String email) {
        Student student = resolveStudent(email);
        return new DashboardResponse(
                new StudentSummaryResponse(student.getId(), student.getName(), student.getEmail()),
                student.getMetrics(),
                student.getRecentResources(),
                student.getLearningItems(),
                student.getDepartments(),
                announcementRepository.findTop5ByOrderByCreatedAtDesc(),
                Instant.now()
        );
    }

    @Transactional
    public LearningItem updateProgress(String email, String itemId, int progress) {
        Student student = resolveStudent(email);

        LearningItem item = student.getLearningItems().stream()
                .filter(it -> it.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Learning item not found: " + itemId));

        item.setProgress(progress);
        item.setLastAccessed("Just now");
        studentRepository.save(student);
        return item;
    }

    @Transactional
    public AnnouncementItem createAnnouncement(CreateAnnouncementRequest request) {
        AnnouncementItem item = new AnnouncementItem();
        item.setId("a-" + System.currentTimeMillis());
        item.setTitle(request.getTitle() == null || request.getTitle().isBlank() ? "Announcement" : request.getTitle());
        item.setMessage(request.getMessage());
        item.setPriority("urgent".equalsIgnoreCase(request.getPriority()) ? "urgent" : "normal");
        item.setCreatedAt(Instant.now());
        return announcementRepository.save(item);
    }

    private Student resolveStudent(String email) {
        return studentRepository.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new NotFoundException("Student not found for email: " + email));
    }
}
