package com.att.developer.exception;

public class DuplicateDataException extends RuntimeException {
	private static final long serialVersionUID = 8293340772491711621L;

	public DuplicateDataException(Throwable e) {
        super(e);
    }

    public DuplicateDataException(String msg) {
        super(msg);
    }

    public DuplicateDataException(String msg, Throwable e) {
        super(msg, e);
    }
}
