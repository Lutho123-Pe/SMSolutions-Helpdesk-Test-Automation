package com.smsolutions.ApiTests;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class ApiRegistrationTests extends ApiTestBase {
    private Map<String, Object> registration(String email, String password, String role) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "API Test User");
        body.put("email", email);
        body.put("password", password);
        body.put("role", role);
        return body;
    }

    @Test(description = "Verify client registration accepts valid unique data")
    public void clientRegistrationWithValidData() {
        test = extent.createTest("Client Registration - Valid Unique Data");
        String email = "api-test-" + System.currentTimeMillis() + "@example.com";
        response = given().body(registration(email, "Test@12345", "Client"))
                .when().post("/register").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(200), is(201))).body(anyOf(hasKey("message"), hasKey("user")));
    }

    @Test(description = "Verify registration rejects malformed email")
    public void registrationWithInvalidEmail() {
        test = extent.createTest("Client Registration - Invalid Email");
        response = given().body(registration("invalid-email", "Test@12345", "Client"))
                .when().post("/register").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(400), is(422))).body("message", notNullValue());
    }

    @Test(description = "Verify registration requires a password")
    public void registrationWithoutPassword() {
        test = extent.createTest("Client Registration - Missing Password");
        response = given().body(registration("missing-password@example.com", "", "Client"))
                .when().post("/register").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(400), is(422))).body("message", notNullValue());
    }

    @Test(description = "Verify duplicate registration is rejected")
    public void duplicateRegistrationIsRejected() {
        test = extent.createTest("Client Registration - Duplicate Email");
        String email = "duplicate-test-" + System.currentTimeMillis() + "@example.com";
        given().body(registration(email, "Test@12345", "Client")).when().post("/register");
        response = given().body(registration(email, "Test@12345", "Client"))
                .when().post("/register").then().extract().response();
        logResponse(response);
        response.then().statusCode(409).body("message", notNullValue());
    }
}
