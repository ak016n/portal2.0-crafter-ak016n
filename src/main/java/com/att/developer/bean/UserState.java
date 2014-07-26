package com.att.developer.bean;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.att.developer.typelist.UserStateType;

@Entity
@Table(name="state")
public class UserState implements Serializable {
	
	
	private static final long serialVersionUID = 6665638242150942277L;

	
	@Id
	private String id;
	
	@Column(name="state_id")
	private Integer state;

	@Column(name="user_id")
    private String userId;
	
	public UserState() {
		this.setId(java.util.UUID.randomUUID().toString());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserStateType getState() {
		return UserStateType.getEnumValue(state);
	}

	public void setState(UserStateType stateType) {
		this.state = (stateType != null)? stateType.getId() : null;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("state", this.state)
			.append("userId", this.userId).append("id", this.id).toString();
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		UserState other = (UserState) obj;
		return Objects.equals(this.id, other.id);
	}

}


