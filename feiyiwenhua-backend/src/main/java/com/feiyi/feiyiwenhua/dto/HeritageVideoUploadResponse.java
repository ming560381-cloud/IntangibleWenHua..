package com.feiyi.feiyiwenhua.dto;

public class HeritageVideoUploadResponse {
    private Long heritageId;
    private String videoUrl;
    private String objectName;
    private String message;

    public HeritageVideoUploadResponse() {
    }

    public HeritageVideoUploadResponse(Long heritageId, String videoUrl, String objectName, String message) {
        this.heritageId = heritageId;
        this.videoUrl = videoUrl;
        this.objectName = objectName;
        this.message = message;
    }

    public Long getHeritageId() {
        return heritageId;
    }

    public void setHeritageId(Long heritageId) {
        this.heritageId = heritageId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
