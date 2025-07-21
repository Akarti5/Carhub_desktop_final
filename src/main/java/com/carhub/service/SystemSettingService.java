package com.carhub.service;

import com.carhub.entity.SystemSetting;
import com.carhub.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SystemSettingService {
    
    @Autowired
    private SystemSettingRepository systemSettingRepository;
    
    public String getSettingValue(String key, String defaultValue) {
        Optional<SystemSetting> setting = systemSettingRepository.findBySettingKey(key);
        return setting.map(SystemSetting::getSettingValue).orElse(defaultValue);
    }
    
    public Integer getIntegerSetting(String key, Integer defaultValue) {
        String value = getSettingValue(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public BigDecimal getDecimalSetting(String key, BigDecimal defaultValue) {
        String value = getSettingValue(key, defaultValue.toString());
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public Boolean getBooleanSetting(String key, Boolean defaultValue) {
        String value = getSettingValue(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
    
    public void updateSetting(String key, String value) {
        Optional<SystemSetting> settingOpt = systemSettingRepository.findBySettingKey(key);
        if (settingOpt.isPresent()) {
            SystemSetting setting = settingOpt.get();
            setting.setSettingValue(value);
            systemSettingRepository.save(setting);
        } else {
            SystemSetting newSetting = new SystemSetting(key, value, SystemSetting.SettingType.STRING);
            systemSettingRepository.save(newSetting);
        }
    }
    
    public void createSetting(String key, String value, SystemSetting.SettingType type, String description) {
        if (!systemSettingRepository.existsBySettingKey(key)) {
            SystemSetting setting = new SystemSetting(key, value, type);
            setting.setDescription(description);
            systemSettingRepository.save(setting);
        }
    }
    
    public List<SystemSetting> getAllSettings() {
        return systemSettingRepository.findAll();
    }
    
    public List<SystemSetting> getEditableSettings() {
        return systemSettingRepository.findByIsEditableTrue();
    }
    
    public void initializeDefaultSettings() {
        createSetting("company_name", "CarHub", SystemSetting.SettingType.STRING, "Company name for branding");
        createSetting("company_address", "123 Business Street, Antananarivo, Madagascar", SystemSetting.SettingType.STRING, "Company address");
        createSetting("company_phone", "+261-20-123-4567", SystemSetting.SettingType.STRING, "Company phone number");
        createSetting("company_email", "info@carhub.com", SystemSetting.SettingType.STRING, "Company email address");
        createSetting("tax_rate", "20.0", SystemSetting.SettingType.DECIMAL, "Default tax rate percentage");
        createSetting("currency", "MGA", SystemSetting.SettingType.STRING, "Default currency");
        createSetting("invoice_prefix", "INV", SystemSetting.SettingType.STRING, "Invoice number prefix");
        createSetting("warranty_months", "12", SystemSetting.SettingType.INTEGER, "Default warranty period in months");
    }
}
