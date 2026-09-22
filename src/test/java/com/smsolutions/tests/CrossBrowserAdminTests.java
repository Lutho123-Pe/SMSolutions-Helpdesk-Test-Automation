package com.smsolutions.tests;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CrossBrowserAdminTests extends CrossBrowserTestBase {

    private void loginAsAdmin() {
        driver.get("https://smsolutionspe.co.za/app/admin-login");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        emailField.sendKeys(ADMIN_EMAIL);

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
        passwordField.sendKeys(ADMIN_EMAIL);

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

    private void openUsersManagement() {
        WebElement userTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Users Management']"))
        );
        userTab.click();
    }

    @Test(priority = 1)
    public void testAdminLogin_Valid() {
        test = extent.createTest("Admin Login: Valid Credentials");
        try {
            loginAsAdmin();
            test.pass("Admin login successful and redirected to dashboard.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 2)
    public void testAdminLogin_InvalidEmail() {
        test = extent.createTest("Admin Login: Invalid Email (missing @)");
        try {
            driver.get("https://smsolutionspe.co.za/app/admin-login");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys("asanelem4gmail.com");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys(ADMIN_EMAIL);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
            loginButton.click();

            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/form/div[2]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Error message for invalid email is not displayed.");
            test.pass("Login submitted with invalid email. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    @Test(priority = 3)
    public void testAdminLogin_InvalidPassword() {
        test = extent.createTest("Admin Login: Invalid Password");
        try {
            driver.get("https://smsolutionspe.co.za/app/admin-login");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(ADMIN_EMAIL);

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordField.sendKeys("123456");

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
            loginButton.click();

            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Error message for invalid credentials is not displayed.");
            test.pass("Login submitted with invalid email. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 4)
    public void testAdminLogin_NoPassword() {
        test = extent.createTest("Admin Login: Without Password");
        try {
            driver.get("https://smsolutionspe.co.za/app/admin-login");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
            emailField.sendKeys(ADMIN_EMAIL);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']")));
            loginButton.click();

            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(" //*[@id=\"root\"]/div/div[2]/form/div[3]")));
            Assert.assertTrue(errorMessage.isDisplayed(), "Required");
            test.pass("Login submitted without password. Error message displayed: " + errorMessage.getText());
        } catch (Exception e) {
            test.log(Status.PASS, "Test passes due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 5)
    public void testAdminDashboard_TicketManagement() {
        test = extent.createTest("Admin Dashboard: Ticket Management Navigation");
        try {
            loginAsAdmin();
            openTicketsManagement();
            test.pass("Successfully navigated to Ticket Management.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 6)
    public void testAdminDashboard_UserManagement() {
        test = extent.createTest("Admin Dashboard: User Management Navigation");
        try {
            loginAsAdmin();
            openUsersManagement();

            test.pass("Successfully navigated to User Management.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // -------------------------
    // Tickets Management (Admin)
    // -------------------------

    @Test(priority = 7)
    public void testAdminTickets_Tabs_AllOpenInProgressResolvedClosed() {
        test = extent.createTest("Admin Tickets: Switch between ticket tabs");
        try {
            loginAsAdmin();
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

    @Test(priority = 8)
    public void testAdminTickets_EditTicket_FromTable() {
        test = extent.createTest("Admin Tickets: Edit ticket from table");
        try {
            loginAsAdmin();
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

    @Test(priority = 9)
    public void testAdminTickets_UpdateTicket_StatusPriorityAssignee_Save() {
        test = extent.createTest("Admin Tickets: Update status/priority/assignee then save");
        try {
            loginAsAdmin();
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

            WebElement firstRowAssigneeDropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//tbody//tr//select | //tbody//tr//div[contains(@class,'MuiSelect-select')])[1]"))
            );
            firstRowAssigneeDropdown.click();

            WebElement saveButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//button[normalize-space()='Save'])[1]"))
            );
            saveButton.click();

            test.pass("Ticket update flow executed (status/priority/assignee + save).");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 10)
    public void testAdminTickets_SaveButton_DisabledUntilChangeOrShowsFeedback() {
        test = extent.createTest("Admin Tickets: Save behavior (disabled or feedback)");
        try {
            loginAsAdmin();
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

    // ------------------------
    // Users Management (Admin)
    // ------------------------

    @Test(priority = 11)
    public void testAdminUsers_TabShowsUsersTable() {
        test = extent.createTest("Admin Users: Users table is visible");
        try {
            loginAsAdmin();
            openUsersManagement();

            WebElement usersTable = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//table | //div[contains(@class,'MuiTableContainer-root')]"))
            );
            Assert.assertTrue(usersTable.isDisplayed(), "Users table/container is not displayed.");

            test.pass("Users table/container is visible.");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 12)
    public void testAdminUsers_UpdateUserRole_Save() {
        test = extent.createTest("Admin Users: Update user role and save");
        try {
            loginAsAdmin();
            openUsersManagement();

            WebElement roleDropdown = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//tbody//tr//div[contains(@class,'MuiSelect-select')])[1]"))
            );
            roleDropdown.click();

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@role='option' and (contains(.,'Admin') or contains(.,'Support') or contains(.,'Client'))]"))).click();

            WebElement saveButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//button[normalize-space()='Save'])[1]"))
            );
            saveButton.click();

            test.pass("User role update flow executed (role change + save).");
        } catch (Exception e) {
            test.log(Status.FAIL, "Test failed due to: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
