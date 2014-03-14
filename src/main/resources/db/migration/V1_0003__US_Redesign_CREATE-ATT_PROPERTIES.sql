CREATE TABLE IF NOT EXISTS att_properties (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
	item_key VARCHAR(50) NOT NULL,
	field_key VARCHAR(50) NOT NULL,
	description MEDIUMTEXT,
	version BIGINT NOT NULL,
	date_created TIMESTAMP
);

COMMIT;