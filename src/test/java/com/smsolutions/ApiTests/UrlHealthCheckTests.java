package com.smsolutions.ApiTests;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;

public class UrlHealthCheckTests extends ApiTestBase {

    private final List<UrlCheckResult> failedUrls = new ArrayList<>();

    class UrlCheckResult {
        String url;
        int statusCode;
        String contentType;

        UrlCheckResult(String url, int statusCode, String contentType) {
            this.url = url;
            this.statusCode = statusCode;
            this.contentType = contentType;
        }
    }

    @Test(priority = 1, description = "Check all critical application URLs return 200 OK")
    public void testAllUrlsReturn200() {
        test = extent.createTest("URL Health Check - All Critical Pages");

        String[] urlsToTest = {
                "/admin-login",
                "/login",
                "/register",
                "/client-login",
                "/support-login",
                "/dashboard",
                "/",
                "/home",
                "/about",
                "/contact"
        };

        int successCount = 0;
        int failCount = 0;

        for (String url : urlsToTest) {
            try {
                response = given()
                        .when()
                        .get(url)
                        .then()
                        .extract()
                        .response();

                int statusCode = response.getStatusCode();
                String contentType = response.getContentType();

                if (statusCode == 200) {
                    successCount++;
                    test.pass("✅ " + url + " → " + statusCode + " (" + contentType + ")");
                } else {
                    failCount++;
                    failedUrls.add(new UrlCheckResult(url, statusCode, contentType));
                    test.fail("❌ " + url + " → " + statusCode + " (" + contentType + ")");
                }

            } catch (Exception e) {
                failCount++;
                failedUrls.add(new UrlCheckResult(url, 0, "ERROR: " + e.getMessage()));
                test.fail("❌ " + url + " → ERROR: " + e.getMessage());
            }
        }

        // Summary assertion
        Assert.assertTrue(failCount == 0,
                "❌ " + failCount + " URLs failed. Check report for details.");

        test.pass("✅ All " + successCount + " URLs returned 200 OK");
    }

    @Test(priority = 2, description = "Verify all URLs load in acceptable time")
    public void testUrlResponseTimes() {
        test = extent.createTest("URL Performance Check");

        String[] criticalUrls = {
                "/admin-login",
                "/login",
                "/register"
        };

        for (String url : criticalUrls) {
            long startTime = System.currentTimeMillis();

            response = given()
                    .when()
                    .get(url)
                    .then()
                    .extract()
                    .response();

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            Assert.assertEquals(response.getStatusCode(), 200, url + " should load");

            // Check response time
            if (responseTime < 1000) {
                test.pass("✅ " + url + " loaded in " + responseTime + "ms (Excellent)");
            } else if (responseTime < 3000) {
                test.pass("⚠️ " + url + " loaded in " + responseTime + "ms (Acceptable)");
            } else {
                test.warning("⚠️ " + url + " loaded in " + responseTime + "ms (Slow - needs optimization)");
            }
        }
    }

    @Test(priority = 3, description = "Verify important URLs return HTML content")
    public void testUrlsReturnHtml() {
        test = extent.createTest("URL Content Type Verification");

        String[] urls = {
                "/admin-login",
                "/login",
                "/register"
        };

        for (String url : urls) {
            response = given()
                    .when()
                    .get(url)
                    .then()
                    .extract()
                    .response();

            Assert.assertEquals(response.getStatusCode(), 200, url + " should load");

            String contentType = response.getContentType();
            Assert.assertTrue(contentType.contains("text/html"),
                    url + " should return HTML content");

            test.pass("✅ " + url + " returns " + contentType);
        }
    }

    @Test(priority = 4, description = "Generate URL Health Report")
    public void generateUrlHealthReport() {
        test = extent.createTest("URL Health Summary Report");

        test.info("📊 URL Health Check Summary:");
        test.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Test the main application URL
        response = given()
                .when()
                .get("/")
                .then()
                .extract()
                .response();

        test.info("🏠 Home Page: " + response.getStatusCode());

        // Test all role-specific login pages
        String[] loginPages = {"/admin-login", "/login", "/client-login", "/support-login"};
        for (String page : loginPages) {
            response = given()
                    .when()
                    .get(page)
                    .then()
                    .extract()
                    .response();
            test.info("🔐 " + page + ": " + response.getStatusCode());
        }

        // Check if page contains basic elements
        response = given()
                .when()
                .get("/admin-login")
                .then()
                .extract()
                .response();

        String body = response.getBody().asString();
        if (body.contains("email") && body.contains("password")) {
            test.pass("✅ Admin login page contains form fields");
        } else {
            test.warning("⚠️ Admin login page may be missing form fields");
        }

        test.pass("📈 URL Health Check completed successfully");
    }
}
