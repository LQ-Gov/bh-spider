package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchResponse;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.transfer.entity.DriverSetting;
import com.bh.spider.transfer.entity.Rule;
import com.bh.spider.transfer.entity.Script;
import org.apache.http.concurrent.FutureCallback;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
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

    private static volatile ExecutorService workers = null;

    static {
        workers = Executors.newFixedThreadPool(3);
    }


    public SeleniumFetchClient() {
    }


    @Override
    public void execute(FetchRequest request, FutureCallback<FetchResponse> callback) {
        workers.execute(() -> {
            Rule rule = request.getRule();
            DriverSetting setting = rule != null ? rule.driver() : null;

            PhantomJSDriver driver = new PhantomJSDriver(initDesiredCapabilities(request));
            try {
                String url = request.url().toString();

                List<HttpCookie> cookies = cookieStore.get(request.url().toURI());

                cookies.forEach(x -> driver.manage().addCookie(new SeleniumCookieAdapter(x)));


                //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);

                driver.get(url);

                if (setting != null) {

                    List<Script> scripts = setting.scripts();

                    scripts.forEach(x -> {
                        switch (x.operator()) {
                            case CUSTOM:
                                driver.executeScript((String) x.args()[0]);
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

                cookies = driver.manage().getCookies().stream().map(this::toHttpCookie).collect(Collectors.toList());
                cookies.forEach(x -> cookieStore.add(null, x));

                FetchResponse response = new FetchResponse(200, driver.getPageSource().getBytes()
                        , null, cookies.stream().map(FetchCookieAdapter::new).collect(Collectors.toList()));
                callback.completed(response);
            } catch (TimeoutException e) {
                callback.failed(e);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());

            } finally {
                driver.close();

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


    private DesiredCapabilities initDesiredCapabilities(FetchRequest request) {
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
