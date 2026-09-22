package com.smsolutions.ApiTests;

import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class NegativeApiTests extends ApiTestBase {
    @Test(description = "Verify malformed JSON is rejected")
    public void malformedJsonIsRejected() {
        test = extent.createTest("Negative - Malformed JSON");
        response = given().body("{\"email\": \"test@example.com\"")
                .when().post("/login").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(400), is(422))).body("message", notNullValue());
    }

    @Test(description = "Verify an empty request body is rejected")
    public void emptyBodyIsRejected() {
        test = extent.createTest("Negative - Empty Request Body");
        response = given().body("").when().post("/login").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(400), is(422))).body("message", notNullValue());
    }

    @Test(description = "Verify unsupported content type is rejected")
    public void unsupportedContentTypeIsRejected() {
        test = extent.createTest("Negative - Unsupported Content Type");
        response = io.restassured.RestAssured.given().contentType("text/plain")
                .body("email=test@example.com&password=test")
                .when().post("/login").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(400), is(415), is(422)));
    }

    @Test(description = "Verify an invalid endpoint returns not found")
    public void unknownEndpointReturnsNotFound() {
        test = extent.createTest("Negative - Unknown Endpoint");
        response = given().when().get("/endpoint-that-does-not-exist").then().extract().response();
        logResponse(response);
        response.then().statusCode(404);
    }

    @Test(description = "Verify malformed login data is rejected")
    public void invalidLoginDataIsRejected() {
        test = extent.createTest("Negative - Invalid Login Data");
        response = given().body(Map.of("email", "not-an-email", "password", "short"))
                .when().post("/login").then().extract().response();
        logResponse(response);
        response.then().statusCode(anyOf(is(400), is(401), is(422)))
                .body(not(containsStringIgnoringCase("sql")));
    }
}
