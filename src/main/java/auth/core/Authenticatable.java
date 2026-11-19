package auth.core;

public interface Authenticatable {
    boolean authenticate(String rawPassword);
}