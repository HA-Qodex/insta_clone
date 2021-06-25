package com.example.instagram.models;

public class CommentData {

    String userId, comment;

    public CommentData(String userId, String comment) {
        this.userId = userId;
        this.comment = comment;
    }

    public CommentData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
