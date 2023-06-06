package com.yj.planrun;

import java.util.Map;

public class ContentDTO {
    private String explain;
    private String imageUrl;
    private String uid;
    private String userId;
    private Long timestamp;
    private int favoriteCount;
    private Map<String, Boolean> favorites;
    private String documentId;
    public ContentDTO() {
        // 기본 생성자
    }

    public ContentDTO(String explain, String imageUrl, String uid, String userId, Long timestamp, int favoriteCount, Map<String, Boolean> favorites) {
        this.explain = explain;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.userId = userId;
        this.timestamp = timestamp;
        this.favoriteCount = favoriteCount;
        this.favorites = favorites;
    }

    public String getDocumentId() {
        return documentId;
    }
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Map<String, Boolean> getFavorites() {
        return favorites;
    }

    public void setFavorites(Map<String, Boolean> favorites) {
        this.favorites = favorites;
    }

    public static class Comment {
        private String uid;
        private String userId;
        private String comment;
        private Long timestamp;

        public Comment() {
            // 기본 생성자
        }

        public Comment(String uid, String userId, String comment, Long timestamp) {
            this.uid = uid;
            this.userId = userId;
            this.comment = comment;
            this.timestamp = timestamp;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
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

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
