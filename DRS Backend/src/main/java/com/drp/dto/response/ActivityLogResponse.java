package com.drp.dto.response;

import com.drp.entity.ActivityAction;
import com.drp.entity.ActivityLog;

import java.time.LocalDateTime;

public class ActivityLogResponse {

    private Long id;
    private Long userId;
    private String username;
    private ActivityAction action;
    private String entityType;
    private Long entityId;
    private String details;
    private LocalDateTime timestamp;

    public static ActivityLogResponse from(ActivityLog log) {
        ActivityLogResponse response = new ActivityLogResponse();
        response.id = log.getId();
        if (log.getUser() != null) {
            response.userId = log.getUser().getId();
            response.username = log.getUser().getUsername();
        }
        response.action = log.getAction();
        response.entityType = log.getEntityType();
        response.entityId = log.getEntityId();
        response.details = log.getDetails();
        response.timestamp = log.getTimestamp();
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ActivityAction getAction() {
        return action;
    }

    public void setAction(ActivityAction action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
