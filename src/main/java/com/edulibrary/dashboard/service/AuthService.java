package com.edulibrary.dashboard.service;

import com.edulibrary.dashboard.dto.AuthResponse;
import com.edulibrary.dashboard.dto.AuthUserResponse;
import com.edulibrary.dashboard.dto.ForgotPasswordResponse;
import com.edulibrary.dashboard.dto.ForgotPasswordRequest;
import com.edulibrary.dashboard.dto.LoginRequest;
import com.edulibrary.dashboard.dto.MessageResponse;
import com.edulibrary.dashboard.dto.RegisterRequest;
import com.edulibrary.dashboard.dto.ResetPasswordRequest;
import com.edulibrary.dashboard.exception.NotFoundException;
import com.edulibrary.dashboard.exception.UnauthorizedException;
import com.edulibrary.dashboard.model.Metrics;
import com.edulibrary.dashboard.model.Student;
import com.edulibrary.dashboard.model.UserAccount;
import com.edulibrary.dashboard.model.UserRole;
import com.edulibrary.dashboard.repository.StudentRepository;
import com.edulibrary.dashboard.repository.UserAccountRepository;
import com.edulibrary.dashboard.security.JwtService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final String resetBaseUrl;
    private final String mailHost;

    public AuthService(
            UserAccountRepository userAccountRepository,
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.mail.from:noreply@library.local}") String mailFrom,
            @Value("${app.reset.base-url:http://localhost:5173}") String resetBaseUrl,
            @Value("${spring.mail.host:}") String mailHost
    ) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.mailFrom = mailFrom;
        this.resetBaseUrl = resetBaseUrl;
        this.mailHost = mailHost;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtService.generateToken(account);
        return new AuthResponse(token, toUserResponse(account), jwtService.getExpirationTime());
    }

    @Transactional(readOnly = true)
    public AuthUserResponse currentUser(String email) {
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return toUserResponse(account);
    }

    @Transactional
    public AuthUserResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userAccountRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserRole role = mapRole(request.getRole());

        UserAccount account = new UserAccount();
        account.setName(request.getName().trim());
        account.setEmail(normalizedEmail);
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setRole(role);

        if (role == UserRole.STUDENT) {
            account.setStudentEmail(normalizedEmail);
            ensureStudentRecord(normalizedEmail, request.getName().trim());
        }

        UserAccount saved = userAccountRepository.save(account);
        return toUserResponse(saved);
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElse(null);

        if (account == null) {
            return new ForgotPasswordResponse("If account exists, reset instructions have been generated.", null);
        }

        String resetToken = UUID.randomUUID().toString().replace("-", "");
        account.setResetToken(resetToken);
        account.setResetTokenExpiresAt(Instant.now().plusSeconds(900));
        userAccountRepository.save(account);

        if (trySendResetEmail(account.getEmail(), resetToken)) {
            return new ForgotPasswordResponse("Reset instructions have been sent to your email.", null);
        }

        return new ForgotPasswordResponse("Reset token generated for demo. Mail not configured, use this token to set a new password.", resetToken);
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (account.getResetToken() == null || account.getResetTokenExpiresAt() == null) {
            throw new UnauthorizedException("No reset request found. Please use forgot password first.");
        }

        if (!account.getResetToken().equals(request.getResetToken())) {
            throw new UnauthorizedException("Invalid reset token");
        }

        if (account.getResetTokenExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Reset token has expired");
        }

        account.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        account.setResetToken(null);
        account.setResetTokenExpiresAt(null);
        userAccountRepository.save(account);

        return new MessageResponse("Password reset successful. Please sign in with your new password.");
    }

    private UserRole mapRole(String role) {
        String normalized = String.valueOf(role).trim().toLowerCase(Locale.ROOT);
        if ("admin".equals(normalized)) return UserRole.ADMIN;
        return UserRole.STUDENT;
    }

    private void ensureStudentRecord(String email, String name) {
        if (studentRepository.findByEmailIgnoreCase(email).isPresent()) {
            return;
        }

        Student student = new Student();
        student.setId("stu-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        student.setName(name);
        student.setEmail(email);
        student.setMetrics(new Metrics(0, 0));
        student.setRecentResources(new ArrayList<>());
        student.setLearningItems(new ArrayList<>());
        student.setDepartments(new ArrayList<>());
        studentRepository.save(student);
    }

    private AuthUserResponse toUserResponse(UserAccount account) {
        String role = account.getRole().name().toLowerCase();
        return new AuthUserResponse(
                account.getId(),
                account.getName(),
                account.getEmail(),
                role,
                account.getStudentEmail()
        );
    }

    private boolean trySendResetEmail(String recipientEmail, String resetToken) {
        if (mailSender == null || !StringUtils.hasText(mailHost) || !StringUtils.hasText(mailFrom)) {
            return false;
        }

        try {
            String trimmedBaseUrl = String.valueOf(resetBaseUrl).replaceAll("/+$", "");
            String encodedEmail = URLEncoder.encode(recipientEmail, StandardCharsets.UTF_8);
            String encodedToken = URLEncoder.encode(resetToken, StandardCharsets.UTF_8);
            String resetLink = trimmedBaseUrl + "/login?recovery=1&email=" + encodedEmail + "&token=" + encodedToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(recipientEmail);
            message.setSubject("Edu Library Password Reset");
            message.setText(
                    "We received a password reset request for your Edu Library account.\n\n"
                            + "Reset link: " + resetLink + "\n"
                            + "Reset token: " + resetToken + "\n\n"
                            + "This token expires in 15 minutes."
            );
            mailSender.send(message);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
