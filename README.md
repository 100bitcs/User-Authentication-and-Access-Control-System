# User Authentication and Access Control System

## Requirements
- JDK 17 or later
- Maven
- MySQL Server 8.x
- IntelliJ IDEA, VS Code, or Google Antigravity

## Setup
1. Create a MySQL database by running `database/schema.sql` in MySQL Workbench.
2. Open `src/main/java/auth/core/Database.java` and replace `CHANGE_ME` with your local MySQL password. Keep credentials out of public repositories.
3. Open this folder as a Maven project in Antigravity.
4. Wait for Maven dependencies to finish downloading.
5. Run `auth.ui.LoginFrame` or use the terminal command `mvn clean compile exec:java`.
6. Register a GUEST or STAFF account in the app. ADMIN registration is intentionally disabled in the UI.

## Create the first ADMIN
For a classroom demo, create a normal account, then run this SQL in MySQL Workbench, replacing `your_username`:
```sql
INSERT IGNORE INTO user_roles(user_id,role_id)
SELECT u.user_id,r.role_id FROM users u JOIN roles r ON r.role_name='ADMIN'
WHERE u.username='your_username';
DELETE ur FROM user_roles ur JOIN users u ON u.user_id=ur.user_id
JOIN roles r ON r.role_id=ur.role_id
WHERE u.username='your_username' AND r.role_name<>'ADMIN';
```
Log out and log in again so the new role and permissions load.

## Modules
1. User registration with input validation and BCrypt password hashing
2. Login with prepared statements and password verification
3. Role assignment (database relation + admin setup SQL)
4. Permission checks (role-based permissions)
5. Access report (registered users, role, status, date)

## Notes
- This is an academic demonstration project, not a production identity provider.
- The `assignRole` service method should only be exposed through an admin-only UI action after checking `ROLE_ASSIGN`.
- Do not commit database passwords or real user data.
