package com.edulibrary.dashboard.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {
    @Id
    private String id;

    private String name;

    @jakarta.persistence.Column(unique = true, nullable = false)
    private String email;

    @Embedded
    private Metrics metrics;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("title ASC")
    private List<ResourceItem> recentResources = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("title ASC")
    private List<LearningItem> learningItems = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    private List<DepartmentItem> departments = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    public List<ResourceItem> getRecentResources() {
        return recentResources;
    }

    public void setRecentResources(List<ResourceItem> recentResources) {
        this.recentResources = recentResources;
    }

    public List<LearningItem> getLearningItems() {
        return learningItems;
    }

    public void setLearningItems(List<LearningItem> learningItems) {
        this.learningItems = learningItems;
    }

    public List<DepartmentItem> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentItem> departments) {
        this.departments = departments;
    }
}
