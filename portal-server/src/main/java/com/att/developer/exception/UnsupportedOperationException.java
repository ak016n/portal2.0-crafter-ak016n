package com.att.developer.exception;

public class UnsupportedOperationException extends RuntimeException {

	private static final long serialVersionUID = 6297365836177027645L;

	public UnsupportedOperationException(Throwable e) {
        super(e);
    }

    public UnsupportedOperationException(String msg) {
        super(msg);
    }

    public UnsupportedOperationException(String msg, Throwable e) {
        super(msg, e);
    }
}
