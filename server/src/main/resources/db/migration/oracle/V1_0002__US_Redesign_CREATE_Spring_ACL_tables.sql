BEGIN
	execute immediate 'DROP TABLE acl_entry';
	execute immediate 'DROP TABLE acl_object_identity';
	execute immediate 'DROP TABLE acl_sid';
	execute immediate 'DROP TABLE acl_class';
	execute immediate 'DROP SEQUENCE acl_sid_sequence';
	execute immediate 'DROP SEQUENCE acl_sid_sequence';
	execute immediate 'DROP SEQUENCE acl_class_sequence';
EXCEPTION
  WHEN OTHERS THEN
	NULL;
  END;
/

CREATE TABLE acl_sid( id INT NOT NULL,
principal NUMBER(1) NOT NULL,
sid VARCHAR(100) NOT NULL,
CONSTRAINT acl_sid_pk PRIMARY KEY (id));

CREATE TABLE acl_class( id INT NOT NULL,
class VARCHAR(100) NOT NULL,
CONSTRAINT acl_class_pk PRIMARY KEY (id),
CONSTRAINT acl_class_unique_class UNIQUE(class));
 
CREATE TABLE acl_object_identity( id int NOT NULL,
object_id_class INT NOT NULL,
object_id_identity VARCHAR(80) NOT NULL,
parent_object INT,
owner_sid INT,
entries_inheriting number(1) NOT NULL,
CONSTRAINT acl_o_i_pk PRIMARY KEY (id),
CONSTRAINT acl_o_i_unique_class_oid UNIQUE(object_id_class,object_id_identity),
CONSTRAINT fk_aoi_parent_object FOREIGN KEY(PARENT_OBJECT) REFERENCES acl_object_identity(id),
CONSTRAINT fk_aoi_acl_class FOREIGN KEY(object_id_class) REFERENCES acl_class(id),
CONSTRAINT fk_aoi_acl_sid FOREIGN KEY(OWNER_SID) REFERENCES acl_sid(id));

CREATE TABLE acl_entry( id INT NOT NULL PRIMARY KEY,
acl_object_identity INT NOT NULL,
ace_order INT NOT NULL,
sid INT NOT NULL,
mask INTEGER NOT NULL,
granting number(1) NOT NULL,
audit_success number(1) NOT NULL,
audit_failure number(1) NOT NULL,
CONSTRAINT acl_entry_unique_oid_order UNIQUE(acl_object_identity,ace_order),
CONSTRAINT fk_acl_entry_aoi FOREIGN KEY(acl_object_identity) REFERENCES acl_object_identity(id),
CONSTRAINT fk_acl_entry_acl_sid FOREIGN KEY(SID) REFERENCES acl_sid(id));

CREATE SEQUENCE acl_sid_sequence;

CREATE OR REPLACE TRIGGER acl_sid_id
BEFORE INSERT ON acl_sid
FOR EACH ROW
BEGIN
SELECT acl_sid_sequence.NEXTVAL INTO :new.id FROM dual;
END;
/

CREATE SEQUENCE acl_class_sequence;

CREATE OR REPLACE TRIGGER acl_class_id
BEFORE INSERT ON acl_sid
FOR EACH ROW
BEGIN
SELECT acl_class_sequence.NEXTVAL INTO :new.id FROM dual;
END;
/