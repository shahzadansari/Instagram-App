package com.example.instagram_app.model;

public class Like {

    private String user_id;

    public Like() {
    }

    public Like(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Like{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
