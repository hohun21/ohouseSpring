package com.ohouse.web.domain.seller;

import java.util.Map;
import lombok.Data;

@Data
public class SellerSignupRequest {
    private String emailId, emailDomain, password, passwordConfirm, brandName;
    private String representativeName, businessNumber, mailOrderNumber;
    private String businessAddrLine1, businessAddrLine2, representativeContact, customerServicePhone;
    private String agreement;

    public String getEmail() { return emailId.trim() + "@" + emailDomain.trim(); }
    public String getBusinessAddress() { return businessAddrLine1.trim() + ", " + businessAddrLine2.trim(); }

    public void validate(Map<String, Boolean> errors) {
        required(errors, "emailId", emailId); required(errors, "emailDomain", emailDomain);
        required(errors, "password", password); required(errors, "passwordConfirm", passwordConfirm);
        required(errors, "brandName", brandName); required(errors, "representativeName", representativeName);
        required(errors, "businessNumber", businessNumber); required(errors, "mailOrderNumber", mailOrderNumber);
        required(errors, "businessAddrLine1", businessAddrLine1); required(errors, "businessAddrLine2", businessAddrLine2);
        required(errors, "representativeContact", representativeContact); required(errors, "customerServicePhone", customerServicePhone);
        required(errors, "agreement", agreement);
        if (errors.isEmpty() || !errors.containsKey("emailId") && !errors.containsKey("emailDomain")) {
            if (!getEmail().matches("[A-Za-z0-9_-]+(?:\\.[A-Za-z0-9_-]+)*@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+")) errors.put("invalidEmail", true);
        }
        if (password != null && !password.matches("^(?=.*[A-Za-z])(?=.*[0-9])[\\x21-\\x7E]{8,20}$")) errors.put("invalidPassword", true);
        if (password != null && !password.equals(passwordConfirm)) errors.put("notMatch", true);
        if (businessNumber != null && !businessNumber.matches("[0-9]{10}")) errors.put("invalidBusinessNumber", true);
        if (mailOrderNumber != null && !mailOrderNumber.matches("[0-9]{4}-[가-힣A-Za-z0-9]+-[가-힣A-Za-z0-9]+")) errors.put("invalidMailOrderNumber", true);
    }
    private void required(Map<String, Boolean> errors, String key, String value) {
        if (value == null || value.trim().isEmpty()) errors.put(key, true);
    }
}
