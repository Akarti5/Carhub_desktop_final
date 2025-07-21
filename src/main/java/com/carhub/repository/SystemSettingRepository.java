package com.carhub.repository;

import com.carhub.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    
    Optional<SystemSetting> findBySettingKey(String settingKey);
    
    List<SystemSetting> findByIsEditableTrue();
    
    List<SystemSetting> findBySettingType(SystemSetting.SettingType settingType);
    
    boolean existsBySettingKey(String settingKey);
}
