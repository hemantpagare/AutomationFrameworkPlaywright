package locator;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LocatorParser {
    public static Locator getLocator(Page page, String locatorString) {
        if (locatorString.startsWith("id=")) {
            return page.locator("#" + locatorString.substring(3));
        } else if (locatorString.startsWith("xpath=")) {
            return page.locator(locatorString.substring(6));
        } else if (locatorString.startsWith("css=")) {
            return page.locator(locatorString.substring(4));
        } else if (locatorString.startsWith("text=")) {
            return page.locator("text=" + locatorString.substring(5));
        }
        // Default fallback to standard Playwright selector
        return page.locator(locatorString);
    }
}