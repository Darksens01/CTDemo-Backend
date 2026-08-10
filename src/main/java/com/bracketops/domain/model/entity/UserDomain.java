package com.bracketops.domain.model.entity;

import com.bracketops.domain.model.valueobject.Role;
import java.util.Objects;
import java.util.UUID;

public class UserDomain {
    private final String id;
    private final String username;
    private final String password;
    private final String fullName;
    private final String email;
    private final Role role;
    private final boolean active;

    public UserDomain(String id, String username, String password, String fullName, String email, Role role, boolean active) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public boolean isActive() { return active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDomain user = (UserDomain) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
