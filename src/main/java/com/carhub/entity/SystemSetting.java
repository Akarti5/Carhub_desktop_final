package com.carhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
public class SystemSetting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "setting_key", unique = true, nullable = false, length = 50)
    private String settingKey;
    
    @Column(name = "setting_value", nullable = false, columnDefinition = "TEXT")
    private String settingValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "setting_type", nullable = false)
    private SettingType settingType = SettingType.STRING;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_editable")
    private Boolean isEditable = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum SettingType {
        STRING, INTEGER, DECIMAL, BOOLEAN, JSON
    }
    
    // Constructors
    public SystemSetting() {}
    
    public SystemSetting(String settingKey, String settingValue, SettingType settingType) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.settingType = settingType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSettingKey() { return settingKey; }
    public void setSettingKey(String settingKey) { this.settingKey = settingKey; }
    
    public String getSettingValue() { return settingValue; }
    public void setSettingValue(String settingValue) { this.settingValue = settingValue; }
    
    public SettingType getSettingType() { return settingType; }
    public void setSettingType(SettingType settingType) { this.settingType = settingType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsEditable() { return isEditable; }
    public void setIsEditable(Boolean isEditable) { this.isEditable = isEditable; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return settingKey + " = " + settingValue;
    }
}
