package com.project.it_asset_tracker.dto;

import com.project.it_asset_tracker.entity.AssetStatus;
import java.time.LocalDateTime;

public class AssetDTO {

    private Long id;
    private String name;
    private String serialNumber;
    private AssetStatus status;
    private Long categoryId;
    private String categoryName;
    private Long assignedToUserId;
    private String assignedToUsername;
    private LocalDateTime createdAt;

    public AssetDTO() {
    }

    public AssetDTO(
            Long id,
            String name,
            String serialNumber,
            AssetStatus status,
            Long categoryId,
            String categoryName,
            Long assignedToUserId,
            String assignedToUsername,
            LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.assignedToUserId = assignedToUserId;
        this.assignedToUsername = assignedToUsername;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
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

    public Long getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public String getAssignedToUsername() {
        return assignedToUsername;
    }

    public void setAssignedToUsername(String assignedToUsername) {
        this.assignedToUsername = assignedToUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}