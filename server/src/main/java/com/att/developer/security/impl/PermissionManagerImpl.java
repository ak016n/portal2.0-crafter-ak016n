package com.att.developer.security.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.att.developer.bean.Organization;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.security.PermissionManager;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.OrganizationService;
import com.att.developer.service.UserService;

@Service
public class PermissionManagerImpl implements PermissionManager {

    private final Logger logger = LogManager.getLogger();

    private MutableAclService mutableAclService;

    private OrganizationService organizationService;

    private UserService userService;
    
    private GlobalScopedParamService globalScopedParamService;

    @Autowired
    public PermissionManagerImpl(MutableAclService mutualAclService, OrganizationService organizationService, UserService userService, GlobalScopedParamService globalScopedParamService) {
        this.mutableAclService = mutualAclService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.globalScopedParamService = globalScopedParamService;
    }

    @Transactional
    public MutableAcl createAcl(Class<?> type, Serializable identifier) {
        logger.debug("creating an ACL for this objectIdentity id (a.k.a. primary key) ********************* " + identifier);
        ObjectIdentity objId = new ObjectIdentityImpl(type, identifier);
        return mutableAclService.createAcl(objId);
    }

    @Transactional
    public void grantPermissions(Class<?> type, String identifier, User user, Permission permission) {
        // load User to make sure it really exists in database
        if(isStrictChecking()){
            Assert.notNull(userService.getUser(user), "user passed in is not found in our database. id : " + user.getId());
        }

        this.grantPermissions(type, identifier, new PrincipalSid(user.getId()), permission);
    }

    @Transactional
    public void grantPermissions(Class<?> type, String identifier, Organization org, Permission permission) {
        // load Organization to make sure it really exists in database
        if(isStrictChecking()){
            Assert.notNull(organizationService.getOrganization(org), "organization passed in is not found in our database. id : "+ org.getId());
        }
        
        this.grantPermissions(type, identifier, new GrantedAuthoritySid(org.getId()), permission);
    }

    private void grantPermissions(Class<?> type, String identifier, Sid sid, Permission permission) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
        for (AccessControlEntry ace : acl.getEntries()) {
            if (ace.getSid().equals(sid) && ace.getPermission().equals(permission)) {
                logger.info("trying to create a duplicate ace entry, don't bother");
                return;
            }
        }
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        mutableAclService.updateAcl(acl);
    }

    @Transactional
    public void changeOwner(Class<?> type, String identifier, User newOwnerUser) {
        this.changeOwner(type, identifier, new PrincipalSid(newOwnerUser.getId()));
    }

    @Transactional
    public void changeOwner(Class<?> type, String identifier, Organization newOwningOrg) {
        this.changeOwner(type, identifier, new GrantedAuthoritySid(newOwningOrg.getId()));
    }

    private void changeOwner(Class<?> type, String identifier, Sid newOwner) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
        acl.setOwner(newOwner);
        mutableAclService.updateAcl(acl);
    }

    @Transactional
    public void deleteAllPermissionsForObject(Class<?> type, String identifier) {
        logger.info("deleting this objectIdentity id (a.k.a. primary key) ********************* " + identifier);
        ObjectIdentity objId = new ObjectIdentityImpl(type, identifier);
        mutableAclService.deleteAcl(objId, false);
    }

    @Transactional
    public void removeAllPermissionForObjectForOrganization(Class<?> type, String identifier, Organization org) {
        if(isStrictChecking()){
            Assert.notNull(organizationService.getOrganization(org), "organization passed in is not found in our database. id : "+ org.getId());
        }
        ObjectIdentity objId = new ObjectIdentityImpl(type, identifier);
        List<Sid> sids = new ArrayList<>();
        Sid grantedAuthoritySid = new GrantedAuthoritySid(org.getId());
        sids.add(grantedAuthoritySid);
        AclImpl acl = (AclImpl) mutableAclService.readAclById(objId, sids);
        List<AccessControlEntry> aces = acl.getEntries();
        int acesSize = aces.size() - 1;
        for (int i = acesSize; i >= 0; i--) {
            AccessControlEntry ace = aces.get(i);
            if (ace.getSid().equals(grantedAuthoritySid)) {
                acl.deleteAce(i);
            }
        }
        mutableAclService.updateAcl(acl);
    }

    /**
     * 
     * @param type
     * @param identifier
     * @return null if there is no Acl entry.
     */
    @Transactional
    public List<AccessControlEntry> getAccessControlEntries(Class<?> type, String identifier) {
        try {
            Acl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
            return acl.getEntries();
        } catch (NotFoundException e) {
            // no entry, warn and return null;
            logger.warn("No Acl entry for type {}, identifier {} ", type, identifier);
            return null;
        }
    }
    
	@Override
    public void denyPermissions(Class<?> type, String identifier, User user, Permission permission) {
        // load User to make sure it really exists in database
        if(isStrictChecking()){
            Assert.notNull(userService.getUser(user), "user passed in is not found in our database. id : " + user.getId());
        }

        this.denyPermissions(type, identifier, new PrincipalSid(user.getId()), permission);
    }
    
    private void denyPermissions(Class<?> type, String identifier, Sid sid, Permission permission) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
        for (AccessControlEntry ace : acl.getEntries()) {
            if (ace.getSid().equals(sid) && ace.getPermission().equals(permission)) {
                logger.info("trying to create a duplicate ace entry, don't bother");
                return;
            }
        }
        acl.insertAce(acl.getEntries().size(), permission, sid, false);
        mutableAclService.updateAcl(acl);
    }

    @Transactional
    public void createAclWithPermissionsAndOwner(Class<?> type, String identifier, User ownerAndPermissionHolder, Permission permission) {
        PrincipalSid sid = new PrincipalSid(ownerAndPermissionHolder.getId());
        this.createAclWithPermissionsAndOwner(type, identifier, sid, permission, sid);
    }

    @Transactional
    public void createAclWithPermissionsAndOwner(Class<?> type, String identifier, Organization ownerAndPermissionHolder, Permission permission) {
        PrincipalSid sid = new PrincipalSid(ownerAndPermissionHolder.getId());
        this.createAclWithPermissionsAndOwner(type, identifier, sid, permission, sid);
    }

    private void createAclWithPermissionsAndOwner(Class<?> type, String identifier, Sid owner, Permission permission, Sid permissionRecipient) {
        this.createAcl(type, identifier);
        this.grantPermissions(type, identifier, permissionRecipient, permission);
        this.changeOwner(type, identifier, owner);
    }
    
	@Override
	@Transactional
	public void createAclWithDenyPermissionsAndOwner(Class<?> type,	String identifier, User ownerAndPermissionHolder, Permission permission) {
		PrincipalSid sid = new PrincipalSid(ownerAndPermissionHolder.getId());
		createAclWithDenyPermissionsAndOwner(type, identifier, sid, permission, sid);
	}
	

	@Override
	@Transactional
	public void createAclWithDenyPermissionsAndOwner(Class<?> type,	String identifier, SessionUser ownerAndPermissionHolder, Permission permission) {
		PrincipalSid sid = new PrincipalSid(ownerAndPermissionHolder.getId());
		createAclWithDenyPermissionsAndOwner(type, identifier, sid, permission, sid);
	}

	@Override
	@Transactional
	public void createAclWithDenyPermissionsAndOwner(Class<?> type,	String identifier, Sid grantedAuthoritiesSid, Permission permission) {
		createAclWithDenyPermissionsAndOwner(type, identifier, grantedAuthoritiesSid, permission, grantedAuthoritiesSid);
	}
	
    private void createAclWithDenyPermissionsAndOwner(Class<?> type, String identifier, Sid owner, Permission permission, Sid permissionRecipient) {
        this.denyPermissions(type, identifier, permissionRecipient, permission);
    }
    
    @Override
    @Transactional
    public void createAclWithParents(Class<?> type, String identifier, Sid owner, Permission permission, Sid permissionRecipient, ObjectIdentity parentOI) {
        MutableAcl mutableAcl = this.createAcl(type, identifier);
        Acl parentAcl = mutableAclService.readAclById(parentOI);
        mutableAcl.setParent(parentAcl);
        mutableAclService.updateAcl(mutableAcl);
        this.grantPermissions(type, identifier, permissionRecipient, permission);
        this.changeOwner(type, identifier, owner);
    }

    private boolean isStrictChecking(){
        String aclStrictParameterChecking  = globalScopedParamService.get("aclStrictParameterChecking");
        return Boolean.TRUE.equals(Boolean.valueOf(aclStrictParameterChecking));    
    }
}