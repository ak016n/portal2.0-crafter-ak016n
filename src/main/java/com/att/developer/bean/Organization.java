package com.att.developer.bean;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections4.CollectionUtils;

import com.att.developer.typelist.OrgRelationshipType;

@Entity
@Table(uniqueConstraints= @UniqueConstraint(columnNames = {"name"}))
public class Organization {
	
	@Id
    private String id;
	private String name;
	private String description;
	
	@Column(name="parent_id")
	private String parentId;
	
	@Column(name="relationship_type")
	private Integer relationshipType;

	@Column(name="created_on")
	private Date createdOn;
	
	@OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "org_id")
	private Set<OrganizationState> organizationStates;
	
	@Column(name="last_updated")
	private Date lastUpdated;
	
	@OrderColumn(name = "sequence_number")
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_org_membership", joinColumns = {@JoinColumn(name = "org_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
	private Set<User> users;

    public Organization() {
		this.setId(java.util.UUID.randomUUID().toString());
    }
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public OrgRelationshipType getRelationshipType() {
		return OrgRelationshipType.getEnumValue(relationshipType);
	}

	public void setRelationshipType(OrgRelationshipType relationshipType) {
		this.relationshipType = (relationshipType != null)? relationshipType.getId(): null;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
        if (this.users != null && (users == null || users.isEmpty())) {
            for (User user : this.users) {
                user.removeOrganization(this);
            }
            this.users = new HashSet<User>();
        } else if (this.users != null && users != null) {
            for (Object user : CollectionUtils.subtract(this.users, users)) {
                this.removeUser((User) user);
            }
            for (Object user : CollectionUtils.subtract(users, this.users)) {
                this.addUser((User) user);
            }
        }
        this.users = users;
	}
	
    public void addUser(final User user) {
        if (users == null) {
            users = new HashSet<User>();
        }
        users.add(user);
        user.addOrganization(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.removeOrganization(this);
    }
    
	public Set<OrganizationState> getOrganizationStates() {
		return organizationStates;
	}

	public void setOrganizationStates(Set<OrganizationState> organizationStates) {
		this.organizationStates = organizationStates;
	}
	
}
