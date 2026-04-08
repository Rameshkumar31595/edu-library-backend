package com.edulibrary.dashboard.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Metrics {
    private int savedResources;
    private int downloads;

    public Metrics() {
    }

    public Metrics(int savedResources, int downloads) {
        this.savedResources = savedResources;
        this.downloads = downloads;
    }

    public int getSavedResources() {
        return savedResources;
    }

    public void setSavedResources(int savedResources) {
        this.savedResources = savedResources;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }
}
