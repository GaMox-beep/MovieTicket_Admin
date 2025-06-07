package com.example.movieticket_admin.models;


import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    private String title;
    private String genre;
    private int duration;
    private String releaseDate;
    private String status;
    private String posterUrl;
    private String trailerUrl;
    private String description;
    private long createdAt;
    private long updatedAt;

    // Empty constructor required for Firestore
    public Movie() {}

    // Full constructor
    public Movie(String title, String genre, int duration, String releaseDate,
                 String status, String posterUrl, String trailerUrl, String description) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.status = status;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Constructor without media files
    public Movie(String title, String genre, int duration, String releaseDate,
                 String status, String description) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.status = status;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDuration() { return duration; }
    public String getReleaseDate() { return releaseDate; }
    public String getStatus() { return status; }
    public String getPosterUrl() { return posterUrl; }
    public String getTrailerUrl() { return trailerUrl; }
    public String getDescription() { return description; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setStatus(String status) { this.status = status; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setTrailerUrl(String trailerUrl) { this.trailerUrl = trailerUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public String getDurationFormatted() {
        return duration + " phút";
    }

    public boolean hasTrailer() {
        return trailerUrl != null && !trailerUrl.isEmpty();
    }

    public boolean hasPoster() {
        return posterUrl != null && !posterUrl.isEmpty();
    }

    public void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", releaseDate='" + releaseDate + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}