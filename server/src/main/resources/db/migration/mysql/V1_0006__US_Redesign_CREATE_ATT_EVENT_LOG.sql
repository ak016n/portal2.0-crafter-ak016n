
CREATE TABLE IF NOT EXISTS dev_core.event_log (
	id 					VARCHAR(40) NOT NULL PRIMARY KEY,
	actor_id 			VARCHAR(250) NOT NULL,
	impacted_user_id	VARCHAR(40),
	org_id	 			VARCHAR(40),
	event_id			INT NOT NULL,	
	info				VARCHAR(4000),
	actor_type			INT NOT NULL,
	transaction_id      VARCHAR(40),
	created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
