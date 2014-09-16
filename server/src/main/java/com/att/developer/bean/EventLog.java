package com.att.developer.bean;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.att.developer.typelist.ActorType;
import com.att.developer.typelist.EventType;

@Entity
@Table(name = "event_log")
public class EventLog implements Serializable {

	private static final long serialVersionUID = 5066708802311155518L;

	@Id
	private String id;

	@Column(name = "actor_id")
	private String actorId;

	@Column(name = "impacted_user_id")
	private String impactedUserId;

	@Column(name = "org_id")
	private String orgId;

	@Column(name = "event_type")
	private Integer eventType;

	private String info;

	@Column(name = "actor_type")
	private Integer actorType;

	@Column(name = "transaction_id")
	private String transactionId;

	@Column(name = "created_on")
	private Date createdOn;

	public EventLog() {
		setId(java.util.UUID.randomUUID().toString());
	}

	public EventLog(String actorId, String impactedUserId, String orgId,
			EventType eventType, String info, ActorType actorType,
			String transactionId) {
		this();
		this.actorId = actorId;
		this.impactedUserId = impactedUserId;
		this.orgId = orgId;
		if (eventType != null)
			this.eventType = eventType.getId();
		this.info = info;
		if (actorType != null)
			this.actorType = actorType.getId();
		this.transactionId = transactionId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getImpactedUserId() {
		return impactedUserId;
	}

	public void setImpactedUserId(String impactedUserId) {
		this.impactedUserId = impactedUserId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public EventType getEventType() {
		return EventType.getEnumValue(eventType);
	}

	public void setEventType(EventType eventType) {
		if (eventType != null)
			this.eventType = eventType.getId();
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getActorType() {
		return actorType;
	}

	public void setActorType(ActorType actorType) {
		if (actorType != null)
			this.actorType = actorType.getId();
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Instant getCreatedOn() {
		return createdOn != null ? createdOn.toInstant() : null;
	}

	public void setCreatedOn(Instant created) {
		if (createdOn != null) {
			this.createdOn = Date.from(created);
		} else {
			this.createdOn = null;
		}
	}

	public String toString() {
		return new ToStringBuilder(this).append("orgId", this.orgId)
				.append("impactedUserId", this.impactedUserId)
				.append("actorId", this.actorId)
				.append("actorType", this.actorType)
				.append("eventId", this.eventType).append("info", this.info)
				.append("transactionId", this.transactionId)
				.append("createdOn", this.createdOn).append("id", this.id)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EventLog other = (EventLog) obj;
		return Objects.equals(id, other.id);
	}

}
