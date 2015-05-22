package com.att.developer.bean.blog;

public class BlogError {

	private String code;
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "BlogError [code=" + code + ", message=" + message + "]";
	}

}
