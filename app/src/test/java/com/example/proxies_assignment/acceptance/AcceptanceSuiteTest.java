package com.example.proxies_assignment.acceptance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.proxies_assignment.controller.ProxyController;

@WebMvcTest(ProxyController.class)
// Disable basic auth
@AutoConfigureMockMvc(addFilters = false)
// Use application-test.properties
@ActiveProfiles("test")
// This is needed for ProxyController to receive its @Autowired proxyRepository.
@AutoConfigureDataJpa
// Make the tests execute in sequential order and depend on eachother.
@TestMethodOrder(MethodOrderer.MethodName.class)
class AcceptanceSuiteTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

	@Test
	void _01_initialGetProxiesPaginatedReturnsEmptyList() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies?page=1&per_page=10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("\"data\":[]"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
	}

    @Test
    void _02_insertProxyReturnsItselfWithId() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                post("/api/v1/proxies")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname1",
                            "type" : "HTTP",
                            "hostname" : "localhost1",
                            "port" : 8081,
                            "username" : "testusername1",
                            "password" : "password",
                            "active" : true
                        }
                    """
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("\"id\":1"));
            assertTrue(content.contains("\"name\":\"testname1\""));
            assertTrue(content.contains("\"type\":\"HTTP\""));
            assertTrue(content.contains("\"hostname\":\"localhost1\""));
            assertTrue(content.contains("\"port\":8081"));
            assertTrue(content.contains("\"username\":\"testusername1\""));
            // Password is hashed along the way.
            assertTrue(content.contains("\"password\":"));
            assertTrue(content.contains("\"active\":true"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _03_tryInsertIdenticalProxyViolatesConstraints() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                post("/api/v1/proxies")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname1",
                            "type" : "HTTP",
                            "hostname" : "localhost1",
                            "port" : 8081,
                            "username" : "testusername1",
                            "password" : "password",
                            "active" : true
                        }
                    """
                ))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("\"success\":false"));
            assertTrue(content.contains("Violated constraint"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _04_insertSecondProxy() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                post("/api/v1/proxies")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname2",
                            "type" : "HTTPS",
                            "hostname" : "localhost2",
                            "port" : 8082,
                            "username" : "testusername2",
                            "password" : "password",
                            "active" : false
                        }
                    """
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("testname2"));
            // id is also incremented after unsuccessful insert, that's why it's 3 and not 2.
            assertTrue(content.contains("\"id\":3"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _05_tryInsertProxyWithMalformedActiveField() {
        // Active field should accept only `true` or `false`.
        try {
            MvcResult requestResult = this.mockMvc.perform(
                post("/api/v1/proxies")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname3",
                            "type" : "HTTPS",
                            "hostname" : "localhost3",
                            "port" : 8082,
                            "username" : "testusername3",
                            "password" : "password",
                            "active" : "malformed"
                        }
                    """
                ))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("Received malformed input"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _06_tryInsertProxyWithInvalidJSON() {
        // Active field should accept only `true` or `false`.
        try {
            MvcResult requestResult = this.mockMvc.perform(
                post("/api/v1/proxies")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname3",
                            "type" : "HTTPS",
                            "hostname" : "localhost3",
                            "port" : 8083,
                            "username" : "testusername3",
                            "password" : "password",
                            "active" : false
                        // missing closing curly brace.
                    """
                ))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("Received malformed input"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _07_getProxyByExistingId() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("\"id\":1"));
            assertTrue(content.contains("\"name\":\"testname1\""));
            assertTrue(content.contains("\"type\":\"HTTP\""));
            assertTrue(content.contains("\"hostname\":\"localhost1\""));
            assertTrue(content.contains("\"port\":8081"));
            assertTrue(content.contains("\"username\":\"testusername1\""));
            assertTrue(content.contains("\"password\":"));
            assertTrue(content.contains("\"active\":true"));
            // Verify that we do not see any other proxy data.
            assertFalse(content.contains("testname2"));
            assertFalse(content.contains("\"id\":3"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _08_tryGetProxyByNonExistentId() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies/10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("Proxy with id 10 does not exist"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _09_getProxiesPaginatedAll() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies?page=1&per_page=10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            // First proxy.
            assertTrue(content.contains("\"id\":1"));
            assertTrue(content.contains("\"name\":\"testname1\""));
            assertTrue(content.contains("\"type\":\"HTTP\""));
            assertTrue(content.contains("\"hostname\":\"localhost1\""));
            assertTrue(content.contains("\"port\":8081"));
            assertTrue(content.contains("\"username\":\"testusername1\""));
            assertTrue(content.contains("\"active\":true"));
            // Second proxy.
            assertTrue(content.contains("\"id\":3"));
            assertTrue(content.contains("\"name\":\"testname2\""));
            assertTrue(content.contains("\"type\":\"HTTPS\""));
            assertTrue(content.contains("\"hostname\":\"localhost2\""));
            assertTrue(content.contains("\"port\":8082"));
            assertTrue(content.contains("\"username\":\"testusername2\""));
            assertTrue(content.contains("\"active\":false"));

        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _10_getProxiesPaginatedSecondPage() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies?page=2&per_page=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("testname2"));
            assertFalse(content.contains("testname1"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _11_getProxiesPaginatedThirdPageEmpty() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies?page=3&per_page=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertFalse(content.contains("testname1"));
            assertFalse(content.contains("testname2"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _12_getProxiesFilteredEmptyFilters() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies/filtered?name=&type="))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("testname1"));
            assertTrue(content.contains("testname2"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _13_getProxiesFilteredByTypeMatchesBoth() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies/filtered?name=&type=HTTP"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("testname1"));
            assertTrue(content.contains("testname2"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _14_getProxiesFilteredByTypeMatchesOnlySecond() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies/filtered?name=&type=HTTPS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertFalse(content.contains("testname1"));
            assertTrue(content.contains("testname2"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _15_getProxiesFilteredByNameMatchesOnlyFirst() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies/filtered?name=testname1&type="))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("testname1"));
            assertFalse(content.contains("testname2"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _16_getProxiesFilteredByBothMatchesNone() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies/filtered?name=testname1&type=SOCKS4"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertFalse(content.contains("testname1"));
            assertFalse(content.contains("testname2"));
            assertTrue(content.contains("\"data\":[]"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _17_tryUpdateProxyByNonExistentId() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                put("/api/v1/proxies/10")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname2.1",
                            "type" : "SOCKS4"
                        }
                    """
                ))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("Proxy with id 10 does not exist"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _18_updateSecondProxy() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                put("/api/v1/proxies/3")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname2.2",
                            "type" : "SOCKS4",
                            "active" : true
                        }
                    """
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("\"id\":3"));
            assertTrue(content.contains("testname2.2"));
            assertTrue(content.contains("SOCKS4"));
            assertTrue(content.contains("\"active\":true"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _19_tryUpdateSecondProxyWithMalformedInput() {
        // Nothing should get updated.
        try {
            MvcResult requestResult = this.mockMvc.perform(
                put("/api/v1/proxies/3")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname2.3",
                            "type" : "SOCKS4",
                            "active" : "malformed"
                        // missing closing curly brace.
                    """
                ))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("Received malformed input"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _20_getAndVerifyAllOk() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies?page=1&per_page=10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("testname1"));
            assertTrue(content.contains("testname2.2"));
            assertTrue(content.contains("HTTP"));
            assertTrue(content.contains("SOCKS4"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _21_tryDeleteNonExistentProxy() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                delete("/api/v1/proxies/10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("Proxy with id 10 does not exist"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _22_deleteFirstProxy() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                delete("/api/v1/proxies/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("\"id\":1"));
            assertTrue(content.contains("testname1"));
            assertTrue(content.contains("HTTP"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _23_getAndVerifyAllOk() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                get("/api/v1/proxies?page=1&per_page=10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertFalse(content.contains("testname1"));
            assertFalse(content.contains("HTTP"));
            assertTrue(content.contains("testname2.2"));
            assertTrue(content.contains("SOCKS4"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }
}
