package auth.core;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.*;

public class AuthService {
    public void register(String username, String password, String fullName, String email, String role)
            throws Exception {
        if (username == null || !username.matches("[A-Za-z0-9_]{4,40}"))
            throw new IllegalArgumentException("Username must be 4-40 characters (letters, numbers, underscore).");
        if (password == null || password.length() < 8)
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        if (fullName == null || fullName.isBlank() || email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("Enter a valid name and email.");
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        try (Connection c=Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                int userId;
                try (PreparedStatement ps=c.prepareStatement(
                    "INSERT INTO users(username,password_hash,full_name,email) VALUES(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1,username); ps.setString(2,hash); ps.setString(3,fullName); ps.setString(4,email);
                    ps.executeUpdate();
                    try(ResultSet rs=ps.getGeneratedKeys()){ if(!rs.next()) throw new SQLException("Could not create user."); userId=rs.getInt(1); }
                }
                try(PreparedStatement ps=c.prepareStatement(
                    "INSERT INTO user_roles(user_id,role_id) SELECT ?,role_id FROM roles WHERE role_name=?")) {
                    ps.setInt(1,userId); ps.setString(2,role.toUpperCase(Locale.ROOT));
                    if(ps.executeUpdate()!=1) throw new SQLException("Selected role does not exist.");
                }
                c.commit();
            } catch(Exception ex){ c.rollback(); throw ex; }
        } catch(SQLIntegrityConstraintViolationException ex) {
            throw new IllegalArgumentException("Username or email is already registered.");
        }
    }

    public User login(String username, String password) throws Exception {
        if(username==null || username.isBlank() || password==null || password.isBlank())
            throw new InvalidLoginException("Enter both username and password.");
        String sql = """
            SELECT u.user_id,u.username,u.password_hash,u.full_name,u.email,r.role_name,p.permission_name
            FROM users u JOIN user_roles ur ON u.user_id=ur.user_id
            JOIN roles r ON r.role_id=ur.role_id
            LEFT JOIN role_permissions rp ON rp.role_id=r.role_id
            LEFT JOIN permissions p ON p.permission_id=rp.permission_id
            WHERE u.username=? AND u.active=TRUE
            """;
        try(Connection c=Database.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,username);
            try(ResultSet rs=ps.executeQuery()){
                int id=-1; String u=null,h=null,n=null,e=null,role=null; Set<String> perms=new HashSet<>();
                while(rs.next()){
                    id=rs.getInt("user_id"); u=rs.getString("username"); h=rs.getString("password_hash");
                    n=rs.getString("full_name"); e=rs.getString("email"); role=rs.getString("role_name");
                    String perm=rs.getString("permission_name"); if(perm!=null) perms.add(perm);
                }
                if(id<0 || !BCrypt.checkpw(password,h)) throw new InvalidLoginException("Invalid username or password.");
                return switch(role){
                    case "ADMIN" -> new Admin(id,u,h,n,e,perms);
                    case "STAFF" -> new Staff(id,u,h,n,e,perms);
                    default -> new Guest(id,u,h,n,e,perms);
                };
            }
        }
    }

    public void checkPermission(User user, String permission) throws AccessDeniedException {
        if(user==null || !user.hasPermission(permission))
            throw new AccessDeniedException("Access denied. Required permission: "+permission);
    }

    public List<String[]> getAccessReport() throws SQLException {
        List<String[]> rows=new ArrayList<>();
        String sql="SELECT u.username,u.full_name,r.role_name,u.active,u.created_at FROM users u " +
                   "JOIN user_roles ur ON u.user_id=ur.user_id JOIN roles r ON r.role_id=ur.role_id ORDER BY u.created_at DESC";
        try(Connection c=Database.getConnection(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()){
            while(rs.next()) rows.add(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),
                rs.getBoolean(4)?"Active":"Disabled",rs.getString(5)});
        }
        return rows;
    }

    public void assignRole(int userId,String role) throws SQLException {
        try(Connection c=Database.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement del=c.prepareStatement("DELETE FROM user_roles WHERE user_id=?")){
                del.setInt(1,userId); del.executeUpdate();
            }
            try(PreparedStatement ins=c.prepareStatement(
                    "INSERT INTO user_roles(user_id,role_id) SELECT ?,role_id FROM roles WHERE role_name=?")){
                ins.setInt(1,userId); ins.setString(2,role.toUpperCase(Locale.ROOT));
                if(ins.executeUpdate()!=1) throw new SQLException("Selected role does not exist.");
            }
            c.commit();
        }
    }
}