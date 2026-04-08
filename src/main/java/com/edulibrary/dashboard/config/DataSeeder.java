package com.edulibrary.dashboard.config;

import com.edulibrary.dashboard.model.AnnouncementItem;
import com.edulibrary.dashboard.model.DepartmentItem;
import com.edulibrary.dashboard.model.LearningItem;
import com.edulibrary.dashboard.model.Metrics;
import com.edulibrary.dashboard.model.ResourceItem;
import com.edulibrary.dashboard.model.UserAccount;
import com.edulibrary.dashboard.model.UserRole;
import com.edulibrary.dashboard.model.Student;
import com.edulibrary.dashboard.repository.AnnouncementRepository;
import com.edulibrary.dashboard.repository.UserAccountRepository;
import com.edulibrary.dashboard.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(
            StudentRepository studentRepository,
            AnnouncementRepository announcementRepository,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (studentRepository.count() == 0) {
                Student student = new Student();
                student.setId("stu-001");
                student.setName("Student Demo");
                student.setEmail("demo.student@library.local");
                student.setMetrics(new Metrics(12, 8));

                ResourceItem r1 = createResource("r-001", "Data Structures and Algorithms", "Computer Science", "PDF", "2 hours ago", "/files/pdfs/data-structures-algorithms-notes.pdf", student);
                ResourceItem r2 = createResource("r-002", "Thermodynamics Fundamentals", "Mechanical Engg.", "Video", "5 hours ago", "https://www.youtube.com/watch?v=4LqZdkkBDas", student);
                ResourceItem r3 = createResource("r-003", "Digital Signal Processing", "Electronics", "PDF", "1 day ago", "/files/pdfs/digital-signal-processing-notes.pdf", student);
                student.setRecentResources(List.of(r1, r2, r3));

                LearningItem l1 = createLearning("l-001", "Data Structures", 60, "Today", "PDF", "https://www.orimi.com/pdf-test.pdf", student);
                LearningItem l2 = createLearning("l-002", "Circuit Theory", 35, "Yesterday", "PDF", "https://web.eecs.utk.edu/~hqi/teaching/ece505f15/lecture01_intro.pdf", student);
                LearningItem l3 = createLearning("l-003", "Engineering Mathematics", 80, "3 days ago", "Video", "https://www.youtube.com/watch?v=4LqZdkkBDas", student);
                student.setLearningItems(List.of(l1, l2, l3));

                DepartmentItem d1 = createDepartment("Computer Science", 156, "computer-science", student);
                DepartmentItem d2 = createDepartment("Electronics & Comm.", 98, "electronics", student);
                DepartmentItem d3 = createDepartment("Mechanical Engg.", 84, "mechanical", student);
                DepartmentItem d4 = createDepartment("Civil Engineering", 72, "civil-engineering", student);
                student.setDepartments(List.of(d1, d2, d3, d4));

                studentRepository.save(student);
            }

            if (userAccountRepository.count() == 0) {
                userAccountRepository.save(createUser(
                        "Student Demo",
                        "demo.student@library.local",
                        "Student@123",
                        UserRole.STUDENT,
                        "demo.student@library.local",
                        passwordEncoder
                ));

                userAccountRepository.save(createUser(
                        "Admin Demo",
                        "demo.admin@library.local",
                        "Admin@123",
                        UserRole.ADMIN,
                        null,
                        passwordEncoder
                ));
            }

            if (announcementRepository.count() == 0) {
                announcementRepository.save(createAnnouncement("a-001", "Library Update", "New course materials have been published. Please check your dashboard resources.", "normal", Instant.parse("2026-04-05T10:30:00Z")));
                announcementRepository.save(createAnnouncement("a-002", "Submission Reminder", "Final assignment submissions close this Friday at 6 PM.", "urgent", Instant.parse("2026-04-07T09:00:00Z")));
            }
        };
    }

    private ResourceItem createResource(String id, String title, String subject, String type, String lastAccessed, String url, Student student) {
        ResourceItem item = new ResourceItem();
        item.setId(id);
        item.setTitle(title);
        item.setSubject(subject);
        item.setType(type);
        item.setLastAccessed(lastAccessed);
        item.setUrl(url);
        item.setStudent(student);
        return item;
    }

    private LearningItem createLearning(String id, String title, int progress, String lastAccessed, String type, String url, Student student) {
        LearningItem item = new LearningItem();
        item.setId(id);
        item.setTitle(title);
        item.setProgress(progress);
        item.setLastAccessed(lastAccessed);
        item.setType(type);
        item.setUrl(url);
        item.setStudent(student);
        return item;
    }

    private DepartmentItem createDepartment(String name, int count, String slug, Student student) {
        DepartmentItem item = new DepartmentItem(name, count, slug);
        item.setStudent(student);
        return item;
    }

    private UserAccount createUser(String name, String email, String rawPassword, UserRole role, String studentEmail, PasswordEncoder passwordEncoder) {
        UserAccount account = new UserAccount();
        account.setName(name);
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(rawPassword));
        account.setRole(role);
        account.setStudentEmail(studentEmail);
        return account;
    }

    private AnnouncementItem createAnnouncement(String id, String title, String message, String priority, Instant createdAt) {
        AnnouncementItem item = new AnnouncementItem();
        item.setId(id);
        item.setTitle(title);
        item.setMessage(message);
        item.setPriority(priority);
        item.setCreatedAt(createdAt);
        return item;
    }
}
