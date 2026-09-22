package com.smsolutions.tests;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ClientTests extends BaseTest {

    @Override
    protected String getReportName() {
        return "extent-report-client.html";
    }

    @Test(priority = 1)
    public void testClientRegistration_Valid() {
        test = extent.createTest("Client Registration: Valid Credentials");
        try {
            driver.get("https://smsolutionspe.co.za/app/register");

            WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
            nameField.sendKeys("Alulutho Tokwe");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(CLIENT_EMAIL);

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys(CLIENT_PASSWORD);

            WebElement roleDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'MuiSelect-select')]")));
            roleDropdown.click();

            WebElement clientOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@role='option' and text()='Client']")));
            clientOption.click();

            WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Register']")));
            registerButton.click();

            // Verify successful registration by checking for a success message or redirection
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Error message for invalid email is  displayed.");
            test.pass("Registration submitted with invalid email. Error message displayed: " + errorMessage.getText());
            // Assuming successful registration redirects to login or shows a success message
            // For now, we'll check if the URL changes or a specific element appears
            // This might need adjustment based on actual application behavior after registration
            if (driver.getCurrentUrl().contains("login") || driver.getCurrentUrl().contains("dashboard")) {
                test.pass("Client registration form submitted successfully and redirected to login/dashboard.");
            } else {
                test.fail("Client registration form submitted but not redirected as expected. Current URL: " + driver.getCurrentUrl());
            }
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e); // Re-throw to mark test as failed in TestNG
        }
    }

    @Test(priority = 2)
    public void testClientRegistration_InvalidEmail() {
        test = extent.createTest("Client Registration: Invalid Email (missing @)");
        try {
            driver.get("https://smsolutionspe.co.za/app/register");

            WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
            nameField.sendKeys("Alulutho Tokwe");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys("tokwealuluthogmail.com");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys(CLIENT_PASSWORD);

            WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Register']")));
            registerButton.click();

            // Verify error message for invalid email
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[4]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Error message for invalid email is  displayed.");
            test.pass("Registration submitted with invalid email. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 3)
    public void testClientRegistration_EmptyPassword() {
        test = extent.createTest("Client Registration: Empty Password");
        try {
            driver.get("https://smsolutionspe.co.za/app/register");

            WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
            nameField.sendKeys("Alulutho Tokwe");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(CLIENT_EMAIL);

            // Do not enter password

            WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Register']")));
            registerButton.click();

            // Verify error message for empty password
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[6]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Required");
            test.pass("Registration submitted with empty password. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 4)
    public void testClientLogin_Valid() {
        test = extent.createTest("Client Login: Valid Credentials");
        try {
            driver.get("https://smsolutionspe.co.za/app/login");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(CLIENT_EMAIL);

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys(CLIENT_PASSWORD);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
            loginButton.click();

            // Verify successful login by checking for dashboard URL or element
            wait.until(ExpectedConditions.urlContains("dashboard"));
            test.pass("Login submitted with valid credentials. Redirected to dashboard.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 5)
    public void testClientLogin_NoPassword() {
        test = extent.createTest("Client Login: Without Password");
        try {
            driver.get("https://smsolutionspe.co.za/app/login");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(CLIENT_EMAIL);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
            loginButton.click();

            // Verify error message for empty password
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[3]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Required");
            test.pass("Login submitted without password. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.PASS, "Test Passed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
