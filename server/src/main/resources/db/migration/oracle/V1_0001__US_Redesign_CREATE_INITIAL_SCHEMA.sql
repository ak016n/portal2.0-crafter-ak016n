BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_user';
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_user_org_membership';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE devcore.att_user (
  id VARCHAR2(40) NOT NULL PRIMARY KEY,
  login VARCHAR2(50) NOT NULL,
  password VARCHAR2(100),
  email VARCHAR2(100) NOT NULL,
  last_updated TIMESTAMP
);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_user TO appdevcore;

COMMIT;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_properties';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE devcore.att_properties (
    id VARCHAR2(40) NOT NULL PRIMARY KEY,
	item_key VARCHAR2(50) NOT NULL,
	field_key VARCHAR2(50) NOT NULL,
	description CLOB,
	version NUMBER(19, 0) NOT NULL,
	is_deleted NUMBER(3, 0),
	date_created TIMESTAMP
);

ALTER TABLE devcore.att_properties ADD CONSTRAINT ik_fk_version_unique UNIQUE (item_key, field_key, version);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_properties TO appdevcore;

COMMIT;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_event_log';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE  devcore.att_event_log (
	id 					VARCHAR2(40) NOT NULL PRIMARY KEY,
	actor_id 			VARCHAR2(40) NOT NULL,
	impacted_user_id	VARCHAR2(40),
	org_id	 			VARCHAR2(40),
	event_type			NUMBER(10, 0) NOT NULL,	
	info				VARCHAR2(4000),
	actor_type			INT NOT NULL,
	transaction_id      VARCHAR2(40),
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_event_log TO appdevcore;

COMMIT;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_organization';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE  devcore.att_organization (
    id VARCHAR2(40) NOT NULL PRIMARY KEY,
	name VARCHAR2(500) NOT NULL UNIQUE,
	description VARCHAR2(4000),
	parent_id VARCHAR2(40),
	relationship_type NUMBER(10, 0) DEFAULT 3 NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_organization TO appdevcore;

COMMIT;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_user_org_membership';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE  devcore.att_user_org_membership (
	org_id VARCHAR2(40) NOT NULL,
	user_id VARCHAR2(40) NOT NULL,
	sequence_number NUMBER(10, 0) DEFAULT 0 NOT NULL,
	CONSTRAINT att_user_org_membership_pk PRIMARY KEY(org_id, user_id),
	CONSTRAINT att_membership_org_fk FOREIGN KEY (org_id) REFERENCES devcore.att_organization(id),
	CONSTRAINT att_membership_user_fk FOREIGN KEY (user_id) REFERENCES devcore.att_user(id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_user_org_membership TO appdevcore;

COMMIT;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_state';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE  devcore.att_state (
    id VARCHAR2(40) NOT NULL PRIMARY KEY,
	user_id VARCHAR2(40),
	org_id VARCHAR2(40),
	state_id NUMBER(10, 0) NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT att_state_user_fk FOREIGN KEY (user_id) REFERENCES devcore.att_user(id),
	CONSTRAINT att_state_org_fk  FOREIGN KEY (org_id) REFERENCES devcore.att_organization(id)
);
GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_state TO appdevcore;

COMMIT;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_api_bundle';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

  CREATE TABLE  devcore.att_api_bundle(
  	id VARCHAR2(40) NOT NULL PRIMARY KEY, 
	name VARCHAR2(40) NOT NULL, 
	start_date TIMESTAMP NOT NULL, 
	end_date TIMESTAMP NOT NULL, 
	comments VARCHAR2(4000) NOT NULL, 
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, 
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_api_bundle TO appdevcore;

COMMIT;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_role';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE  devcore.att_role (
    id VARCHAR2(40) NOT NULL PRIMARY KEY,
    name VARCHAR2(100) NOT NULL,
	description VARCHAR2(4000) NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_role TO appdevcore;

COMMIT;


BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_user_role_relationship';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE  devcore.att_user_role_relationship (
	user_id VARCHAR2(40) NOT NULL,
	role_id VARCHAR2(40) NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT att_user_role_pk  PRIMARY KEY(user_id, role_id),
	CONSTRAINT att_rship_role_fk  FOREIGN KEY (role_id) REFERENCES devcore.att_role(id),
	CONSTRAINT att_rship_user_fk  FOREIGN KEY (user_id) REFERENCES devcore.att_user(id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_user_role_relationship TO appdevcore;

COMMIT;


BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE devcore.att_org_role_relationship';
EXCEPTION
  WHEN OTHERS THEN
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE  devcore.att_org_role_relationship (
	org_id VARCHAR2(40) NOT NULL,
	role_id VARCHAR2(40) NOT NULL,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT att_org_role_pk PRIMARY KEY(org_id, role_id),
	CONSTRAINT att_rship_role_pk FOREIGN KEY (role_id) REFERENCES devcore.att_role(id),
	CONSTRAINT att_rship_org_pk FOREIGN KEY (org_id) REFERENCES devcore.att_organization(id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON devcore.att_org_role_relationship TO appdevcore;

COMMIT;