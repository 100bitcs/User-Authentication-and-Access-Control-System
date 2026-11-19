package auth.core;

import org.mindrot.jbcrypt.BCrypt;
import java.util.Set;

public abstract class User implements Authenticatable {
    protected final int id;
    protected final String username, passwordHash, fullName, email;
    protected final Set<String> permissions;

    protected User(int id, String username, String passwordHash, String fullName,
                   String email, Set<String> permissions) {
        this.id=id; this.username=username; this.passwordHash=passwordHash;
        this.fullName=fullName; this.email=email; this.permissions=permissions;
    }
    @Override public boolean authenticate(String rawPassword) {
        return rawPassword != null && BCrypt.checkpw(rawPassword, passwordHash);
    }
    public boolean hasPermission(String permission) { return permissions.contains(permission); }
    public int getId(){ return id; }
    public String getUsername(){ return username; }
    public String getFullName(){ return fullName; }
    public String getEmail(){ return email; }
    public Set<String> getPermissions(){ return Set.copyOf(permissions); }
    public abstract String getRoleName();
}