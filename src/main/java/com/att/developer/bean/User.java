package com.att.developer.bean;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {
	@Id
	private String id;
	private String login;
	private String password;
	@Column(name = "last_updated")
	private Date lastUpdated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Instant getLastUpdated() {
		return lastUpdated != null ? lastUpdated.toInstant() : null;
	}

	public void setLastUpdated(Instant lastUpdated) {
		if(lastUpdated != null){
			this.lastUpdated = Date.from(lastUpdated);
		}
		else{
			this.lastUpdated = null;
		}
	}

}
