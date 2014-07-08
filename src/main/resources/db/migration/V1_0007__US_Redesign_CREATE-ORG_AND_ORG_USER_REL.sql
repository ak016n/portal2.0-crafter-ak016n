CREATE TABLE IF NOT EXISTS dev_core.organization (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
	name VARCHAR(254) NOT NULL UNIQUE,
	description VARCHAR(4000),
	parent_id VARCHAR(40),
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS dev_core.user_org_membership(
	org_id VARCHAR(40) NOT NULL,
	user_id VARCHAR(40) NOT NULL,
	sequence_number INT NOT NULL DEFAULT 0,
	
	PRIMARY KEY(org_id, user_id),
	
	FOREIGN KEY (org_id) REFERENCES organization(id),
	FOREIGN KEY (user_id) REFERENCES user(id)
);

COMMIT;