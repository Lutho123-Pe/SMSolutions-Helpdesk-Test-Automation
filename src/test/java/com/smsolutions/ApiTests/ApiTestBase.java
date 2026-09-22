package com.smsolutions.ApiTests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApiTestBase {
    protected static ExtentReports extent;
    protected ExtentTest test;
    protected Response response;

    protected final String BASE_URL = System.getProperty(
            "base.url", System.getenv().getOrDefault("SM_API_BASE_URL", "https://smsolutionspe.co.za/app"));
    protected final String ADMIN_EMAIL = secret("SM_ADMIN_EMAIL");
    protected final String ADMIN_PASSWORD = secret("SM_ADMIN_PASSWORD");
    protected final String CLIENT_EMAIL = secret("SM_CLIENT_EMAIL");
    protected final String CLIENT_PASSWORD = secret("SM_CLIENT_PASSWORD");
    protected final String SUPPORT_EMAIL = secret("SM_SUPPORT_EMAIL");
    protected final String SUPPORT_PASSWORD = secret("SM_SUPPORT_PASSWORD");

    private static String secret(String name) {
        String property = name.toLowerCase().replace('_', '.');
        return System.getProperty(property, System.getenv().getOrDefault(name, ""));
    }

    @BeforeSuite
    public void setupExtent() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        ExtentSparkReporter spark = new ExtentSparkReporter("target/api-test-report-" + timestamp + ".html");
        spark.config().setDocumentTitle("API Test Execution Report");
        spark.config().setReportName("SM Solutions API Test Results");
        spark.config().setTheme(Theme.STANDARD);

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Base URL", BASE_URL);
        extent.setSystemInfo("Environment", System.getProperty("test.env", "local"));
        extent.setSystemInfo("Test Type", "REST API Testing");
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));

        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.STATUS);
    }

    protected void assertCredentialsConfigured() {
        Assert.assertFalse(ADMIN_EMAIL.isBlank() || ADMIN_PASSWORD.isBlank(),
                "API credentials are not configured. Set the required SM_* environment variables.");
    }

    protected RequestSpecification given() {
        RequestSpecification specification = RestAssured.given()
                .header("Accept", "application/json")
                .header("User-Agent", "SM-Solutions-API-Tests")
                .contentType("application/json");
        if (Boolean.parseBoolean(System.getProperty("api.debug", "false"))) {
            specification.filter(new RequestLoggingFilter()).filter(new ResponseLoggingFilter());
        }
        return specification;
    }

    protected RequestSpecification givenWithToken(String token) {
        Assert.assertNotNull(token, "Bearer token must not be null");
        return given().header("Authorization", "Bearer " + token);
    }

    protected void logResponse(Response response) {
        test.info("Status Code: " + response.getStatusCode());
        test.info("Response Time: " + response.getTime() + " ms");
        if (Boolean.parseBoolean(System.getProperty("api.debug", "false"))) {
            test.info("Response Body: " + response.getBody().asPrettyString());
        }
    }

    protected String extractToken(Response response) {
        String token = response.jsonPath().getString("token");
        return token != null ? token : response.jsonPath().getString("accessToken");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (test == null) return;
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test failed: " +
                    (result.getThrowable() == null ? "unknown error" : result.getThrowable().getMessage()));
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Test skipped");
        }
    }

    @AfterSuite
    public void tearDownExtent() {
        if (extent != null) extent.flush();
    }
}
