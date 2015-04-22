DECLARE
v_code  NUMBER;
v_errm  VARCHAR2(64);
BEGIN
  
  EXECUTE IMMEDIATE 'DROP sequence dev_core.acl_entry_sequence';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.acl_entry';
	
  EXECUTE IMMEDIATE 'DROP sequence dev_core.acl_object_identity_sequence';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.acl_object_identity';
  
  EXECUTE IMMEDIATE 'DROP sequence dev_core.acl_class_sequence';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.acl_class';
  
  EXECUTE IMMEDIATE 'DROP sequence dev_core.acl_sid_sequence';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.acl_sid';
  
EXCEPTION
  WHEN OTHERS THEN
  v_code := SQLCODE;
  v_errm := SUBSTR(SQLERRM, 1, 64);
  DBMS_OUTPUT.PUT_LINE (v_code || ' ' || v_errm);
  IF (sqlcode = -0942 or sqlcode = -06512 or sqlcode = -02289) THEN NULL; ELSE RAISE; END IF;
END;
/

CREATE TABLE dev_core.acl_sid (
id NUMBER(38) NOT NULL PRIMARY KEY,
principal NUMBER(1) NOT NULL CHECK (principal in (0, 1)),
sid NVARCHAR2(100) NOT NULL,
CONSTRAINT unique_acl_sid UNIQUE (sid, principal)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.acl_sid TO appdev_core;

CREATE SEQUENCE dev_core.acl_sid_sequence START WITH 1 INCREMENT BY 1 NOMAXVALUE;

CREATE OR REPLACE TRIGGER dev_core.acl_sid_id_trigger
BEFORE INSERT ON dev_core.acl_sid
FOR EACH ROW
BEGIN
SELECT dev_core.acl_sid_sequence.nextval INTO :new.id FROM dual;
END;
/

commit;

CREATE TABLE dev_core.acl_class (
id NUMBER(38) NOT NULL PRIMARY KEY,
class NVARCHAR2(100) NOT NULL,
CONSTRAINT uk_acl_class UNIQUE (class)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.acl_class TO appdev_core;

CREATE SEQUENCE dev_core.acl_class_sequence START WITH 1 INCREMENT BY 1 NOMAXVALUE;

CREATE OR REPLACE TRIGGER dev_core.acl_class_id_trigger
BEFORE INSERT ON dev_core.acl_class
FOR EACH ROW
BEGIN
SELECT dev_core.acl_class_sequence.nextval INTO :new.id FROM dual;
END;
/

commit;

CREATE TABLE dev_core.acl_object_identity (
id NUMBER(38) NOT NULL PRIMARY KEY,
object_id_class NUMBER(38) NOT NULL,
object_id_identity VARCHAR(80) NOT NULL,
parent_object NUMBER(38),
owner_sid NUMBER(38),
entries_inheriting NUMBER(1) NOT NULL CHECK (entries_inheriting in (0, 1)),
CONSTRAINT uk_acl_object_identity UNIQUE (object_id_class, object_id_identity),
CONSTRAINT fk_acl_object_identity_parent FOREIGN KEY (parent_object) REFERENCES dev_core.acl_object_identity (id),
CONSTRAINT fk_acl_object_identity_class FOREIGN KEY (object_id_class) REFERENCES dev_core.acl_class (id),
CONSTRAINT fk_acl_object_identity_owner FOREIGN KEY (owner_sid) REFERENCES dev_core.acl_sid (id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.acl_object_identity TO appdev_core;

CREATE SEQUENCE dev_core.acl_object_identity_sequence START WITH 1 INCREMENT BY 1 NOMAXVALUE;

CREATE OR REPLACE TRIGGER dev_core.acl_object_identity_id_trigger
BEFORE INSERT ON dev_core.acl_object_identity
FOR EACH ROW
BEGIN
SELECT dev_core.acl_object_identity_sequence.nextval INTO :new.id FROM dual;
END;
/


commit;

CREATE TABLE dev_core.acl_entry (
id NUMBER(38) NOT NULL PRIMARY KEY,
acl_object_identity NUMBER(38) NOT NULL,
ace_order INTEGER NOT NULL,
sid NUMBER(38) NOT NULL,
mask INTEGER NOT NULL,
granting NUMBER(1) NOT NULL CHECK (granting in (0, 1)),
audit_success NUMBER(1) NOT NULL CHECK (audit_success in (0, 1)),
audit_failure NUMBER(1) NOT NULL CHECK (audit_failure in (0, 1)),
CONSTRAINT unique_acl_entry UNIQUE (acl_object_identity, ace_order),
CONSTRAINT fk_acl_entry_object FOREIGN KEY (acl_object_identity) REFERENCES dev_core.acl_object_identity (id),
CONSTRAINT fk_acl_entry_acl FOREIGN KEY (sid) REFERENCES dev_core.acl_sid (id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.acl_entry TO appdev_core;

CREATE SEQUENCE dev_core.acl_entry_sequence START WITH 1 INCREMENT BY 1 NOMAXVALUE;

CREATE OR REPLACE TRIGGER dev_core.acl_entry_id_trigger
BEFORE INSERT ON dev_core.acl_entry
FOR EACH ROW
BEGIN
SELECT dev_core.acl_entry_sequence.nextval INTO :new.id FROM dual;
END;
/

commit;

