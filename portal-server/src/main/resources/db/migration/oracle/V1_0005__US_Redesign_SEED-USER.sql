INSERT INTO dev_core.role(id, name, description) VALUES ('1', 'ROLE_SYS_ADMIN', 'System administrator');

-- DELETE before trying to INSERT
DELETE FROM dev_core.user_role_relationship WHERE user_id = '1';
DELETE FROM dev_core.user_org_membership WHERE user_id in ('1', '2', '3', '4');
DELETE FROM dev_core.users WHERE id IN ('1', '2', '3', '4');
DELETE FROM dev_core.organization WHERE id IN ('1');


-- ADMIN user
INSERT INTO dev_core.users(id, login, password, email) VALUES ('1', 'raj_test', '{SSHA}Uexh4IbjBJU4l7DIq50itbMfFIMPEdoq+hYd4Q==', 'raj.test@att.com');
INSERT INTO dev_core.user_role_relationship (user_id, role_id) VALUES ('1', '1');

-- member
INSERT INTO dev_core.users(id, login, password, email) VALUES ('2', 'leonard_test', '{SSHA}Uexh4IbjBJU4l7DIq50itbMfFIMPEdoq+hYd4Q==', 'leonard.test@att.com');

INSERT INTO dev_core.users(id, login, password, email) VALUES ('3', 'penny_test', '{SSHA}Uexh4IbjBJU4l7DIq50itbMfFIMPEdoq+hYd4Q==', 'penny.test@att.com');

INSERT INTO dev_core.users(id, login, password, email) VALUES ('4', 'howard_test', '{SSHA}Uexh4IbjBJU4l7DIq50itbMfFIMPEdoq+hYd4Q==', 'howard.test@att.com');

-- organization
INSERT INTO dev_core.organization(id, name, description, relationship_type) VALUES ('1', 'ATT_INTERNAL-Big Bang Theory', 'QA only organizaiton', 1);

-- adding user
INSERT INTO dev_core.user_org_membership (org_id, user_id, sequence_number) VALUES ('1', '1', 0);
INSERT INTO dev_core.user_org_membership (org_id, user_id, sequence_number) VALUES ('1', '2', 0);
INSERT INTO dev_core.user_org_membership (org_id, user_id, sequence_number) VALUES ('1', '3', 0);
INSERT INTO dev_core.user_org_membership (org_id, user_id, sequence_number) VALUES ('1', '4', 0);

COMMIT;