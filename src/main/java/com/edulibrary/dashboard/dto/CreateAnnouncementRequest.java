package com.edulibrary.dashboard.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAnnouncementRequest {
    private String title;

    @NotBlank
    private String message;

    private String priority;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
