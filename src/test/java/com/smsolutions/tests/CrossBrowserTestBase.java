package com.smsolutions.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CrossBrowserTestBase {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static ExtentReports extent;
    protected ExtentTest test;
    protected String browserName;

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

    private final String BASE_PATH = "C:\\Users\\Nozuko Tokwe\\Documents\\MS solutions Pe\\Helpdesk Test\\";
    private static String reportPath;

    @BeforeSuite
    public void setupExtent() {
        // Create report with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        reportPath = "cross-browser-test-report-" + timestamp + ".html";

        // Initialize ExtentSparkReporter
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

        // Configure the report appearance
        spark.config().setDocumentTitle("Cross Browser Test Execution Report");
        spark.config().setReportName("SM Solutions Cross Browser Test Results");
        spark.config().setTheme(Theme.DARK);  // DARK or STANDARD
        spark.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
        spark.config().setEncoding("utf-8");

        // Set which tabs to display and their order
        spark.viewConfigurer()
                .viewOrder()
                .as(new ViewName[]{
                        ViewName.DASHBOARD,
                        ViewName.TEST,
                        ViewName.CATEGORY,
                        ViewName.AUTHOR,
                        ViewName.DEVICE,
                        ViewName.LOG
                })
                .apply();

        // Initialize ExtentReports
        extent = new ExtentReports();
        extent.attachReporter(spark);

        // Add system information
        extent.setSystemInfo("Operating System", System.getProperty("os.name"));
        extent.setSystemInfo("OS Version", System.getProperty("os.version"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Browser(s) Tested", "Chrome, Edge, Firefox, Safari");
        extent.setSystemInfo("Environment", "Production");
        extent.setSystemInfo("Application URL", "https://smsolutionspe.co.za");

        // Add custom information
        extent.setSystemInfo("Test Suite", "Cross Browser Testing Suite");
        extent.setSystemInfo("Total Test Classes", "3 (Admin, Client, Support)");
        extent.setSystemInfo("Total Tests per Browser", "15");
        extent.setSystemInfo("Total Tests Overall", "45");

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     SM SOLUTIONS CROSS-BROWSER TEST EXECUTION            ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println(" Report will be generated at: " + reportPath);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        verifyDrivers();
    }

    private void verifyDrivers() {
        File chromeDriver = new File(BASE_PATH + "chromedriver.exe");
        File edgeDriver = new File(BASE_PATH + "msedgedriver.exe");
        File geckoDriver = new File(BASE_PATH + "geckodriver.exe");

        System.out.println("\n Driver Verification:");
        System.out.println("   Chrome Driver: " + (chromeDriver.exists() ? "✅" : "❌") + " " + BASE_PATH + "chromedriver.exe");
        System.out.println("   Edge Driver: " + (edgeDriver.exists() ? "✅" : "❌") + " " + BASE_PATH + "msedgedriver.exe");
        System.out.println("   Firefox Driver: " + (geckoDriver.exists() ? "✅" : "❌") + " " + BASE_PATH + "geckodriver.exe");
        System.out.println();
    }

    @BeforeMethod
    @Parameters("browser")
    public void setup(@Optional("chrome") String browser, ITestContext context) {
        this.browserName = browser;

        // Create test with browser as category
        String testName = context.getName() + " - " + browser.toUpperCase();
        test = extent.createTest(testName);

        // Add browser as category for filtering in report
        test.assignCategory(browser.toUpperCase());
        test.assignCategory(context.getName().replace(" Tests", ""));

        // Add device info
        test.assignDevice(browser.toUpperCase());

        // Add author
        test.assignAuthor("QA Team");

        test.info("Initializing " + browserName + " browser");
        System.out.println("\n▶ Starting test on: " + browserName.toUpperCase());

        try {
            switch (browserName.toLowerCase()) {
                case "firefox":
                    String firefoxPath = BASE_PATH + "geckodriver.exe";
                    verifyDriverExists(firefoxPath, "Firefox");
                    System.setProperty("webdriver.gecko.driver", firefoxPath);
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--no-sandbox");
                    driver = new FirefoxDriver(firefoxOptions);
                    test.pass("Firefox browser initialized successfully");
                    break;

                case "edge":
                    String edgePath = BASE_PATH + "msedgedriver.exe";
                    verifyDriverExists(edgePath, "Edge");
                    System.setProperty("webdriver.edge.driver", edgePath);
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--no-sandbox");
                    driver = new EdgeDriver(edgeOptions);
                    test.pass("Edge browser initialized successfully");
                    break;

                case "safari":
                    // SafariDriver is only available on macOS with Safari installed.
                    SafariOptions safariOptions = new SafariOptions();
                    driver = new SafariDriver(safariOptions);
                    test.pass("Safari browser initialized successfully");
                    break;

                case "chrome":
                default:
                    String chromePath = BASE_PATH + "chromedriver.exe";
                    verifyDriverExists(chromePath, "Chrome");
                    System.setProperty("webdriver.chrome.driver", chromePath);
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    driver = new ChromeDriver(chromeOptions);
                    test.pass("Chrome browser initialized successfully");
                    break;
            }

            driver.manage().window().maximize();
            wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            test.info("Browser window maximized");

        } catch (Exception e) {
            test.fail("Failed to initialize browser: " + e.getMessage());
            System.err.println("✗ Failed to start browser: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void verifyDriverExists(String path, String browserName) {
        File driverFile = new File(path);
        if (!driverFile.exists()) {
            String error = browserName + " driver not found at: " + path;
            System.err.println("✗ " + error);
            throw new RuntimeException(error);
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String status = result.getStatus() == ITestResult.SUCCESS ? "PASSED" :
                result.getStatus() == ITestResult.FAILURE ? "FAILED" : "SKIPPED";

        // Log test result to ExtentReport
        if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test " + status + " on " + browserName);
            System.out.println("  ✓ TEST PASSED: " + result.getName());
        } else if (result.getStatus() == ITestResult.FAILURE) {
            test.fail("Test " + status + " on " + browserName);
            test.fail("Error: " + result.getThrowable().getMessage());
            System.err.println("  ✗ TEST FAILED: " + result.getName());
            System.err.println("    Error: " + result.getThrowable().getMessage());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("Test " + status + " on " + browserName);
            System.out.println("  ⚠ TEST SKIPPED: " + result.getName());
        }

        // Add test duration
        test.info("Test Duration: " + (result.getEndMillis() - result.getStartMillis()) + " ms");

        if (driver != null) {
            driver.quit();
            test.info("Browser closed");
            System.out.println("  ✓ Browser closed: " + browserName);
        }
    }

    @AfterSuite
    public void tearDownExtent() {
        if (extent != null) {
            extent.flush();
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println(" EXTENT REPORT GENERATED SUCCESSFULLY!");
            System.out.println(" Report location: " + new File(reportPath).getAbsolutePath());
            System.out.println(" Report includes:");
            System.out.println("   • Dashboard with charts and graphs");
            System.out.println("   • Test execution timeline");
            System.out.println("   • Browser-wise test results");
            System.out.println("   • Category-wise test distribution");
            System.out.println("   • Pass/Fail/Skip statistics");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Open the report in any web browser to view");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
}
