ALTER TABLE att_properties ADD is_deleted TINYINT AFTER version;

COMMIT;