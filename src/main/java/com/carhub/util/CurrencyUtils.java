package com.carhub.util;

import com.carhub.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class CurrencyUtils {

    private static SystemSettingService systemSettingService;
    private static final String DEFAULT_CURRENCY = "MGA";

    @Autowired
    public CurrencyUtils(SystemSettingService systemSettingService) {
        CurrencyUtils.systemSettingService = systemSettingService;
    }

    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        
        // Get the currency code from system settings (default to MGA)
        String currencyCode = getCurrencyCode();
        
        // For Malagasy Ariary, use custom formatting
        if (DEFAULT_CURRENCY.equalsIgnoreCase(currencyCode)) {
            // Format with 0 decimal places and "Ar" prefix
            return String.format("Ar %,.0f", amount.doubleValue());
        }
        
        // For other currencies, use default locale formatting
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(amount);
    }

    public static String getCurrencySymbol() {
        String currencyCode = getCurrencyCode();
        
        if (DEFAULT_CURRENCY.equalsIgnoreCase(currencyCode)) {
            return "Ar";
        }
        
        // For other currencies, return the default currency symbol
        return NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
    }
    
    private static String getCurrencyCode() {
        try {
            return systemSettingService.getSettingValue("currency", DEFAULT_CURRENCY);
        } catch (Exception e) {
            return DEFAULT_CURRENCY;
        }
    }
}
