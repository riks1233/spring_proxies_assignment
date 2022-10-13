package com.example.proxies_assignment;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.proxies_assignment.controller.ProxyController;
import com.example.proxies_assignment.repository.ProxyRepository;

@WebMvcTest(ProxyController.class)
// Disable basic auth
@AutoConfigureMockMvc(addFilters = false)
// Use application-test.properties
@ActiveProfiles("test")
// This is needed for ProxyController to receive its @Autowired proxyRepository.
@AutoConfigureDataJpa
// Make the tests execute in sequential order and depend on eachother.
@TestMethodOrder(MethodOrderer.MethodName.class)
class ProxyControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private MockMvc mockMvc;

	@Test
	void _1_initialGetReturnsEmptyList() {
        try {
            this.mockMvc.perform(get("/api/v1/proxies/all?page=1&per_page=10")).andDo(print()).andExpect(status().isOk());
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
	}

    @Test
    void _2_insertObjectReturnsItselfWithId() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                post("/api/v1/proxies")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname1",
                            "type" : "HTTP",
                            "hostname" : "localhost",
                            "port" : 8080,
                            "username" : "testusername1",
                            "password" : "password",
                            "active" : false
                        }
                    """
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
            String content = requestResult.getResponse().getContentAsString();
            assertTrue(content.contains("testname1"));
            assertTrue(content.contains("\"id\":1"));
        } catch (Exception e) {
            System.out.println("Got exception, but shouldn't have: " + e.getMessage());
        }
    }

    @Test
    void _3_insertIdenticalObjectViolatesConstraints() {
        try {
            MvcResult requestResult = this.mockMvc.perform(
                post("/api/v1/proxies")
                .contentType(APPLICATION_JSON_UTF8)
                .content(
                    """
                        {
                            "name" : "testname1",
                            "type" : "HTTP",
                            "hostname" : "localhost",
                            "port" : 8080,
                            "username" : "testusername1",
                            "password" : "password",
                            "active" : false
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
}
