package com.att.developer.bean;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.att.developer.typelist.OrgRelationshipType;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="organization", uniqueConstraints= @UniqueConstraint(columnNames = {"name"}))
public class Organization implements Serializable {
	
    private static final long serialVersionUID = 2117541682366680664L;

    @Id
    private String id;
    private String name;
    private String description;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "relationship_type")
    private Integer relationshipType;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "org_id")
    private Set<OrganizationState> organizationStates;

    @Column(name = "created_on", insertable = false, updatable = false)
    private Date createdOn;
    
	@Column(name = "last_updated", insertable = false)
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
        this.relationshipType = (relationshipType != null) ? relationshipType.getId() : null;
    }

	public Instant getCreatedOn() {
		return this.createdOn != null ? createdOn.toInstant() : null;
	}

	public void setCreatedOn(Instant created) {
		this.createdOn = Date.from(created);
	}

	public Instant getLastUpdated() {
		return this.lastUpdated != null ? lastUpdated.toInstant() : null;
	}

	public void setLastUpdated(Instant lastUpdated) {
		this.lastUpdated = Date.from(lastUpdated);
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

	
    @Transient
    @JsonBackReference("fromUser")
    public User getOrganizationAdmin() {
        if (users != null) {
            for (User user : users) {
                if (user.getRoles() != null) {
                    for (Role role : user.getRoles()) {
                        if (Role.ROLE_NAME_ORG_ADMIN.equals(role.getName())) {
                            return user;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this)
	    .append("id", this.id)
	    .append("name", this.name)
	    .append("parentId", this.parentId)
	    .append("description", this.description)
	    .append("relationshipType", this.getRelationshipType())
	    .append("organizationStates", this.getOrganizationStates())
	    //skip users
	    .append("createdOn", this.createdOn)
	    .append("lastUpdated", this.lastUpdated)
	    .toString();
	}

	
    @Override
    public int hashCode() {
        return Objects.hash(id);
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

        Organization other = (Organization) obj;
        return Objects.equals(this.getId(), other.getId());
    }
	
	
}
