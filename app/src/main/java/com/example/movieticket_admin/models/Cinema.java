package com.example.movieticket_admin.models;

import com.google.firebase.Timestamp;

public class Cinema {
    private String id;
    private String address;
    private String city;
    private Timestamp createdAt; // Đổi thành Timestamp
    private String description;
    private boolean isActive;
    private String name;
    private Timestamp updatedAt; // Đổi thành Timestamp
    private String imageUrl;

    // Constructor rỗng cho Firestore
    public Cinema() {}

    public Cinema(String id, String address, String city, Timestamp createdAt, String description,
                  boolean isActive, String name, Timestamp updatedAt, String imageUrl) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.createdAt = createdAt;
        this.description = description;
        this.isActive = isActive;
        this.name = name;
        this.updatedAt = updatedAt;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}