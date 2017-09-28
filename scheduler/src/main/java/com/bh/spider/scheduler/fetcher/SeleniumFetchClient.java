package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchResponse;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.transfer.entity.DriverSetting;
import com.bh.spider.transfer.entity.Rule;
import com.bh.spider.transfer.entity.Script;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SeleniumFetchClient implements FetchClient {
    private final static Logger logger = LoggerFactory.getLogger(SeleniumFetchClient.class);
    private final static CookieStore cookieStore = CookieStoreFactory.get();

    private static volatile WebDriver driver = null;

    private static volatile ExecutorService workers = null;

    static {
        workers = Executors.newFixedThreadPool(3);
    }


    public SeleniumFetchClient() {
    }

//    private static WebDriver driverInstance() {
//
//        if (driver == null) {
//            synchronized (SeleniumFetchClient.class) {
//                ChromeOptions options = new ChromeOptions();
////                options.addArguments("--headless");
//                driver = new ChromeDriver(options);
//
//                driver = new PhantomJSDriver()
//
//
//            }
//        }
//        return driver;
//
//    }


    public static WebDriver createDriver() {

        //chrome
//        if (driver == null) {
//            synchronized (SeleniumFetchClient.class) {
//                ChromeOptions options = new ChromeOptions();
////                options.addArguments("--headless");
//                driver = new ChromeDriver(options);
//                driver.manage().deleteAllCookies();
//            }
//        }
//        return driver;


        //firefox
        if (driver == null) {
            synchronized (SeleniumFetchClient.class) {
                if (driver == null) {
                    //chrome
                    //ChromeOptions options = new ChromeOptions();
                    //driver = new ChromeDriver(options);

                    //firefox

                    System.setProperty("webdriver.gecko.driver","D:\\geckodriver.exe");
                    FirefoxOptions options = new FirefoxOptions();

                    options.addPreference("network.proxy.type", 1);
                    options.addPreference("network.proxy.http", "202.77.131.218");
                    options.addPreference("network.proxy.http_port", "9156");
                    options.addPreference("network.proxy.no_proxies_on", "localhost");
                    options.setBinary("D:\\Mozilla Firefox\\firefox.exe");





                    driver = new FirefoxDriver(options);
                }
            }
        }
        return driver;


        //phantomJS
//        DesiredCapabilities dc = new DesiredCapabilities();
//        dc.setJavascriptEnabled(true);
//
//        dc.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, Config.INIT_PHANTOMJS_PATH);
//        dc.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.109 Safari/537.36");
//        return new PhantomJSDriver(dc);
    }


    @Override
    public void execute(Request request, Rule rule, FetchCallback callback) {
        workers.execute(() -> {
            DriverSetting setting = rule != null ? rule.driver() : null;

            try {
                String url = request.url().toString();

                WebDriver driver = createDriver();

                List<HttpCookie> cookies = cookieStore.get(request.url().toURI());

                cookies.forEach(x -> driver.manage().addCookie(new SeleniumCookieAdapter(x)));


                //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                driver.manage().window().maximize();
                driver.manage().timeouts().pageLoadTimeout(setting == null || setting.getTimeout() <= 0 ? 15 : setting.getTimeout(), TimeUnit.SECONDS);

                driver.get(url);

                if (setting != null) {

                    List<Script> scripts = setting.scripts();

                    scripts.forEach(x -> {
                        switch (x.operator()) {
                            case CUSTOM:
                                ((JavascriptExecutor) driver).executeScript((String) x.args()[0]);
                                break;
                            case WAIT: {
                                long duration = ((Number) x.args()[0]).longValue();
                                try {
                                    new FluentWait<WebDriver>(driver)
                                            .withTimeout(duration, TimeUnit.SECONDS)
                                            .pollingEvery(duration, TimeUnit.SECONDS)
                                            .ignoring(NoSuchElementException.class)
                                            .ignoring(TimeoutException.class)
                                            .until(ExpectedConditions.presenceOfElementLocated(By.id(UUID.randomUUID().toString())));
                                } catch (TimeoutException | NoSuchElementException ignore) {
                                }
                                break;
                            }
                            case WAIT_UNTIL: {
                                new WebDriverWait(driver, ((Number) x.args()[1]).longValue())
                                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector((String) x.args()[0])));
                                break;
                            }
                            case CAN_CLICK: {
                                new WebDriverWait(driver, ((Number) x.args()[1]).longValue())
                                        .until(ExpectedConditions.elementToBeClickable(By.cssSelector((String) x.args()[0])));
                                break;
                            }
                        }
                    });
                }

                //((PhantomJSDriver) driver).getScreenshotAs(OutputType.FILE);

                cookies = driver.manage().getCookies().stream().map(this::toHttpCookie).collect(Collectors.toList());
                cookies.forEach(x -> cookieStore.add(null, x));

                FetchResponse response = new FetchResponse(200, driver.getPageSource().getBytes()
                        , null, cookies.stream().map(FetchCookieAdapter::new).collect(Collectors.toList()));
                callback.completed(response, rule);
            } catch (WebDriverException e) {
                callback.failed(e);

            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());

            } finally {


            }


        });
    }


    private HttpCookie toHttpCookie(Cookie cookie) {
        HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
        httpCookie.setSecure(cookie.isSecure());
        httpCookie.setPath(cookie.getPath());
        httpCookie.setMaxAge(cookie.getExpiry() == null ? -1 : cookie.getExpiry().getTime() / 1000);
        httpCookie.setDomain(cookie.getDomain());
        httpCookie.setHttpOnly(cookie.isHttpOnly());
        return httpCookie;

    }


    private DesiredCapabilities initDesiredCapabilities(Request request) {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setJavascriptEnabled(true);

        dc.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                Config.INIT_PHANTOMJS_PATH);


//        Proxy proxy = new Proxy();
//        proxy.setHttpProxy("127.0.0.1:1080");
//        dc.setCapability(CapabilityType.PROXY,proxy);


        Map<String, String> headers = request.headers();

        if (headers != null) {
            headers.forEach((k, v) -> setHeader(dc, k, v));
        }

        return dc;
    }

    private void setHeader(DesiredCapabilities dc, String name, String value) {
        if (name.equalsIgnoreCase("host") || name.equalsIgnoreCase("accept-encoding"))
            return;
        dc.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + name, value);
    }
}
