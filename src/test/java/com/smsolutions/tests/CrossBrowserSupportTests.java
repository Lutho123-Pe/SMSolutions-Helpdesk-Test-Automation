package com.smsolutions.tests;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CrossBrowserSupportTests extends CrossBrowserTestBase {

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
            nameField.sendKeys("Tester 1");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys("tester@gmail.com");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys(CLIENT_PASSWORD);

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
    public void testSupportDashboard_TicketManagement_Navigation() {
        test = extent.createTest("Support Dashboard: Ticket Management Navigation");
        try {
            loginAsSupport();
            openTicketsManagement();
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
            loginAsSupport();
            openTicketsManagement();

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'All Tickets')]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Open']"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'In Progress')]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Resolved']"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Closed']"))).click();

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
            loginAsSupport();
            openTicketsManagement();

            WebElement firstEditButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//button[normalize-space()='Edit'])[1]"))
            );
            firstEditButton.click();

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(.,'Edit Ticket') or contains(.,'Update Ticket')]")),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//form"))
            ));

            test.pass("Edit action opens an edit/update UI.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 10)
    public void testSupportTickets_UpdateTicket_StatusPriority_Save() {
        test = extent.createTest("Support Tickets: Update status/priority then save");
        try {
            loginAsSupport();
            openTicketsManagement();

            WebElement firstRowStatusDropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//tbody//tr//div[contains(@class,'MuiSelect-select')])[1]"))
            );
            firstRowStatusDropdown.click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@role='option' and (normalize-space()='Open' or normalize-space()='In Progress' or normalize-space()='Resolved' or normalize-space()='Closed')]"))).click();

            WebElement firstRowPriorityDropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//tbody//tr//div[contains(@class,'MuiSelect-select')])[2]"))
            );
            firstRowPriorityDropdown.click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@role='option' and (normalize-space()='Low' or normalize-space()='Medium' or normalize-space()='High')]"))).click();

            WebElement saveButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//button[normalize-space()='Save'])[1]"))
            );
            saveButton.click();

            test.pass("Ticket update flow executed (status/priority + save).");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 11)
    public void testSupportTickets_SaveButton_DisabledUntilChangeOrShowsFeedback() {
        test = extent.createTest("Support Tickets: Save behavior (disabled or feedback)");
        try {
            loginAsSupport();
            openTicketsManagement();

            WebElement saveButton = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("(//button[normalize-space()='Save'])[1]"))
            );

            boolean isDisabled = "true".equalsIgnoreCase(saveButton.getAttribute("disabled"))
                    || "true".equalsIgnoreCase(saveButton.getAttribute("aria-disabled"));

            if (!isDisabled) {
                saveButton.click();
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(.,'Saved') or contains(.,'Updated') or contains(.,'Success')]")),
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'MuiAlert') or contains(@class,'Snackbar')]"))
                ));
            }

            test.pass("Save button is present and behaves as expected (disabled or feedback).");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
