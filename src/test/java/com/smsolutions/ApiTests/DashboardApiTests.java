package com.smsolutions.ApiTests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class DashboardApiTests extends ApiTestBase {
    private String adminToken;
    private String clientToken;
    private String supportToken;

    @BeforeClass
    public void setupTokens() {
        assertCredentialsConfigured();
        adminToken = login(ADMIN_EMAIL, ADMIN_PASSWORD, "/admin-login");
        clientToken = login(CLIENT_EMAIL, CLIENT_PASSWORD, "/login");
        supportToken = login(SUPPORT_EMAIL, SUPPORT_PASSWORD, "/login");
    }

    private String login(String email, String password, String endpoint) {
        Response loginResponse = given().body(Map.of("email", email, "password", password))
                .when().post(endpoint).then().statusCode(anyOf(is(200), is(201))).extract().response();
        String token = extractToken(loginResponse);
        Assert.assertNotNull(token, "Authentication response must contain a token");
        return token;
    }

    @Test(description = "Verify admin dashboard access")
    public void adminDashboardAccess() {
        test = extent.createTest("Admin Dashboard - Authorized Access");
        response = givenWithToken(adminToken).when().get("/admin/dashboard").then().extract().response();
        logResponse(response);
        response.then().statusCode(200);
    }

    @Test(description = "Verify admin ticket management access")
    public void adminTicketManagementAccess() {
        test = extent.createTest("Admin Tickets - Authorized Access");
        response = givenWithToken(adminToken).when().get("/admin/tickets").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(200), is(204)));
    }

    @Test(description = "Verify admin user management access")
    public void adminUserManagementAccess() {
        test = extent.createTest("Admin Users - Authorized Access");
        response = givenWithToken(adminToken).when().get("/admin/users").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(200), is(204)));
    }

    @Test(description = "Verify client dashboard access")
    public void clientDashboardAccess() {
        test = extent.createTest("Client Dashboard - Authorized Access");
        response = givenWithToken(clientToken).when().get("/client/dashboard").then().extract().response();
        logResponse(response);
        response.then().statusCode(200);
    }

    @Test(description = "Verify support dashboard access")
    public void supportDashboardAccess() {
        test = extent.createTest("Support Dashboard - Authorized Access");
        response = givenWithToken(supportToken).when().get("/support/dashboard").then().extract().response();
        logResponse(response);
        response.then().statusCode(200);
    }
}
