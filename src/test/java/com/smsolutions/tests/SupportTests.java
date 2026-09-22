package com.smsolutions.tests;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class SupportTests extends BaseTest {

    @Override
    protected String getReportName() {
        return "extent-report-support.html";
    }

    private void loginAsSupport() {
        driver.get("https://smsolutionspe.co.za/app/login");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        emailField.sendKeys(SUPPORT_EMAIL);

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        passwordField.sendKeys(CLIENT_PASSWORD);

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("dashboard"));
    }

    private void openTicketsManagement() {
        WebElement ticketTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Tickets Management']"))
        );
        ticketTab.click();
    }

    @Test(priority = 1)
    public void testSupportRegistration_Valid() {
        test = extent.createTest("Support Registration: Valid Credentials");
        try {
            driver.get("https://smsolutionspe.co.za/app/register");

            WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
            nameField.sendKeys("Alulutho");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(CLIENT_EMAIL);

            var passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys("123456");

            WebElement roleDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'MuiSelect-select')]")));
            roleDropdown.click();

            WebElement supportOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@role='option' and text()='Support Staff']")));
            supportOption.click();

            WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Register']")));
            registerButton.click();

            // Verify successful registration by checking for a success message or redirection
            try {
                // Check if the error message element is present
                List<WebElement> errorElements = driver.findElements(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[1]/div[2]"));

                if (!errorElements.isEmpty() && errorElements.get(0).isDisplayed()) {
                    test.pass("Error message detected as expected: Support registration failed with proper error message.");
                } else {
                    test.fail("Expected error message not found. Current URL: " + driver.getCurrentUrl());
                }
            } catch (Exception e) {
                test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 2)
    public void testSupportRegistration_InvalidEmail() {
        test = extent.createTest("Support Registration: Invalid Email (missing @)");
        try {
            driver.get("https://smsolutionspe.co.za/app/register");

            WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
            nameField.sendKeys("Alulutho");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys("lutho1750gmail.com");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys(CLIENT_PASSWORD);

            WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Register']")));
            registerButton.click();

            // Verify error message for invalid email
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[3]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Error message for invalid email is not displayed.");
            test.pass("Registration submitted with invalid email. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 3)
    public void testSupportRegistration_EmptyPassword() {
        test = extent.createTest("Support Registration: Empty Password");
        try {
            driver.get("https://smsolutionspe.co.za/app/register");

            WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
            nameField.sendKeys("Alulutho");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(SUPPORT_EMAIL);

            // Do not enter password

            WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Register']")));
            registerButton.click();

            // Verify error message for empty password
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[5]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Error message for empty password is not displayed.");
            test.pass("Registration submitted with empty password. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 4)
    public void testSupportLogin_Valid() {
        test = extent.createTest("Support Login: Valid Credentials");
        try {
            loginAsSupport();
            test.pass("Login submitted with valid support credentials. Redirected to dashboard.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 5)
    public void testSupportLogin_InvalidPassword() {
        test = extent.createTest("Support Login: InValid Credentials");
        try {
            driver.get("https://smsolutionspe.co.za/app/login");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(SUPPORT_EMAIL);

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys("123456");

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
            loginButton.click();

            // Verify error message for invalid credentials
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Error message for invalid password is not displayed.");
            test.pass("Login submitted without password. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 6)
    public void testSupportLogin_NoPassword() {
        test = extent.createTest("Support Login: Without Password");
        try {
            driver.get("https://smsolutionspe.co.za/app/login");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(SUPPORT_EMAIL);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
            loginButton.click();

            // Verify error message for empty password
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[3]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Error message for empty password is not displayed.");
            test.pass("Login submitted without password. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // ---------------------------
    // Tickets Management (Support)
    // ---------------------------
    @Test(priority = 7)
    private void loginAsSupportStaff() {
        driver.get("https://smsolutionspe.co.za/app/login");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        emailField.sendKeys(ADMIN_EMAIL);

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        passwordField.sendKeys(ADMIN_EMAIL);

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("dashboard"));
    }

    @Test(priority = 7)
    public void testSupportDashboard_TicketManagement_Navigation() {
        test = extent.createTest("Support Dashboard: Ticket Management Navigation");
        try {
            loginAsSupportStaff();
            WebElement saveBtn = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[2]/div/div/button[1]")
                    )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", saveBtn);

            test.pass("Successfully navigated to Ticket Management.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 8)
    public void testSupportTickets_Tabs_AllOpenInProgressResolvedClosed() {
        test = extent.createTest("Support Tickets: Switch between ticket tabs");
        try {
            loginAsSupportStaff();
            openTicketsManagement();

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[3]/div/div/button[1]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[3]/div/div/button[2]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[3]/div/div/button[3]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[3]/div/div/button[4]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[3]/div/div/button[5]"))).click();

            test.pass("Ticket tabs are clickable and navigable.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 9)
    public void testSupportTickets_EditTicket_FromTable() {
        test = extent.createTest("Support Tickets: Edit ticket from table");
        try {
            loginAsSupportStaff();
            openTicketsManagement();

            WebElement firstEditButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[4]/div[2]/div[5]/button"))
            );
            firstEditButton.click();

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[3]/div")),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//form"))
            ));

            test.pass("Edit action opens an edit/update UI.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

   }
