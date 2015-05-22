ALTER TABLE dev_core.att_properties ADD is_deleted TINYINT AFTER version;

COMMIT;