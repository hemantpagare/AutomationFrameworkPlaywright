package listeners;

import driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExcelDataReader;

import java.util.Collection;

public class TestExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(TestExecutionListener.class);
    private static ThreadLocal<String> currentTcId = new ThreadLocal<>();

    @Before
    public void beforeScenario(Scenario scenario) {
        log.info("Starting Scenario: {}", scenario.getName());

        // 1. Load Excel test data from VM argument (defaults if not provided)
        String excelPath = System.getProperty("testDataFile", "src/test/resources/TestData.xlsx");
        try {
            ExcelDataReader.loadExcelData(excelPath);
        } catch (Exception e) {
            log.warn("Could not load Excel file from path [{}]: {}", excelPath, e.getMessage());
        }

        // 2. Extract Scenario TC ID tag (e.g., @TC01 -> TC01)
        Collection<String> tags = scenario.getSourceTagNames();
        for (String tag : tags) {
            if (tag.matches("(?i)@?TC\\d+")) {
                currentTcId.set(tag.replace("@", "").toUpperCase());
                log.info("Mapped Test Case ID [{}] for scenario: {}", currentTcId.get(), scenario.getName());
                break;
            }
        }
    }

    public static String getCurrentTcId() {
        return currentTcId.get();
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        // 3. Capture screenshot after every step for Extent Report
        try {
            Page page = DriverManager.getPage();
            if (page != null) {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(false));
                scenario.attach(screenshot, "image/png", "Step Snapshot");
            }
        } catch (Exception e) {
            log.warn("Could not capture step screenshot: {}", e.getMessage());
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        // 4. Handle failures and clean up ThreadLocal context
        if (scenario.isFailed()) {
            log.error("Scenario Failed: {}", scenario.getName());
            try {
                Page page = DriverManager.getPage();
                if (page != null) {
                    byte[] failureScreenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                    scenario.attach(failureScreenshot, "image/png", "Failure Snapshot");
                }
            } catch (Exception e) {
                log.warn("Could not capture failure screenshot: {}", e.getMessage());
            }
        }
        currentTcId.remove();
        DriverManager.closeAll();
    }
}