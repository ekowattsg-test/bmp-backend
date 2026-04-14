package com.hcteol.jwt.backend.dtos;

public class SecurePasswordChangeDto {
    private Long requestingUserId;
    private char[] requestingUserPassword;
    private Long targetUserId;
    private char[] newPassword;
    
    public Long getRequestingUserId() {
        return requestingUserId;
    }
    
    public void setRequestingUserId(Long requestingUserId) {
        this.requestingUserId = requestingUserId;
    }
    
    public char[] getRequestingUserPassword() {
        return requestingUserPassword;
    }
    
    public void setRequestingUserPassword(char[] requestingUserPassword) {
        this.requestingUserPassword = requestingUserPassword;
    }
    
    public Long getTargetUserId() {
        return targetUserId;
    }
    
    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
    
    public char[] getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(char[] newPassword) {
        this.newPassword = newPassword;
    }
}
