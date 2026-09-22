# SM Solutions Helpdesk Test Automation

Java-based UI and REST API automation for the SM Solutions Helpdesk application. The project covers Client, Support, and Admin workflows using Selenium WebDriver, REST Assured, TestNG, Maven, WebDriverManager, and Extent Reports.

## API test coverage

The API suite covers authentication, registration, role-based dashboard access, malformed requests, unsupported content types, unknown endpoints, invalid tokens, unauthorized access, SQL-injection payload handling, and login rate limiting.

## Prerequisites

- Java 11 or newer
- Apache Maven 3.8+
- Chrome for the UI tests
- Network access to the configured test environment

## Secure configuration

Credentials are intentionally not stored in source control. Set the following environment variables before running authenticated API tests:

```bash
export SM_API_BASE_URL="https://smsolutionspe.co.za/app"
export SM_ADMIN_EMAIL="your-admin-email"
export SM_ADMIN_PASSWORD="your-admin-password"
export SM_CLIENT_EMAIL="your-client-email"
export SM_CLIENT_PASSWORD="your-client-password"
export SM_SUPPORT_EMAIL="your-support-email"
export SM_SUPPORT_PASSWORD="your-support-password"
```

You can override the base URL with a Maven property:

```bash
mvn test -Papi-tests -Dbase.url="https://your-test-environment.example/api"
```

Never commit `.env` files, passwords, bearer tokens, generated reports, or `target/` directories.

## Running the tests

Run the REST API suite:

```bash
mvn clean test -Papi-tests
```

Enable request and response logging only when debugging locally:

```bash
mvn test -Papi-tests -Dapi.debug=true
```

Run the browser suite with the existing TestNG configuration:

```bash
mvn clean test
```

API reports are generated under `target/` and are excluded from Git commits.

## Project structure

```text
src/test/java/com/smsolutions/ApiTests/
├── ApiTestBase.java
├── AuthenticationApiTests.java
├── ApiRegistrationTests.java
├── DashboardApiTests.java
├── NegativeApiTests.java
├── SecurityApiTests.java
└── UrlHealthCheckTests.java
```

## Engineering notes

The API tests use environment-driven configuration, explicit HTTP status assertions, structured JSON assertions, isolated test data for registration, and a dedicated Maven/TestNG execution profile. Debug logging is opt-in so credentials and response bodies are not written to normal CI output.
