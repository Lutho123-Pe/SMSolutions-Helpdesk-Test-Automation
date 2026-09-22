package com.smsolutions.ApiTests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class AuthenticationApiTests extends ApiTestBase {
    @BeforeClass
    public void verifyConfiguration() { assertCredentialsConfigured(); }

    @Test(description = "Verify admin login with valid credentials")
    public void adminLoginWithValidCredentials() {
        test = extent.createTest("Admin Login - Valid Credentials");
        response = given().body(Map.of("email", ADMIN_EMAIL, "password", ADMIN_PASSWORD))
                .when().post("/admin-login").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(200), is(201))).contentType(containsString("json"))
                .body(anyOf(hasKey("token"), hasKey("accessToken")));
    }

    @Test(description = "Verify admin login rejects malformed email")
    public void adminLoginWithInvalidEmail() {
        test = extent.createTest("Admin Login - Invalid Email");
        response = given().body(Map.of("email", "invalid-email", "password", ADMIN_PASSWORD))
                .when().post("/admin-login").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(400), is(422))).body("message", notNullValue());
    }

    @Test(description = "Verify admin login requires a password")
    public void adminLoginWithoutPassword() {
        test = extent.createTest("Admin Login - Missing Password");
        response = given().body(Map.of("email", ADMIN_EMAIL))
                .when().post("/admin-login").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(400), is(422))).body("message", notNullValue());
    }

    @Test(description = "Verify client login with valid credentials")
    public void clientLoginWithValidCredentials() {
        test = extent.createTest("Client Login - Valid Credentials");
        response = given().body(Map.of("email", CLIENT_EMAIL, "password", CLIENT_PASSWORD))
                .when().post("/login").then().extract().response();
        logResponse(response);
        response.then().statusCode(200).body(anyOf(hasKey("token"), hasKey("accessToken")));
    }

    @Test(description = "Verify support login with valid credentials")
    public void supportLoginWithValidCredentials() {
        test = extent.createTest("Support Login - Valid Credentials");
        response = given().body(Map.of("email", SUPPORT_EMAIL, "password", SUPPORT_PASSWORD))
                .when().post("/login").then().extract().response();
        logResponse(response);
        response.then().statusCode(200).body(anyOf(hasKey("token"), hasKey("accessToken")));
    }
}
