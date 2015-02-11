package com.att.developer.bean;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.att.developer.typelist.UserStateType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
public class User implements Serializable {

    private static final long serialVersionUID = -4130170797253136478L;

    @Id
    private String id;
    private String login;
    @JsonIgnore
    @Column(name = "password")
    private String encryptedPassword;

	@Transient
    @JsonIgnore
    private String password;

    private String email;
    
    // Marking as transient as DB doesn't have the column yet, but we need it on the REST service
    @Transient
    private String firstName;

    // Marking as transient as DB doesn't have the column yet, but we need it on the REST service
    @Transient
    private String lastName;

    
    @Column(name = "last_updated")
    private Date lastUpdated;

    // TODO: temporarily fetch eagerly. What do we want to do here?
    @OrderColumn(name = "sequence_number")
    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "users", fetch = FetchType.EAGER)
    private List<Organization> organizations;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Set<UserState> userStates;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role_relationship", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    public User() {
        this.setId(java.util.UUID.randomUUID().toString());
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
    
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        if (lastUpdated != null) {
            this.lastUpdated = Date.from(lastUpdated);
        } else {
            this.lastUpdated = null;
        }
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public void removeOrganization(Organization organization) {
        if (organizations != null && !organizations.isEmpty()) {
            organizations.remove(organization);
        }
    }

    public void addOrganization(Organization organization) {
        if (organizations == null) {
            organizations = new ArrayList<>();
        }
        organizations.add(organization);
    }

    /**
     * Simple logic to encapsulate rules around what the 'default' organization
     * is.
     * 
     * @return organization in the zeroth position
     */
    public Organization getDefaultOrganization() {
        if (organizations != null && organizations.size() > 0) {
            return organizations.get(0);
        } else {
            return null;
        }
    }

    public Set<UserState> getUserStates() {
        return userStates;
    }

    public void setUserStates(Set<UserState> userStates) {
        this.userStates = userStates;
    }

    public boolean hasUserState(UserStateType userStateType) {
        boolean status = false;
        for (UserState userState : userStates) {
            if (userState.getState().equals(userStateType)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(final Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public void removeRole(Role role) {
        if (roles != null) {
            roles.remove(role);
        }
    }
	
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("organizations", this.organizations).append("login", this.login)
                .append("encryptedPassword", this.encryptedPassword).append("lastUpdated", this.lastUpdated)
                .append("id", this.id).append("password", this.password).toString();
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

        User other = (User) obj;
        return Objects.equals(this.getId(), other.getId());
    }

}
