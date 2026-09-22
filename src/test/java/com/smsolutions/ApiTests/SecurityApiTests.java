package com.smsolutions.ApiTests;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class SecurityApiTests extends ApiTestBase {
    @Test(description = "Verify admin endpoints require authentication")
    public void adminEndpointsRequireAuthentication() {
        test = extent.createTest("Security - Unauthorized Admin Access");
        for (String endpoint : new String[]{"/admin/dashboard", "/admin/tickets", "/admin/users"}) {
            response = given().when().get(endpoint).then().extract().response();
            logResponse(response);
            response.then().statusCode(anyOf(is(401), is(403)));
        }
    }

    @Test(description = "Verify invalid bearer tokens are rejected")
    public void invalidTokenIsRejected() {
        test = extent.createTest("Security - Invalid Token");
        response = givenWithToken("invalid.token.value").when().get("/admin/dashboard")
                .then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(401), is(403)));
    }

    @Test(description = "Verify SQL injection payloads cannot authenticate")
    public void sqlInjectionPayloadsAreRejected() {
        test = extent.createTest("Security - SQL Injection Prevention");
        for (String payload : new String[]{"' OR '1'='1", "admin'--", "' UNION SELECT * FROM users --"}) {
            response = given().body(Map.of("email", payload, "password", "anything"))
                    .when().post("/login").then().extract().response();
            logResponse(response);
            response.then().statusCode(anyOf(is(400), is(401), is(422)))
                    .body(not(containsStringIgnoringCase("sql")))
                    .body(not(containsStringIgnoringCase("syntax error")));
        }
    }

    @Test(description = "Verify repeated failed logins are rate limited")
    public void repeatedFailedLoginsAreRateLimited() {
        test = extent.createTest("Security - Login Rate Limiting");
        int rateLimitedResponses = 0;
        for (int i = 0; i < 15; i++) {
            response = given().body(Map.of("email", "rate-limit-test@example.com", "password", "wrong"))
                    .when().post("/login").then().extract().response();
            if (response.getStatusCode() == 429) rateLimitedResponses++;
        }
        Assert.assertTrue(rateLimitedResponses > 0,
                "Expected at least one HTTP 429 response after repeated failed logins");
    }
}
