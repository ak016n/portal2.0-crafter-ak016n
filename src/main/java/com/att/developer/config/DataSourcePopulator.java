package com.att.developer.config;


import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.att.developer.bean.ApiBundle;



public class DataSourcePopulator {
    //~ Instance fields ================================================================================================

	private static final Logger logger = Logger.getLogger(DataSourcePopulator.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private PlatformTransactionManager txManager;
	
	
    private JdbcTemplate template;
    
    @Autowired
    private MutableAclService mutableAclService;
    
    
    private TransactionTemplate transactionTemplate;
    
    
    final String[] firstNames = {
            "Bob", "Mary", "James", "Jane", "Kristy", "Kirsty", "Kate", "Jeni", "Angela", "Melanie", "Kent", "William",
            "Geoff", "Jeff", "Adrian", "Amanda", "Lisa", "Elizabeth", "Prue", "Richard", "Darin", "Phillip", "Michael",
            "Belinda", "Samantha", "Brian", "Greg", "Matthew"
        };
    final String[] lastNames = {
            "Smith", "Williams", "Jackson", "Rictor", "Nelson", "Fitzgerald", "McAlpine", "Sutherland", "Abbott", "Hall",
            "Edwards", "Gates", "Black", "Brown", "Gray", "Marwell", "Booch", "Johnson", "McTaggart", "Parklin",
            "Findlay", "Robinson", "Giugni", "Lang", "Chi", "Carmichael"
        };


//    @PostConstruct
    @Transactional
    public void initialize() throws Exception {
    	
    	if(true) throw new Exception("***************************** don't initialize unintentionally! **************************************");
    	
    	this.template = new JdbcTemplate(dataSource);
    	this.transactionTemplate = new TransactionTemplate(txManager);
    	
    	
    	
        Assert.notNull(mutableAclService, "mutableAclService required");
        Assert.notNull(template, "dataSource required");
        Assert.notNull(transactionTemplate, "platformTransactionManager required");

//        // Set a user account that will initially own all the created data
        Authentication authRequest = new UsernamePasswordAuthenticationToken("rod", "koala", AuthorityUtils.createAuthorityList("ROLE_IGNORED"));
        SecurityContextHolder.getContext().setAuthentication(authRequest);

//        try {
//            template.execute("DROP TABLE CONTACTS");
//            template.execute("DROP TABLE AUTHORITIES");
//            template.execute("DROP TABLE USERS");
//            template.execute("DROP TABLE ACL_ENTRY");
//            template.execute("DROP TABLE ACL_OBJECT_IDENTITY");
//            template.execute("DROP TABLE ACL_CLASS");
//            template.execute("DROP TABLE ACL_SID");
//        } catch(Exception e) {
//            System.out.println("Failed to drop tables: " + e.getMessage());
//        }
//
//        template.execute(
//            "CREATE TABLE ACL_SID(" +
//                    "ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 100) NOT NULL PRIMARY KEY," +
//                    "PRINCIPAL BOOLEAN NOT NULL," +
//                    "SID VARCHAR_IGNORECASE(100) NOT NULL," +
//                    "CONSTRAINT UNIQUE_UK_1 UNIQUE(SID,PRINCIPAL));");
//        template.execute(
//            "CREATE TABLE ACL_CLASS(" +
//                    "ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 100) NOT NULL PRIMARY KEY," +
//                    "CLASS VARCHAR_IGNORECASE(100) NOT NULL," +
//                    "CONSTRAINT UNIQUE_UK_2 UNIQUE(CLASS));");
//        template.execute(
//            "CREATE TABLE ACL_OBJECT_IDENTITY(" +
//                    "ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 100) NOT NULL PRIMARY KEY," +
//                    "OBJECT_ID_CLASS BIGINT NOT NULL," +
//                    "OBJECT_ID_IDENTITY BIGINT NOT NULL," +
//                    "PARENT_OBJECT BIGINT," +
//                    "OWNER_SID BIGINT," +
//                    "ENTRIES_INHERITING BOOLEAN NOT NULL," +
//                    "CONSTRAINT UNIQUE_UK_3 UNIQUE(OBJECT_ID_CLASS,OBJECT_ID_IDENTITY)," +
//                    "CONSTRAINT FOREIGN_FK_1 FOREIGN KEY(PARENT_OBJECT)REFERENCES ACL_OBJECT_IDENTITY(ID)," +
//                    "CONSTRAINT FOREIGN_FK_2 FOREIGN KEY(OBJECT_ID_CLASS)REFERENCES ACL_CLASS(ID)," +
//                    "CONSTRAINT FOREIGN_FK_3 FOREIGN KEY(OWNER_SID)REFERENCES ACL_SID(ID));");
//        template.execute(
//            "CREATE TABLE ACL_ENTRY(" +
//                    "ID BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 100) NOT NULL PRIMARY KEY," +
//                    "ACL_OBJECT_IDENTITY BIGINT NOT NULL,ACE_ORDER INT NOT NULL,SID BIGINT NOT NULL," +
//                    "MASK INTEGER NOT NULL,GRANTING BOOLEAN NOT NULL,AUDIT_SUCCESS BOOLEAN NOT NULL," +
//                    "AUDIT_FAILURE BOOLEAN NOT NULL,CONSTRAINT UNIQUE_UK_4 UNIQUE(ACL_OBJECT_IDENTITY,ACE_ORDER)," +
//                    "CONSTRAINT FOREIGN_FK_4 FOREIGN KEY(ACL_OBJECT_IDENTITY) REFERENCES ACL_OBJECT_IDENTITY(ID)," +
//                    "CONSTRAINT FOREIGN_FK_5 FOREIGN KEY(SID) REFERENCES ACL_SID(ID));");


//        template.execute("INSERT INTO USERS VALUES('rod','$2a$10$75pBjapg4Nl8Pzd.3JRnUe7PDJmk9qBGwNEJDAlA3V.dEJxcDKn5O',TRUE);");
//        template.execute("INSERT INTO USERS VALUES('dianne','$2a$04$bCMEyxrdF/7sgfUiUJ6Ose2vh9DAMaVBldS1Bw2fhi1jgutZrr9zm',TRUE);");
//        template.execute("INSERT INTO USERS VALUES('scott','$2a$06$eChwvzAu3TSexnC3ynw4LOSw1qiEbtNItNeYv5uI40w1i3paoSfLu',TRUE);");
//        template.execute("INSERT INTO USERS VALUES('peter','$2a$04$8.H8bCMROLF4CIgd7IpeQ.tcBXLP5w8iplO0n.kCIkISwrIgX28Ii',FALSE);");
//        template.execute("INSERT INTO USERS VALUES('bill','$2a$04$8.H8bCMROLF4CIgd7IpeQ.3khQlPVNWbp8kzSQqidQHGFurim7P8O',TRUE);");
//        template.execute("INSERT INTO USERS VALUES('bob','$2a$06$zMgxlMf01SfYNcdx7n4NpeFlAGU8apCETz/i2C7VlYWu6IcNyn4Ay',TRUE);");
//        template.execute("INSERT INTO USERS VALUES('jane','$2a$05$ZrdS7yMhCZ1J.AAidXZhCOxdjD8LO/dhlv4FJzkXA6xh9gdEbBT/u',TRUE);");
//        template.execute("INSERT INTO AUTHORITIES VALUES('rod','ROLE_USER');");
//        template.execute("INSERT INTO AUTHORITIES VALUES('rod','ROLE_SUPERVISOR');");
//        template.execute("INSERT INTO AUTHORITIES VALUES('dianne','ROLE_USER');");
//        template.execute("INSERT INTO AUTHORITIES VALUES('scott','ROLE_USER');");
//        template.execute("INSERT INTO AUTHORITIES VALUES('peter','ROLE_USER');");
//        template.execute("INSERT INTO AUTHORITIES VALUES('bill','ROLE_USER');");
//        template.execute("INSERT INTO AUTHORITIES VALUES('bob','ROLE_USER');");
//        template.execute("INSERT INTO AUTHORITIES VALUES('jane','ROLE_USER');");

//        template.execute("INSERT INTO contacts VALUES (1, 'John Smith', 'john@somewhere.com');");
//        template.execute("INSERT INTO contacts VALUES (2, 'Michael Citizen', 'michael@xyz.com');");
//        template.execute("INSERT INTO contacts VALUES (3, 'Joe Bloggs', 'joe@demo.com');");
//        template.execute("INSERT INTO contacts VALUES (4, 'Karen Sutherland', 'karen@sutherland.com');");
//        template.execute("INSERT INTO contacts VALUES (5, 'Mitchell Howard', 'mitchell@abcdef.com');");
//        template.execute("INSERT INTO contacts VALUES (6, 'Rose Costas', 'rose@xyz.com');");
//        template.execute("INSERT INTO contacts VALUES (7, 'Amanda Smith', 'amanda@abcdef.com');");
//        template.execute("INSERT INTO contacts VALUES (8, 'Cindy Smith', 'cindy@smith.com');");
//        template.execute("INSERT INTO contacts VALUES (9, 'Jonathan Citizen', 'jonathan@xyz.com');");

//        for (int i = 10; i < createEntities; i++) {
//            String[] person = selectPerson();
//            template.execute("INSERT INTO contacts VALUES (" + i + ", '" + person[2] + "', '" + person[0].toLowerCase()
//                + "@" + person[1].toLowerCase() + ".com');");
//        }


        // Create acl_object_identity rows (and also acl_class rows as needed
        
        final ObjectIdentity objectIdentity1 = new ObjectIdentityImpl(ApiBundle.class, "1Bundle");
        final ObjectIdentity objectIdentity2 = new ObjectIdentityImpl(ApiBundle.class, "2Bundle");
        final ObjectIdentity objectIdentity3 = new ObjectIdentityImpl(ApiBundle.class, "3Bundle");
        final ObjectIdentity objectIdentity6 = new ObjectIdentityImpl(ApiBundle.class, "6BundleStringIdentifier");
		Set<ObjectIdentity> objIdentities = new HashSet<>();
		objIdentities.add(objectIdentity1);
		objIdentities.add(objectIdentity2);
		objIdentities.add(objectIdentity3);
		objIdentities.add(objectIdentity6);

		for(ObjectIdentity objectIdentity : objIdentities){

			transactionTemplate.execute(new TransactionCallback<Object>() {
				public Object doInTransaction(TransactionStatus arg0) {
					mutableAclService.createAcl(objectIdentity);
	
					return null;
				}
			});

		}
        
//		transactionTemplate.execute(new TransactionCallback<Object>() {
//			public Object doInTransaction(TransactionStatus arg0) {
//				mutableAclService.createAcl(objectIdentity6);
//
//				return null;
//			}
//		});
        

        
        
//        for(int i=1; i<=3; i++){
//        	final ObjectIdentity objectIdentity = new ObjectIdentityImpl(ApiBundle.class, i+"");
//			
//			transactionTemplate.execute(new TransactionCallback<Object>() {
//				public Object doInTransaction(TransactionStatus arg0) {
//					mutableAclService.createAcl(objectIdentity);
//	
//					return null;
//				}
//			});
//        }

        // Now grant some permissions
        grantPermissions("1Bundle", "somas", BasePermission.ADMINISTRATION);
        grantPermissions("1Bundle", "user2", BasePermission.READ);
        
//        grantPermissions(2+"", "somas", BasePermission.WRITE);
//        grantPermissions(2+"", "somas", BasePermission.READ);
//        grantPermissions("2Bundle", "somas", CustomBasePermission.READ_WRITE);
        CumulativePermission cumulativePermission = new CumulativePermission();
        cumulativePermission.set(BasePermission.WRITE).set(BasePermission.READ);
        grantPermissions("2Bundle", "somas", cumulativePermission);
        
        grantPermissions("3Bundle", "user2", BasePermission.WRITE);
        
        grantPermissions("6BundleStringIdentifier", "somas", BasePermission.WRITE);

        
		changeOwner("1Bundle", "somas");
		changeOwner("2Bundle", "somas");
		changeOwner("3Bundle", "somas");
		changeOwner("6BundleStringIdentifier", "somas");
		
        SecurityContextHolder.clearContext();
    }


    private void grantPermissions(int bundleNumber, String recipientUsername, Permission permission) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(ApiBundle.class, new Long(bundleNumber)));
        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(recipientUsername), true);
        updateAclInTransaction(acl);
    }
    
    private void grantPermissions(String bundleNumber, String recipientUsername, Permission permission) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(ApiBundle.class, bundleNumber));
        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(recipientUsername), true);
        updateAclInTransaction(acl);
    }

	
    private void changeOwner(int bundleNumber, String newOwnerUsername) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(ApiBundle.class, new Long(bundleNumber)));
        acl.setOwner(new PrincipalSid(newOwnerUsername));
        
        updateAclInTransaction(acl);
    }
    
    
    private void changeOwner(String bundleNumber, String newOwnerUsername) {
        AclImpl acl = (AclImpl) mutableAclService.readAclById(new ObjectIdentityImpl(ApiBundle.class, bundleNumber));
        acl.setOwner(new PrincipalSid(newOwnerUsername));
        
        updateAclInTransaction(acl);
    }
    
	private void updateAclInTransaction(final MutableAcl acl) {
		transactionTemplate.execute(new TransactionCallback<Object>() {
			public Object doInTransaction(TransactionStatus arg0) {
				mutableAclService.updateAcl(acl);
				return null;
				
			}
		});
	}
	
	
//	@PreDestroy
//	public void destroy(){
//		this.deletePermissions("6BundleStringIdentifier");
//	}

	private void deletePermissions(String bundleNumber){
		logger.warn("deleting this bundleNumber ********************* " + bundleNumber);
		mutableAclService.deleteAcl(new ObjectIdentityImpl(ApiBundle.class, bundleNumber), false);
	}
	
    public void setDataSource(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }
}
