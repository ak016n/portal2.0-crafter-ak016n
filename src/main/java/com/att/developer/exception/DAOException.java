package com.att.developer.exception;

public class DAOException extends RuntimeException {

	private static final long serialVersionUID = 8293340772491711621L;

	public DAOException(Throwable e) {
        super(e);
    }

    public DAOException(String msg) {
        super(msg);
    }

    public DAOException(String msg, Throwable e) {
        super(msg, e);
    }
}