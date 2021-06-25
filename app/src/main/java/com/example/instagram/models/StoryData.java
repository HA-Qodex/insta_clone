package com.example.instagram.models;

public class StoryData {

    private String userId, storyId, storyPic;
    private long startTime, endTime;

    public StoryData(String userId, String storyId, String storyPic, long startTime, long endTime) {
        this.userId = userId;
        this.storyId = storyId;
        this.storyPic = storyPic;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public StoryData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryPic() {
        return storyPic;
    }

    public void setStoryPic(String storyPic) {
        this.storyPic = storyPic;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
