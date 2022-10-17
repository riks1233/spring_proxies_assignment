package com.example.proxies_assignment.model;


import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
@Table(name = "proxies")
public class Proxy {
    @Id
    @GeneratedValue
    private long id;

    // @Column(length = 120, unique = true, nullable = false)
    @NotBlank
    @Length(min = 2, max = 120, message = "`name` should be provided.")
    private String name;

    public static enum Type {
        HTTP,
        HTTPS,
        SOCKS4,
        SOCKS5,
    }
    @Enumerated(EnumType.STRING)
    // @NotNull(message = "`type` should be provided.")
    private Type type;

    // @Column(length = 120, unique = true, nullable = false)
    @NotBlank(message = "`hostname` should be provided.")
    @Length(min = 2, max = 120, message = "`hostname` should have 2-120 characters.")
    private String hostname;

    // Validated on application level to range from 0 to 65535.
    // @Column(nullable = false)
    @NotNull(message = "`port` should be provided.")
    @Min(value = 0, message = "`port` value must be in range 0-65535.")
    @Max(value = 65535, message = "`port` value must be in range 0-65535.")
    private Integer port;

    // @Column(length = 120, nullable = false)
    @NotBlank(message = "`username` should be provided.")
    @Length(min = 2, max = 120, message = "`username` should have 2-120 characters.")
    private String username;

    // Not validating password because it is immediately converted to hash.
    // Password validation is expected to be done on the client side.
    @NotBlank(message = "`password` should be provided.")
    private String password;

    // @Column(nullable = false)
    @NotNull(message = "`active` should be provided.")
    private Boolean active;

    public Proxy() {}
    public Proxy(
        String name,
        Type type,
        String hostname,
        int port,
        String username,
        String plaintextPassword,
        boolean active
    ) {
        this.name = name;
        this.type = type;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = hashPlaintextPassword(plaintextPassword);
        this.active = active;
    }

    private static String hashPlaintextPassword(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt(12));
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String plaintextPassword) {
        this.password = hashPlaintextPassword(plaintextPassword);
    }
    // Set password without automatically hashing it.
    // Useful when wanting to set password to an already
    // existing hash.
    public void setPasswordWithoutHashing(String password) {
        this.password = password;
    }
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void updateFieldsFromAnother(Proxy anotherProxy) {
        if (anotherProxy.getName() != null) {
            this.setName(anotherProxy.getName());
        }
        if (anotherProxy.getType() != null) {
            this.setType(anotherProxy.getType());
        }
        if (anotherProxy.getHostname() != null) {
            this.setHostname(anotherProxy.getHostname());
        }
        if (anotherProxy.getPort() != null) {
            this.setPort(anotherProxy.getPort());
        }
        if (anotherProxy.getUsername() != null) {
            this.setUsername(anotherProxy.getUsername());
        }
        if (anotherProxy.getPassword() != null) {
            this.setPasswordWithoutHashing(anotherProxy.getPassword());
        }
        if (anotherProxy.isActive() != null) {
            this.setActive(anotherProxy.isActive());
        }
    }
}
