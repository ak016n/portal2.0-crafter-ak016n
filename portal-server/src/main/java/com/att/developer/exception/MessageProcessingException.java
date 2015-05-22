package com.att.developer.exception;

public class MessageProcessingException extends Exception {

	private static final long serialVersionUID = 5400434973749910376L;

	public MessageProcessingException(Throwable e) {
        super(e);
    }

    public MessageProcessingException(String msg) {
        super(msg);
    }

    public MessageProcessingException(String msg, Throwable e) {
        super(msg, e);
    }
}