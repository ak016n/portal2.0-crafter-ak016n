ALTER TABLE dev_core.att_properties ADD CONSTRAINT UC_ik_fk UNIQUE (item_key, field_key, version);

COMMIT;