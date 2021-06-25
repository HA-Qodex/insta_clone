package com.example.instagram.models;

public class PostData {

    String description, imageUrl, postId, userId;

    public PostData(String description, String imageUrl, String postId, String userId) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.postId = postId;
        this.userId = userId;
    }

    public PostData() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
