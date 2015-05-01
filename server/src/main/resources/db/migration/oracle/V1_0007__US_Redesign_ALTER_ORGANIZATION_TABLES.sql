ALTER TABLE dev_core.organization ADD (organization_type VARCHAR2(2) not null default '0');

UPDATE dev_core.organization set organization_type = '3';

COMMIT;