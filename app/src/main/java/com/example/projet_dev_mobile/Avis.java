package com.example.projet_dev_mobile;

public class Avis {
    private String userName;
    private String comment;
    private double rating;

    public Avis(String userName, String comment, double rating) {
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
    }

    public String getUserName() { return userName; }
    public String getComment() { return comment; }
    public double getRating() { return rating; }
}
