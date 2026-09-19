package utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;

public class GenericAsserts {
    private static final Logger log = LoggerUtil.getLogger(GenericAsserts.class);

    public static void assertEquals(String actual, String expected, String message) {
        log.info("Asserting Equals -> Expected: [{}], Actual: [{}] | Message: {}", expected, actual, message);
        Assertions.assertEquals(expected, actual, message);
    }

    public static void assertTrue(boolean condition, String message) {
        log.info("Asserting True -> Condition: [{}] | Message: {}", condition, message);
        Assertions.assertTrue(condition, message);
    }

    public static void assertElementVisible(Locator locator, String message) {
        log.info("Asserting Element is Visible | Message: {}", message);
        PlaywrightAssertions.assertThat(locator).isVisible();
    }

    public static void assertElementContainsText(Locator locator, String expectedText) {
        log.info("Asserting Element contains text -> Expected: [{}]", expectedText);
        PlaywrightAssertions.assertThat(locator).containsText(expectedText);
    }

    public static void assertStatusCode(int actualStatusCode, int expectedStatusCode) {
        log.info("Asserting API Status Code -> Expected: [{}], Actual: [{}]", expectedStatusCode, actualStatusCode);
        Assertions.assertEquals(expectedStatusCode, actualStatusCode, "API Status code mismatch!");
    }
}