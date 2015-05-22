package com.att.developer.exception;

public class NonExistentUserException extends RuntimeException {
	private static final long serialVersionUID = 8293340772491711621L;

	public NonExistentUserException(Throwable e) {
        super(e);
    }

    public NonExistentUserException(String msg) {
        super(msg);
    }

    public NonExistentUserException(String msg, Throwable e) {
        super(msg, e);
    }
}
