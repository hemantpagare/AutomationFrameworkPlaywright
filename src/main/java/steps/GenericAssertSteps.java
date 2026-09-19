package steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import driver.DriverManager;
import io.cucumber.java.en.Then;
import locator.LocatorParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GenericAsserts;
import org.junit.jupiter.api.Assertions;

public class GenericAssertSteps {
    private static final Logger log = LoggerFactory.getLogger(GenericAssertSteps.class);

    @Then("user asserts locator {string} is visible")
    public void userAssertsLocatorIsVisible(String locatorExpr) {
        log.info("Asserting visibility for locator: {}", locatorExpr);
        Locator locator = LocatorParser.getLocator(DriverManager.getPage(), locatorExpr);
        GenericAsserts.assertElementVisible(locator, "Element should be visible: " + locatorExpr);
    }

    @Then("user asserts locator {string} contains text {string}")
    public void userAssertsLocatorContainsText(String locatorExpr, String expectedText) {
        log.info("Asserting locator [{}] contains text: [{}]", locatorExpr, expectedText);
        Locator locator = LocatorParser.getLocator(DriverManager.getPage(), locatorExpr);
        GenericAsserts.assertElementContainsText(locator, expectedText);
    }

    @Then("user asserts text {string} is displayed at locator {string}")
    public void userAssertsTextIsDisplayedAtLocator(String expectedText, String locatorExpr) {
        log.info("Asserting exact text [{}] is displayed at locator [{}]", expectedText, locatorExpr);
        Locator locator = LocatorParser.getLocator(DriverManager.getPage(), locatorExpr);
        PlaywrightAssertions.assertThat(locator).hasText(expectedText);
    }

    @Then("user asserts page title should be {string}")
    public void userAssertsPageTitleShouldBe(String expectedTitle) {
        log.info("Asserting page title. Expected: [{}]", expectedTitle);
        String actualTitle = DriverManager.getPage().title();
        GenericAsserts.assertEquals(actualTitle, expectedTitle, "Page title mismatch!");
    }
}