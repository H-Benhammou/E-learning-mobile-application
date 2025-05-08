package com.example.projet_dev_mobile;

public class Comment {
    private String userId;
    private String username;
    private String comment;
    private float rating;
    private long timestamp;

    public Comment() {}

    public Comment(String userId, String username, String comment, float rating, long timestamp) {
        this.userId = userId;
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}