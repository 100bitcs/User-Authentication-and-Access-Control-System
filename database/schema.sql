CREATE DATABASE IF NOT EXISTS user_auth_db;
USE user_auth_db;

CREATE TABLE IF NOT EXISTS roles (
  role_id INT AUTO_INCREMENT PRIMARY KEY,
  role_name VARCHAR(30) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS permissions (
  permission_id INT AUTO_INCREMENT PRIMARY KEY,
  permission_name VARCHAR(60) NOT NULL UNIQUE,
  description VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(40) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS user_roles (
  user_id INT NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY(user_id, role_id),
  FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY(role_id) REFERENCES roles(role_id)
);
CREATE TABLE IF NOT EXISTS role_permissions (
  role_id INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY(role_id, permission_id),
  FOREIGN KEY(role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
  FOREIGN KEY(permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

INSERT IGNORE INTO roles(role_name) VALUES ('ADMIN'),('STAFF'),('GUEST');
INSERT IGNORE INTO permissions(permission_name,description) VALUES
 ('USER_MANAGE','Create and manage users'),
 ('ROLE_ASSIGN','Assign roles to users'),
 ('ACCESS_REPORT','View access report'),
 ('VIEW_DASHBOARD','View dashboard'),
 ('VIEW_PROFILE','View own profile');

-- ADMIN: all permissions
INSERT IGNORE INTO role_permissions(role_id,permission_id)
 SELECT r.role_id,p.permission_id FROM roles r CROSS JOIN permissions p WHERE r.role_name='ADMIN';
-- STAFF
INSERT IGNORE INTO role_permissions(role_id,permission_id)
 SELECT r.role_id,p.permission_id FROM roles r JOIN permissions p
 WHERE r.role_name='STAFF' AND p.permission_name IN ('VIEW_DASHBOARD','VIEW_PROFILE','ACCESS_REPORT');
-- GUEST
INSERT IGNORE INTO role_permissions(role_id,permission_id)
 SELECT r.role_id,p.permission_id FROM roles r JOIN permissions p
 WHERE r.role_name='GUEST' AND p.permission_name IN ('VIEW_DASHBOARD','VIEW_PROFILE');
