package com.example.instagram.models;

public class UserData {

    String name, email, phone, userId, profilePic, bio, userName;

    public UserData(String name, String email, String phone, String userId, String profilePic, String bio, String userName) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
        this.profilePic = profilePic;
        this.bio = bio;
        this.userName = userName;
    }

    public UserData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
