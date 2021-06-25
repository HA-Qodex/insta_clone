package com.example.instagram.models;

public class NotificationData {

    private String userId, postId, text, isPost;

    public NotificationData(String userId, String postId, String text, String isPost) {
        this.userId = userId;
        this.postId = postId;
        this.text = text;
        this.isPost = isPost;
    }

    public NotificationData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIsPost() {
        return isPost;
    }

    public void setIsPost(String isPost) {
        this.isPost = isPost;
    }
}
