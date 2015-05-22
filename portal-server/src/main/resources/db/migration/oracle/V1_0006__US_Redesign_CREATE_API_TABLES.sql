DECLARE
v_code  NUMBER;
v_errm  VARCHAR2(64);
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.api_bundle_relationship';
  EXECUTE IMMEDIATE 'DROP TABLE dev_core.api';
EXCEPTION
  WHEN OTHERS THEN
  v_code := SQLCODE;
  v_errm := SUBSTR(SQLERRM, 1, 64);
  DBMS_OUTPUT.PUT_LINE (v_code || ' ' || v_errm);
  IF sqlcode != -0942 THEN RAISE; END IF;
END;
/

CREATE TABLE dev_core.api (
  id VARCHAR2(40) NOT NULL,
  name VARCHAR2(100) NOT NULL,
  created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  
  CONSTRAINT api_pk PRIMARY KEY (id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.api TO appdev_core;

COMMIT;

CREATE TABLE dev_core.api_bundle_relationship (
    id VARCHAR2(40) NOT NULL,
	api_bundle_id VARCHAR2(40) NOT NULL,
	api_id VARCHAR2(40) NOT NULL,
	
	CONSTRAINT api_bundle_relationship_pk PRIMARY KEY (id),
	CONSTRAINT api_bundle_fk FOREIGN KEY (api_bundle_id) REFERENCES dev_core.api_bundle(id),
	CONSTRAINT api_fk FOREIGN KEY (api_id) REFERENCES dev_core.api(id)
);

GRANT INSERT, UPDATE, SELECT, DELETE ON dev_core.api_bundle_relationship TO appdev_core;

COMMIT;