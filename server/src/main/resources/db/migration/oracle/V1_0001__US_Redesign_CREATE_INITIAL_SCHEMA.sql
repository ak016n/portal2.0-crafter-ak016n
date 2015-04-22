DECLARE
v_code  NUMBER;
v_errm  VARCHAR2(64);
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.user_role_relationship';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.org_role_relationship';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.api_bundle';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.user_org_membership';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.att_properties';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.event_log';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.role';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.state';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.users';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.organization';
EXCEPTION
  WHEN OTHERS THEN
  v_code := SQLCODE;
  v_errm := SUBSTR(SQLERRM, 1, 64);
  DBMS_OUTPUT.PUT_LINE (v_code || ' ' || v_errm);
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE dev_core.users (
  id VARCHAR2(40) NOT NULL,
  login VARCHAR2(50) NOT NULL,
  password VARCHAR2(100),
  email VARCHAR2(100) NOT NULL,
  last_updated TIMESTAMP,
  
  CONSTRAINT user_pk PRIMARY KEY (id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.users TO appdev_core;

COMMIT;

CREATE TABLE dev_core.att_properties (
    id VARCHAR2(40) NOT NULL,
	item_key VARCHAR2(50) NOT NULL,
	field_key VARCHAR2(50) NOT NULL,
	description CLOB,
	version NUMBER(19, 0) NOT NULL,
	is_deleted NUMBER(3, 0),
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	
	CONSTRAINT att_properties_pk PRIMARY KEY (id)
);

ALTER TABLE dev_core.att_properties ADD CONSTRAINT ik_fk_version_unique UNIQUE (item_key, field_key, version);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.att_properties TO appdev_core;

COMMIT;


CREATE TABLE  dev_core.event_log (
	id 					VARCHAR2(40) NOT NULL,
	actor_id 			VARCHAR2(40) NOT NULL,
	impacted_user_id	VARCHAR2(40),
	org_id	 			VARCHAR2(40),
	event_type			NUMBER(10, 0) NOT NULL,	
	info				VARCHAR2(4000),
	actor_type			INT NOT NULL,
	transaction_id      VARCHAR2(40),
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	
	CONSTRAINT event_log_pk PRIMARY KEY (id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.event_log TO appdev_core;

COMMIT;

CREATE TABLE  dev_core.organization (
    id VARCHAR2(40) NOT NULL,
	name VARCHAR2(500) NOT NULL UNIQUE,
	description VARCHAR2(4000),
	parent_id VARCHAR2(40),
	relationship_type NUMBER(10, 0) DEFAULT 3 NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	
	CONSTRAINT organization_pk PRIMARY KEY (id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.organization TO appdev_core;

COMMIT;


CREATE TABLE  dev_core.user_org_membership (
	org_id VARCHAR2(40) NOT NULL,
	user_id VARCHAR2(40) NOT NULL,
	sequence_number NUMBER(10, 0) DEFAULT 0 NOT NULL,
	CONSTRAINT user_org_membership_pk PRIMARY KEY(org_id, user_id),
	CONSTRAINT membership_org_fk FOREIGN KEY (org_id) REFERENCES dev_core.organization(id),
	CONSTRAINT membership_user_fk FOREIGN KEY (user_id) REFERENCES dev_core.users(id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.user_org_membership TO appdev_core;

COMMIT;


CREATE TABLE  dev_core.state (
    id VARCHAR2(40) NOT NULL,
	user_id VARCHAR2(40),
	org_id VARCHAR2(40),
	state_id NUMBER(10, 0) NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	
	CONSTRAINT state_pk PRIMARY KEY (id),
	
	CONSTRAINT state_user_fk FOREIGN KEY (user_id) REFERENCES dev_core.users(id),
	CONSTRAINT state_org_fk  FOREIGN KEY (org_id) REFERENCES dev_core.organization(id)
);
GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.state TO appdev_core;

COMMIT;

  CREATE TABLE  dev_core.api_bundle (
  	id VARCHAR2(40) NOT NULL, 
	name VARCHAR2(40) NOT NULL, 
	start_date TIMESTAMP NOT NULL, 
	end_date TIMESTAMP NOT NULL, 
	comments VARCHAR2(4000) NOT NULL, 
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, 
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	
	CONSTRAINT api_bundle_pk PRIMARY KEY (id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.api_bundle TO appdev_core;

COMMIT;

CREATE TABLE  dev_core.role (
    id VARCHAR2(40) NOT NULL,
    name VARCHAR2(100) NOT NULL,
	description VARCHAR2(4000) NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	
	CONSTRAINT role_pk PRIMARY KEY (id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.role TO appdev_core;

COMMIT;


CREATE TABLE  dev_core.user_role_relationship (
	user_id VARCHAR2(40) NOT NULL,
	role_id VARCHAR2(40) NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT user_role_pk  PRIMARY KEY(user_id, role_id),
	CONSTRAINT rship_role_fk  FOREIGN KEY (role_id) REFERENCES dev_core.role(id),
	CONSTRAINT rship_user_fk  FOREIGN KEY (user_id) REFERENCES dev_core.users(id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.user_role_relationship TO appdev_core;

COMMIT;


CREATE TABLE  dev_core.org_role_relationship (
	org_id VARCHAR2(40) NOT NULL,
	role_id VARCHAR2(40) NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT org_role_pk PRIMARY KEY(org_id, role_id),
	CONSTRAINT rship_role_pk FOREIGN KEY (role_id) REFERENCES dev_core.role(id),
	CONSTRAINT rship_org_pk FOREIGN KEY (org_id) REFERENCES dev_core.organization(id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.org_role_relationship TO appdev_core;

COMMIT;