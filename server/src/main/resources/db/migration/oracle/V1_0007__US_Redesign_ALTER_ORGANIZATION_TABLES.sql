ALTER TABLE dev_core.organization ADD (organization_type VARCHAR2(2) default '0' not null);

UPDATE dev_core.organization set organization_type = '3';

COMMIT;