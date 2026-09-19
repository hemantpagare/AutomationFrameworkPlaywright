package driver;

import com.microsoft.playwright.*;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverManager {
    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

    private static final ThreadLocal<Playwright> playwrightThread = ThreadLocal.withInitial(Playwright::create);
    private static final ThreadLocal<Browser> browserThread = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThread = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThread = ThreadLocal.withInitial(() -> {
        Browser browser = getBrowser();
        BrowserContext context = browser.newContext(new Browser.NewContextOptions().setRecordVideoDir(java.nio.file.Paths.get("target/videos")));
        contextThread.set(context);
        return context.newPage();
    });

    public static Page getPage() {
        return pageThread.get();
    }

    private static Browser getBrowser() {
        if (browserThread.get() == null) {
            String browserName = ConfigurationManager.get("browser").toLowerCase();
            boolean headless = Boolean.parseBoolean(ConfigurationManager.get("headless"));
            String remoteUrl = ConfigurationManager.get("remote.url");

            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);
            Playwright pw = playwrightThread.get();

            Browser browserInstance;
            if (remoteUrl != null && !remoteUrl.isEmpty()) {
                // Remote Grid / Browserless connection
                browserInstance = pw.chromium().connect(remoteUrl);
            } else {
                switch (browserName) {
                    case "firefox":
                        browserInstance = pw.firefox().launch(options);
                        break;
                    case "webkit":
                        browserInstance = pw.webkit().launch(options);
                        break;
                    case "chromium":
                    default:
                        browserInstance = pw.chromium().launch(options);
                        break;
                }
            }
            browserThread.set(browserInstance);
        }
        return browserThread.get();
    }

    public static void closeAll() {
        if (contextThread.get() != null) contextThread.get().close();
        if (browserThread.get() != null) browserThread.get().close();
        if (playwrightThread.get() != null) playwrightThread.get().close();

        pageThread.remove();
        contextThread.remove();
        browserThread.remove();
        playwrightThread.remove();
    }
}