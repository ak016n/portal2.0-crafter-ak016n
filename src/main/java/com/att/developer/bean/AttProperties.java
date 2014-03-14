package com.att.developer.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "att_properties")
public class AttProperties {

	public AttProperties() {
		this.setId(java.util.UUID.randomUUID().toString());
		this.setDateCreated(new Date());
	}

	@Id
	private String id;
	@Column(name = "item_key")
	private String itemKey;
	@Column(name = "field_key")
	private String fieldKey;
	private String description;
	private int version;
	@Column(name = "date_created")
	private Date dateCreated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public String getFieldKey() {
		return fieldKey;
	}

	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String toString() {
		return new ToStringBuilder(this).append("version", this.version)
				.append("description", this.description)
				.append("fieldKey", this.fieldKey)
				.append("dateCreated", this.dateCreated)
				.append("itemKey", this.itemKey).append("id", this.id)
				.toString();
	}

	public boolean equals(Object object) {
		if (!(object instanceof AttProperties)) {
			return false;
		}
		AttProperties rhs = (AttProperties) object;
		return new EqualsBuilder().append(this.itemKey, rhs.itemKey)
				.append(this.fieldKey, rhs.fieldKey)
				.append(this.version, rhs.version).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(-1057293819, 322548257).append(this.itemKey)
				.append(this.fieldKey).append(this.version).toHashCode();
	}

}
