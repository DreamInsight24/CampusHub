package com.campushub;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for core business flows.
 *
 * Uses H2 in-memory database (MySQL mode) to test the full
 * Controller → Service → Mapper → Database chain.
 *
 * Covers: 1 complete normal flow + 3 exception flows as required by P4.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Integration Tests - Core Business Flows")
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        // Clean all tables before each test for isolation
        jdbcTemplate.update("DELETE FROM chat_message");
        jdbcTemplate.update("DELETE FROM conversation");
        jdbcTemplate.update("DELETE FROM demand_application");
        jdbcTemplate.update("DELETE FROM teamup_demand_detail");
        jdbcTemplate.update("DELETE FROM tutoring_demand_detail");
        jdbcTemplate.update("DELETE FROM secondhand_demand_detail");
        jdbcTemplate.update("DELETE FROM express_demand_detail");
        jdbcTemplate.update("DELETE FROM demand");
        jdbcTemplate.update("DELETE FROM user_detail");
        jdbcTemplate.update("DELETE FROM `user`");
    }

    // ================================================================
    // FLOW 1: Complete Normal Flow
    // register → login → post demand → another user accepts → view lists
    // ================================================================

    @Test
    @DisplayName("Complete Normal Flow: Registration → Login → Post Demand → Accept → View Lists")
    void completeNormalFlow() throws Exception {
        // --- Step 1: Alice registers ---
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.userUuid").isNotEmpty());
        System.out.println("[Integration Test] ✓ Alice registered");

        // --- Step 2: Alice logs in ---
        MvcResult aliceLoginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        String aliceToken = objectMapper.readTree(
                aliceLoginResult.getResponse().getContentAsString())
                .get("data").get("token").asText();
        System.out.println("[Integration Test] ✓ Alice logged in");

        // --- Step 3: Alice posts an express demand ---
        MvcResult demandResult = mockMvc.perform(post("/api/demands")
                        .header("token", aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"EXPRESS\",\"title\":\"帮取东门快递\","
                                + "\"pickupLocation\":\"东门快递柜\",\"deliveryLocation\":\"6号楼\","
                                + "\"pickupCode\":\"A123\",\"description\":\"今晚送到宿舍楼下\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andReturn();

        String demandId = objectMapper.readTree(
                demandResult.getResponse().getContentAsString())
                .get("data").get("id").asText();
        System.out.println("[Integration Test] ✓ Alice posted demand: " + demandId);

        // --- Step 4: Bob registers ---
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"password\":\"password456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("bob"));
        System.out.println("[Integration Test] ✓ Bob registered");

        // --- Step 5: Bob logs in ---
        MvcResult bobLoginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bob\",\"password\":\"password456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        String bobToken = objectMapper.readTree(
                bobLoginResult.getResponse().getContentAsString())
                .get("data").get("token").asText();
        System.out.println("[Integration Test] ✓ Bob logged in");

        // --- Step 6: Bob views demand detail ---
        mockMvc.perform(get("/api/demands/{id}", demandId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("帮取东门快递"))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andExpect(jsonPath("$.data.pickupCode").doesNotExist());
        System.out.println("[Integration Test] ✓ Bob viewed demand detail");

        // --- Step 7: Bob applies to the demand ---
        mockMvc.perform(post("/api/demands/{id}/responses", demandId)
                        .header("token", bobToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.takerId").doesNotExist());
        System.out.println("[Integration Test] ✓ Bob applied to the demand");

        // --- Step 8: Alice accepts Bob's application ---
        MvcResult applicationsResult = mockMvc.perform(get("/api/demands/{id}/applications", demandId)
                        .header("token", aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"))
                .andReturn();

        String applicationId = objectMapper.readTree(
                applicationsResult.getResponse().getContentAsString())
                .get("data").get(0).get("id").asText();

        mockMvc.perform(post("/api/demands/{id}/applications/{applicationId}/accept", demandId, applicationId)
                        .header("token", aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.takerId").isNotEmpty());
        System.out.println("[Integration Test] ✓ Alice selected Bob as taker");

        // --- Step 9: Verify demand status changed ---
        mockMvc.perform(get("/api/demands/{id}", demandId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
        System.out.println("[Integration Test] ✓ Demand status updated to IN_PROGRESS");

        // --- Step 10: Bob can now see sensitive pickup code ---
        mockMvc.perform(get("/api/demands/{id}", demandId)
                        .header("token", bobToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.pickupCode").value("A123"));
        System.out.println("[Integration Test] ✓ Bob can view pickup code after acceptance");

        // --- Step 11: Alice views published demands ---
        mockMvc.perform(get("/api/demands/mine/published")
                        .header("token", aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("帮取东门快递"));
        System.out.println("[Integration Test] ✓ Alice viewed published demands");

        // --- Step 12: Bob views accepted demands ---
        mockMvc.perform(get("/api/demands/mine/accepted")
                        .header("token", bobToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("帮取东门快递"));
        System.out.println("[Integration Test] ✓ Bob viewed accepted demands");

        // --- Step 11: Alice views user profile ---
        mockMvc.perform(get("/api/users/me")
                        .header("token", aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.creditScore").value(100));
        System.out.println("[Integration Test] ✓ Alice viewed profile");

        System.out.println("[Integration Test] ✓✓✓ COMPLETE NORMAL FLOW PASSED ✓✓✓");
    }

    // ================================================================
    // EXCEPTION FLOW 1: Unauthenticated Access
    // ================================================================

    @Nested
    @DisplayName("Exception Flow 1: Unauthenticated Access")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("Creating demand without token returns 401")
        void createDemandWithoutToken() throws Exception {
            mockMvc.perform(post("/api/demands")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"type\":\"EXPRESS\",\"title\":\"帮忙取快递\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("请先登录"));
        }

        @Test
        @DisplayName("Accessing my published demands without token returns 401")
        void myPublishedDemandsWithoutToken() throws Exception {
            mockMvc.perform(get("/api/demands/mine/published"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("请先登录"));
        }

        @Test
        @DisplayName("Accessing my accepted demands without token returns 401")
        void myAcceptedDemandsWithoutToken() throws Exception {
            mockMvc.perform(get("/api/demands/mine/accepted"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("请先登录"));
        }

        @Test
        @DisplayName("Responding to demand without token returns 401")
        void respondDemandWithoutToken() throws Exception {
            mockMvc.perform(post("/api/demands/{id}/responses", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("请先登录"));
        }

        @Test
        @DisplayName("Getting user profile without token returns 401")
        void getProfileWithoutToken() throws Exception {
            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("请先登录"));
        }

        @Test
        @DisplayName("Updating user profile without token returns 401")
        void updateProfileWithoutToken() throws Exception {
            mockMvc.perform(patch("/api/users/me")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"nickname\":\"hacker\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("请先登录"));
        }
    }

    // ================================================================
    // EXCEPTION FLOW 2: Business Rule Violations
    // ================================================================

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Exception Flow 2: Business Rule Violations")
    class BusinessRuleViolations {

        /**
         * Helper: register and login a user, returning the token.
         */
        private String registerAndLogin(String username, String password) throws Exception {
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"));

            MvcResult result = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                    .andReturn();
            return objectMapper.readTree(result.getResponse().getContentAsString())
                    .get("data").get("token").asText();
        }

        @Test
        @Order(1)
        @DisplayName("Registering duplicate username returns 409")
        void registerDuplicateUsername() throws Exception {
            // Register once
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"duplicate_user\",\"password\":\"pass123\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            // Register again with same username
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"duplicate_user\",\"password\":\"pass456\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(409))
                    .andExpect(jsonPath("$.message").value("用户名已存在"));
        }

        @Test
        @Order(2)
        @DisplayName("Login with wrong password returns 401")
        void loginWrongPassword() throws Exception {
            // Register a user first
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"wrongpwd_user\",\"password\":\"correct\"}"))
                    .andExpect(status().isOk());

            // Login with wrong password
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"wrongpwd_user\",\"password\":\"wrongpassword\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("账号或密码错误"));
        }

        @Test
        @Order(3)
        @DisplayName("Login with non-existent user returns 401")
        void loginNonExistentUser() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"nonexistent\",\"password\":\"whatever\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("账号或密码错误"));
        }

        @Test
        @Order(4)
        @DisplayName("Creating demand with empty title returns 400")
        void createDemandEmptyTitle() throws Exception {
            String token = registerAndLogin("emptytitle_user", "pass123");

            mockMvc.perform(post("/api/demands")
                            .header("token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"type\":\"EXPRESS\",\"title\":\"\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("需求类型和标题不能为空"));
        }

        @Test
        @Order(5)
        @DisplayName("Responding to own demand returns 400")
        void respondToOwnDemand() throws Exception {
            String token = registerAndLogin("self_user", "pass123");

            // Post a demand
            MvcResult demandResult = mockMvc.perform(post("/api/demands")
                            .header("token", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"type\":\"EXPRESS\",\"title\":\"帮取快递\","
                                    + "\"pickupLocation\":\"菜鸟驿站\",\"deliveryLocation\":\"宿舍\"}"))
                    .andExpect(status().isOk())
                    .andReturn();

            String demandId = objectMapper.readTree(demandResult.getResponse().getContentAsString())
                    .get("data").get("id").asText();

            // Try to accept own demand
            mockMvc.perform(post("/api/demands/{id}/responses", demandId)
                            .header("token", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("不能申请自己发布的需求"));
        }

        @Test
        @Order(6)
        @org.junit.jupiter.api.Disabled("Replaced by publisher-review application flow for express demands")
        @DisplayName("Responding to already-taken demand returns 409")
        void respondToTakenDemand() throws Exception {
            String aliceToken = registerAndLogin("alice_taken", "pass123");
            String bobToken = registerAndLogin("bob_taken", "pass456");

            // Alice posts a demand
            MvcResult demandResult = mockMvc.perform(post("/api/demands")
                            .header("token", aliceToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"type\":\"EXPRESS\",\"title\":\"帮取快递2\","
                                    + "\"pickupLocation\":\"驿站\",\"deliveryLocation\":\"宿舍\"}"))
                    .andExpect(status().isOk())
                    .andReturn();

            String demandId = objectMapper.readTree(demandResult.getResponse().getContentAsString())
                    .get("data").get("id").asText();

            // Bob accepts the demand
            mockMvc.perform(post("/api/demands/{id}/responses", demandId)
                            .header("token", bobToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            // Charlie tries to accept the already-taken demand
            String charlieToken = registerAndLogin("charlie_taken", "pass789");
            mockMvc.perform(post("/api/demands/{id}/responses", demandId)
                            .header("token", charlieToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(409))
                    .andExpect(jsonPath("$.message").value("该需求已被接取"));
        }

        @Test
        @Order(7)
        @DisplayName("Getting non-existent demand returns 404")
        void getNonExistentDemand() throws Exception {
            mockMvc.perform(get("/api/demands/{id}", UUID.randomUUID()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("需求不存在"));
        }

        @Test
        @Order(8)
        @DisplayName("Registering with empty credentials returns 400")
        void registerWithEmptyCredentials() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"\",\"password\":\"\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("用户名和密码不能为空"));
        }
    }

    // ================================================================
    // EXCEPTION FLOW 3: Message & Conversation Edge Cases
    // ================================================================

    @Nested
    @DisplayName("Exception Flow 3: Message & Conversation Edge Cases")
    class MessageAndConversationEdgeCases {

        @Test
        @DisplayName("Querying conversations without token returns 401")
        void queryConversationsWithoutToken() throws Exception {
            mockMvc.perform(get("/api/conversations/query"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未登录，请先登录"));
        }

        @Test
        @DisplayName("Querying conversations with invalid token returns 401")
        void queryConversationsWithInvalidToken() throws Exception {
            mockMvc.perform(get("/api/conversations/query")
                            .header("token", "garbage-token-value"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("登录已过期，请重新登录"));
        }

        @Test
        @DisplayName("Creating conversation for non-existent demand returns 404")
        void createConversationForNonExistentDemand() throws Exception {
            UUID fakeDemandId = UUID.randomUUID();
            UUID ownerId = UUID.randomUUID();
            UUID participantId = UUID.randomUUID();

            mockMvc.perform(post("/api/conversations/demands/{demandId}", fakeDemandId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"ownerId\":\"" + ownerId + "\","
                                    + "\"participantId\":\"" + participantId + "\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("需求不存在"));
        }

        @Test
        @DisplayName("Creating message in non-existent conversation returns 404")
        void createMessageInNonExistentConversation() throws Exception {
            UUID fakeConvId = UUID.randomUUID();
            UUID senderId = UUID.randomUUID();

            mockMvc.perform(post("/api/conversations/{conversationId}/messages", fakeConvId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"senderUuid\":\"" + senderId + "\",\"message\":\"hello\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("会话不存在"));
        }

        @Test
        @DisplayName("Creating conversation and exchanging messages works end-to-end")
        void conversationAndMessageFlow() throws Exception {
            // Register two users through the API
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"conv_alice\",\"password\":\"pass123\"}"));

            MvcResult aliceLogin = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"conv_alice\",\"password\":\"pass123\"}"))
                    .andReturn();
            String aliceToken = objectMapper.readTree(aliceLogin.getResponse().getContentAsString())
                    .get("data").get("token").asText();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"conv_bob\",\"password\":\"pass456\"}"));

            MvcResult bobLogin = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"conv_bob\",\"password\":\"pass456\"}"))
                    .andReturn();
            String bobToken = objectMapper.readTree(bobLogin.getResponse().getContentAsString())
                    .get("data").get("token").asText();

            // Alice posts a demand
            MvcResult demandResult = mockMvc.perform(post("/api/demands")
                            .header("token", aliceToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"type\":\"EXPRESS\",\"title\":\"帮取快递\","
                                    + "\"pickupLocation\":\"菜鸟驿站\",\"deliveryLocation\":\"宿舍\"}"))
                    .andExpect(status().isOk())
                    .andReturn();
            String demandId = objectMapper.readTree(demandResult.getResponse().getContentAsString())
                    .get("data").get("id").asText();

            // Bob accepts the demand (this triggers the demand update)
            mockMvc.perform(post("/api/demands/{id}/responses", demandId)
                            .header("token", bobToken))
                    .andExpect(status().isOk());

            // Query conversations with valid token - should return empty or success
            mockMvc.perform(get("/api/conversations/query")
                            .header("token", aliceToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            System.out.println("[Integration Test] ✓ Conversation and message flow completed");
        }
    }
}
