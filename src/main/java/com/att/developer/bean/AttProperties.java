package com.att.developer.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
@NamedQueries({
	@NamedQuery(name="AttProperties.getIKWildCard", query="select distinct a.itemKey from AttProperties as a where a.itemKey LIKE :itemKey ORDER BY a.itemKey ASC"), 
	@NamedQuery(name="AttProperties.getIKFKWildCard", query="select distinct a.fieldKey from AttProperties as a where a.itemKey = :itemKey and a.fieldKey LIKE :fieldKey ORDER BY a.fieldKey ASC")
})
@Entity
@Table(name = "att_properties")
public class AttProperties {

	public AttProperties() {
		this.setId(java.util.UUID.randomUUID().toString());
		this.setDateCreated(new Date());
	}

	public AttProperties(String itemKey, String fieldKey,
			String description, int version) {
		this();
		this.itemKey = itemKey;
		this.fieldKey = fieldKey;
		this.description = description;
		this.version = version;
	}

	@Id
	private String id;
	
	@NotNull
	@Column(name = "item_key")
	private String itemKey;
	
	@NotNull
	@Column(name = "field_key")
	private String fieldKey;
	
	private String description;
	
	private int version;
	
	@Column(name = "date_created")
	private Date dateCreated;
	
	@Column(name = "is_deleted")
	private boolean deleted;

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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public String toString() {
		return new ToStringBuilder(this).append("version", this.version)
				.append("description", this.description)
				.append("fieldKey", this.fieldKey)
				.append("dateCreated", this.dateCreated)
				.append("itemKey", this.itemKey).append("id", this.id)
				.append("deleted", this.deleted)
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
