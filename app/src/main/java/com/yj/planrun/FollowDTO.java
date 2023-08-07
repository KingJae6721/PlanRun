package com.yj.planrun;

import java.util.HashMap;
import java.util.Map;

public class FollowDTO {
    private int followerCount = 0;
    private int followingCount = 0;
    private Map<String, Boolean> followers = new HashMap<>();
    private Map<String, Boolean> followings = new HashMap<>();
    private HashMap<String, String> followingNicknames = new HashMap<>();
    private HashMap<String, String> followerNicknames = new HashMap<>();


    public FollowDTO() {
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public Map<String, Boolean> getFollowers() {
        return followers;
    }

    public void setFollowers(Map<String, Boolean> followers) {
        this.followers = followers;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public Map<String, Boolean> getFollowings() {
        return followings;
    }

    public void setFollowings(Map<String, Boolean> followings) {
        this.followings = followings;
    }

    public HashMap<String, String> getFollowingNicknames() {
        return followingNicknames;
    }

    public void setFollowingNicknames(HashMap<String, String> followingNicknames) {
        this.followingNicknames = followingNicknames;
    }

    public HashMap<String, String> getFollowerNicknames() {
        return followerNicknames;
    }

    public void setFollowerNicknames(HashMap<String, String> followerNicknames) {
        this.followerNicknames = followerNicknames;
    }
}