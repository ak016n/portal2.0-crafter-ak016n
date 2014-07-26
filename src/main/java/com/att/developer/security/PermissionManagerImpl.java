package com.att.developer.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.att.developer.bean.Organization;
import com.att.developer.bean.User;
import com.att.developer.service.OrganizationService;
import com.att.developer.service.UserService;


@Service
@Transactional
public class PermissionManagerImpl implements PermissionManager {

	
	private final Logger logger = LogManager.getLogger();
	
	
	@Autowired
	private PlatformTransactionManager txManager;
	
	
    @Autowired
    private MutableAclService mutableAclService;
    
    @Autowired
    private TransactionTemplate transactionTemplate;
    
	@Autowired
	private DataSource dataSource;
	
	@Autowired
    private JdbcTemplate template;
	
	@Resource
	private OrganizationService orgService;
	
	@Resource
	private UserService userService;
    
	
	@Override
	public void createAcl(Class<?> type, Serializable identifier){
    	logger.debug("creating an ACL for this objectIdentity id (a.k.a. primary key) ********************* " + identifier);
		ObjectIdentity objId = new ObjectIdentityImpl(type, identifier);
		transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction(TransactionStatus arg0) {
				mutableAclService.createAcl(objId);
				return null;
			}
		});
	}
    
    
    @Override
	public void grantPermissions(Class<?> type, String identifier,  User user, Permission permission) {
    	//TODO: turn on 'strict' checking flag to disable this assert. 
    	//load User to make sure it really exists in database
    	Assert.notNull(userService.getUser(user), "user passed in is not found in our database. id : " + user.getId());
    	
        this.grantPermissions(type, identifier, new PrincipalSid(user.getId()), permission);
    }
    
    @Override
	public void grantPermissions(Class<?> type, String identifier, Organization org, Permission permission) {
    	//TODO: turn on 'strict' checking flag to disable this assert. 
    	//load Organization to make sure it really exists in database
    	Assert.notNull(orgService.getOrganization(org), "organization passed in is not found in our database. id : " + org.getId());
    	this.grantPermissions(type, identifier, new GrantedAuthoritySid(org.getId()), permission);
    }
    
    
    private void grantPermissions(Class<?> type, String identifier, Sid sid, Permission permission) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
        for(AccessControlEntry ace : acl.getEntries()){
        	if(ace.getSid().equals(sid) && ace.getPermission().equals(permission)){
        		logger.info("trying to create a duplicate ace entry, don't bother");
        		return;
        	}
        }
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        updateAclInTransaction(acl);
    }
    
    

    @Override
	public void changeOwner(Class<?> type, String identifier, User newOwnerUser) {
    	this.changeOwner(type, identifier, new PrincipalSid(newOwnerUser.getId()));
    }
    

    @Override
	public void changeOwner(Class<?> type, String identifier, Organization newOwningOrg) {
    	this.changeOwner(type, identifier, new GrantedAuthoritySid(newOwningOrg.getId()));
    }

    
    private void changeOwner(Class<?> type, String identifier, Sid newOwner) {
    	AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
        acl.setOwner(newOwner);
        updateAclInTransaction(acl);
    }
    
    
	@Override
	public void deletePermissionsForObject(Class<?> type, String identifier){
		logger.info("deleting this objectIdentity id (a.k.a. primary key) ********************* " + identifier);
		ObjectIdentity objId = new ObjectIdentityImpl(type, identifier);
		transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction(TransactionStatus arg0) {
				mutableAclService.deleteAcl(objId, false);
				return null;
			}
		});
	}
	

	@Override
	public void removeAllPermissionForObject(Class<?> type, String identifier, Organization org){
		ObjectIdentity objId = new ObjectIdentityImpl(type, identifier);
		List<Sid> sids = new ArrayList<>();
		Sid grantedAuthoritySid = new GrantedAuthoritySid(org.getId()); 
		sids.add(grantedAuthoritySid);
		AclImpl acl = (AclImpl)mutableAclService.readAclById(objId, sids);
		List<AccessControlEntry> aces = acl.getEntries();
		List<Integer> locationOfMatches = new ArrayList<>();
		int location = 0;
		int acesSize = aces.size()-1;
		for(int i = acesSize; i >= 0; i--){
			AccessControlEntry ace = aces.get(i);
			if(ace.getSid().equals(grantedAuthoritySid)){
				acl.deleteAce(i);
			}
		}
		
//		for(AccessControlEntry ace : aces){
//			if(ace.getSid().equals(grantedAuthoritySid)){
//				locationOfMatches.add(location++);
//				
//			}
//		}
		

		
//		for(int i = aces.size(); i>0; i--){
//			acl.deleteAce(0);
//		}
		
		updateAclInTransaction(acl);
	}
	
	
	
	/**
	 * 
	 * @param type
	 * @param identifier
	 * @return null if there is no Acl entry.
	 */
	@Override
	public List<AccessControlEntry> getAccessControlEntries(Class<?> type, String identifier){
		try{
			Acl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
			return acl.getEntries();
		}
		catch(NotFoundException e){
			//no entry, warn and return null;
			logger.warn("No Acl entry for type {}, identifier {} ", type, identifier);
			return null;
		}
	}
	
	@Override
	public void createAclWithPermissionsAndOwner(Class<?> type, String identifier, User ownerAndPermissionHolder, Permission permission){
		PrincipalSid sid = new PrincipalSid(ownerAndPermissionHolder.getId());
		this.createAclWithPermissionsAndOwner(type, identifier, sid, permission, sid);
	}
	
	@Override
	public void createAclWithPermissionsAndOwner(Class<?> type, String identifier, Organization ownerAndPermissionHolder, Permission permission){
		PrincipalSid sid = new PrincipalSid(ownerAndPermissionHolder.getId());
		this.createAclWithPermissionsAndOwner(type, identifier, sid, permission, sid);
	}
	
	
	private void createAclWithPermissionsAndOwner(Class<?> type, String identifier, Sid owner, Permission permission, Sid permissionRecipient){
		this.createAcl(type, identifier);
		this.grantPermissions(type, identifier, permissionRecipient, permission);
		this.changeOwner(type, identifier, owner);
	}
	
	
	private void updateAclInTransaction(final MutableAcl acl) {
		transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction(TransactionStatus arg0) {
				mutableAclService.updateAcl(acl);
				return null;
				
			}
		});
	}
	
	
	public void setTransactionTemplate(TransactionTemplate tTemplate){
		this.transactionTemplate = tTemplate;
	}


	public void setMutableAclService(MutableAclService svc) {
		this.mutableAclService = svc;
	}
	
	
}