package com.example.proxies_assignment.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.proxies_assignment.model.Proxy;

public class ProxyPasswordHashingTest {
    @Test
    void proxyConstructsWithHashedPassword() {
        String password = "password";
        Proxy proxy = new Proxy("testname1", Proxy.Type.HTTP, "localhost1", 8081, "testusername1", password, true);
        assertTrue(BCrypt.checkpw(password, proxy.getPassword()));
    }

    @Test
    void proxySetNewPasswordDoesHash() {
        Proxy proxy = new Proxy("testname1", Proxy.Type.HTTP, "localhost1", 8081, "testusername1", "password", true);
        String newPassword = "superstrongpassword";
        proxy.setPassword(newPassword);
        assertTrue(BCrypt.checkpw(newPassword, proxy.getPassword()));
    }

    @Test
    void proxySetNewPasswordWithoutHashing() {
        Proxy proxy = new Proxy("testname1", Proxy.Type.HTTP, "localhost1", 8081, "testusername1", "password", true);
        String newPassword = "$2a$12$VSs7qBBTn1qtwf0.vP47FO4p6Jy2WhlPW2fo2YftATxyCx9BIRViC";
        proxy.setPasswordWithoutHashing(newPassword);
        assertTrue(proxy.getPassword().equals(newPassword));
    }
}
