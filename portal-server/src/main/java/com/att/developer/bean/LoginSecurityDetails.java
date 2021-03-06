package com.att.developer.bean;

public class LoginSecurityDetails {

    private boolean isAccountLocked = false;
    private boolean isWarnAccountLock = false;
    private String errorMessage;
    private int numberOfInvalidLoginAttempts;
    private User user;

    
    public LoginSecurityDetails() {
    }

    public LoginSecurityDetails(boolean isAccountLocked,
			boolean isWarnAccountLock, String errorMessage,
			int numberOfInvalidLoginAttempts, User user) {
		super();
		this.isAccountLocked = isAccountLocked;
		this.isWarnAccountLock = isWarnAccountLock;
		this.errorMessage = errorMessage;
		this.numberOfInvalidLoginAttempts = numberOfInvalidLoginAttempts;
		this.user = user;
	}

	public int getNumberOfInvalidLoginAttempts() {
        return numberOfInvalidLoginAttempts;
    }
    
    public void setNumberOfInvalidLoginAttempts(int numberOfInvalidLoginAttempts) {
        this.numberOfInvalidLoginAttempts = numberOfInvalidLoginAttempts;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAccountLocked() {
        return isAccountLocked;
    }
    
    public void setAccountLocked(boolean isAccountLocked) {
        this.isAccountLocked = isAccountLocked;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isWarnAccountLock() {
        return isWarnAccountLock;
    }

    public void setWarnAccountLock(boolean isWarnAccountLock) {
        this.isWarnAccountLock = isWarnAccountLock;
    }
    
}
