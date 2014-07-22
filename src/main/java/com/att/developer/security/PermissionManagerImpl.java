package com.att.developer.security;

import java.io.Serializable;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


@Service
@Transactional
public class PermissionManagerImpl implements PermissionManager {

	private static final Logger logger = Logger.getLogger(PermissionManagerImpl.class);
	
	
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
    
	
	@Override
	public void createAcl(Class<?> type, Serializable identifier){
    	logger.info("creating an ACL for this objectIdentity id (a.k.a. primary key) ********************* " + identifier);
		ObjectIdentity objId = new ObjectIdentityImpl(type, identifier);
		transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction(TransactionStatus arg0) {
				mutableAclService.createAcl(objId);
				return null;
			}
		});
	}
    
    
    @Override
	public void grantPermissions(Class<?> type, String identifier,  String recipientUsername, Permission permission) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(recipientUsername), true);
        updateAclInTransaction(acl);
    }
    
    

    @Override
	public void changeOwner(Class<?> type, String identifier, String newOwnerUsername) {
    	AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(type, identifier));
        acl.setOwner(new PrincipalSid(newOwnerUsername));
        updateAclInTransaction(acl);
    }
    

    
	@Override
	public void deletePermissionsForObject(Class<?> type, String identifier){
		logger.info("deleting this objectIdentity id (a.k.a. primary key) ********************* " + identifier);
		mutableAclService.deleteAcl(new ObjectIdentityImpl(type, identifier), false);
	}
	

	@Override
	public void createAclWithPermissionsAndOwner(Class<?> type, String identifier, String ownerId, Permission permission, String permissionRecipient){
		this.createAcl(type, identifier);
		this.grantPermissions(type, identifier, permissionRecipient, permission);
		this.changeOwner(type, identifier, ownerId);
	}
	
	
	private void updateAclInTransaction(final MutableAcl acl) {
		transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction(TransactionStatus arg0) {
				mutableAclService.updateAcl(acl);
				return null;
				
			}
		});
	}

}