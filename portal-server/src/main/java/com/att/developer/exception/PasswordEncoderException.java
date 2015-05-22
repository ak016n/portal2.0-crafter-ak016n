package com.att.developer.exception;

public class PasswordEncoderException extends RuntimeException {
	private static final long serialVersionUID = 8293340772491711621L;

	public PasswordEncoderException(Throwable e) {
        super(e);
    }

    public PasswordEncoderException(String msg) {
        super(msg);
    }

    public PasswordEncoderException(String msg, Throwable e) {
        super(msg, e);
    }
}
