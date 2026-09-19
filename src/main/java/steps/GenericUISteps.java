package steps;

import com.microsoft.playwright.Page;
import driver.DriverManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import locator.LocatorParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExcelDataReader;
import listeners.TestExecutionListener;

public class GenericUISteps {
    private static final Logger log = LoggerFactory.getLogger(GenericUISteps.class);

    @Given("user navigates to URL {string}")
    public void userNavigatesToUrl(String urlKey) {
        String url = config.ConfigurationManager.get(urlKey);
        if (url == null) url = urlKey;
        log.info("Navigating to URL: {}", url);
        DriverManager.getPage().navigate(url);
    }

    @And("user clicks on locator {string}")
    public void userClicksOnLocator(String locatorExpr) {
        log.info("Clicking on locator: {}", locatorExpr);
        LocatorParser.getLocator(DriverManager.getPage(), locatorExpr).click();
    }

    @And("user enters {string} into locator {string}")
    public void userEntersIntoLocator(String inputVal, String locatorKey) {
        String resolvedValue = inputVal;

        // If input starts with '#', look up value from Excel using current scenario's TC_ID
        if (inputVal.startsWith("#")) {
            String columnName = inputVal.substring(1); // Remove '#'
            String tcId = TestExecutionListener.getCurrentTcId();
            resolvedValue = ExcelDataReader.getData(tcId, columnName);
        }

        log.info("Entering value: [{}] into locator: {}", resolvedValue, locatorKey);

        // Perform the actual Playwright action using the resolved Excel data value
        Page page = DriverManager.getPage();
        if (page != null) {
            // Handle different locator strategies (e.g., id=xxx or CSS/XPath)
            if (locatorKey.startsWith("id=")) {
                String idValue = locatorKey.substring(3);
                page.locator("#" + idValue).fill(resolvedValue);
            } else {
                page.locator(locatorKey).fill(resolvedValue);
            }
        }
    }
}