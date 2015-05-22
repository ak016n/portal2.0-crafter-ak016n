package com.att.developer.bean;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ServerSideError {

	private String id;
	private String message;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static class Builder {
		private String id;
		private String message;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public ServerSideError build() {
			return new ServerSideError(this);
		}
	}

	private ServerSideError(Builder builder) {
		this.id = builder.id;
		this.message = builder.message;
	}

	public boolean equals(Object object) {
		if (!(object instanceof ServerSideError)) {
			return false;
		}
		ServerSideError rhs = (ServerSideError) object;
		return new EqualsBuilder()
				.append(this.id, rhs.id).append(this.message, rhs.message)
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(2086184833, -49553065)
				.append(this.id)
				.append(this.message).toHashCode();
	}

	
}
