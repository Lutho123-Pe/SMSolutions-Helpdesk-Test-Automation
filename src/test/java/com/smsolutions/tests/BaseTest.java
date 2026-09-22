package com.smsolutions.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.time.Duration;

/**
 * Shared WebDriver and ExtentReports setup for single‑browser UI tests.
 * Support, Client and Admin UI tests should extend this class.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected final String ADMIN_EMAIL = secret("SM_ADMIN_EMAIL");
    protected final String ADMIN_PASSWORD = secret("SM_ADMIN_PASSWORD");
    protected final String CLIENT_EMAIL = secret("SM_CLIENT_EMAIL");
    protected final String CLIENT_PASSWORD = secret("SM_CLIENT_PASSWORD");
    protected final String SUPPORT_EMAIL = secret("SM_SUPPORT_EMAIL");
    protected final String SUPPORT_PASSWORD = secret("SM_SUPPORT_PASSWORD");

    private static String secret(String name) {
        return System.getProperty(name.toLowerCase().replace('_', '.'),
                System.getenv().getOrDefault(name, ""));
    }

    protected static ExtentReports extent;
    protected ExtentTest test;

    /**
     * Each concrete test class provides its own report file name.
     */
    protected abstract String getReportName();

    @BeforeSuite(alwaysRun = true)
    public void setupExtent() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(getReportName());
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setupDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownDriver(ITestResult result) {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownExtent() {
        if (extent != null) {
            extent.flush();
        }
    }
}
