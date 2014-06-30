CREATE TABLE IF NOT EXISTS dev_core.user (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
	login VARCHAR(50) NOT NULL,
	password VARCHAR(100),
	last_updated TIMESTAMP
);

COMMIT;