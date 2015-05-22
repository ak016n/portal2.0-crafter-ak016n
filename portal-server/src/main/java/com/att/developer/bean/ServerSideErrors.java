package com.att.developer.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerSideErrors {
	
	@JsonIgnore
	private HttpStatus httpStatus =  HttpStatus.BAD_REQUEST;

	@JsonProperty("errors")
	private List<ServerSideError> errorColl = new ArrayList<ServerSideError>();

	public List<ServerSideError> getErrorColl() {
		return errorColl;
	}

	public void setErrorColl(List<ServerSideError> errorColl) {
		this.errorColl = errorColl;
	}
	
	public ServerSideErrors add(ServerSideError error) {
		errorColl.add(error);
		return this;
	}

	public HttpStatus getHttpStatus() {
		return  httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	
}
