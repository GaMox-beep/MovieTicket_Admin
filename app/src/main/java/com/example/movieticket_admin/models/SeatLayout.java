package com.example.movieticket_admin.models;

public class SeatLayout {
    private String name;
    private String originalLayout;
    private String currentLayout;

    // Constructor mặc định (yêu cầu bởi Firestore)
    public SeatLayout() {}

    public SeatLayout(String name, String originalLayout, String currentLayout) {
        this.name = name;
        this.originalLayout = originalLayout;
        this.currentLayout = currentLayout;
    }

    // Getters và Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOriginalLayout() { return originalLayout; }
    public void setOriginalLayout(String originalLayout) { this.originalLayout = originalLayout; }
    public String getCurrentLayout() { return currentLayout; }
    public void setCurrentLayout(String currentLayout) { this.currentLayout = currentLayout; }
}