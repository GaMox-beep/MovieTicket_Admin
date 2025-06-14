package com.example.movieticket_admin.models;

public class Cinema {
    private String id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private String description;

    public Cinema() {}  // Required for Firestore

    public Cinema(String id, String name, String city, String address, String phone, String description) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.description = description;
    }

    // Getters và Setters...
}

