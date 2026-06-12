package com.drp.dto.response;

import com.drp.entity.Resource;

import java.time.LocalDateTime;

public class ResourceResponse {

    private Long id;
    private String title;
    private String description;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String tags;
    private Long uploadedById;
    private String uploadedByUsername;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ResourceResponse from(Resource resource) {
        ResourceResponse response = new ResourceResponse();
        response.id = resource.getId();
        response.title = resource.getTitle();
        response.description = resource.getDescription();
        response.fileName = resource.getFileName();
        response.fileType = resource.getFileType();
        response.fileSize = resource.getFileSize();
        response.tags = resource.getTags();
        response.uploadedById = resource.getUploadedBy().getId();
        response.uploadedByUsername = resource.getUploadedBy().getUsername();
        if (resource.getCategory() != null) {
            response.categoryId = resource.getCategory().getId();
            response.categoryName = resource.getCategory().getName();
        }
        response.createdAt = resource.getCreatedAt();
        response.updatedAt = resource.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Long getUploadedById() {
        return uploadedById;
    }

    public void setUploadedById(Long uploadedById) {
        this.uploadedById = uploadedById;
    }

    public String getUploadedByUsername() {
        return uploadedByUsername;
    }

    public void setUploadedByUsername(String uploadedByUsername) {
        this.uploadedByUsername = uploadedByUsername;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
