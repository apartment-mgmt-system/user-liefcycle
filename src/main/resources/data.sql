USE User_Management;

-- Insert 'ROLE_ADMIN' if it doesn't already exist
INSERT INTO roles (role_code, role_name, created_by, created_date) 
SELECT 'ROLE_ADMIN', 'Admin', 'system', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_code = 'ROLE_ADMIN');

-- Insert 'ROLE_TENANT' if it doesn't already exist
INSERT INTO roles (role_code, role_name, created_by, created_date) 
SELECT 'ROLE_TENANT', 'Tenant', 'system', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_code = 'ROLE_TENANT');


-- Insert 'Niket' if the user doesn't already exist
INSERT INTO users (name, email, user_name, apartment_details, encoded_pwd, created_by, created_date) 
SELECT 'Niket', 'niketsourabh767@example.com', 'admin', 'Apartment 101, Building A', 
       '$2a$10$ttztzlqwtoSavIgQ0N87ie9qmn0JOpgCyMMX1Nt6t1iK4HAHNYRMu', 'system', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE name = 'Niket');


-- Associate 'Niket' with 'ROLE_ADMIN' if not already associated
INSERT INTO user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
JOIN roles r ON r.role_code = 'ROLE_ADMIN'
WHERE u.user_name = 'admin'
AND NOT EXISTS (SELECT 1 FROM user_role ur 
                JOIN users u2 ON ur.user_id = u2.user_id
                JOIN roles r2 ON ur.role_id = r2.role_id
                WHERE u2.user_name = 'admin' AND r2.role_code = 'ROLE_ADMIN');

-- Associate 'Niket' with 'ROLE_TENANT' if not already associated
INSERT INTO user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
JOIN roles r ON r.role_code = 'ROLE_TENANT'
WHERE u.user_name = 'admin'
AND NOT EXISTS (SELECT 1 FROM user_role ur 
                JOIN users u2 ON ur.user_id = u2.user_id
                JOIN roles r2 ON ur.role_id = r2.role_id
                WHERE u2.user_name = 'admin' AND r2.role_code = 'ROLE_TENANT');
