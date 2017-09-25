package com.bh.spider.fetch;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetchContextUtils {

    private static final FetchContextUtils utils = new FetchContextUtils();

    private FetchContextUtils() {
    }

    public static FetchContextUtils instance() {
        return utils;
    }

    public String param(URL url, String key, String defaultValue) {
        String value = param(url, key);

        return value == null ? defaultValue : value;
    }

    public String param(URL url, String key) {
        String u = url.toString();

        int start = u.indexOf('?');

        if (start < 0 || start == u.length() - 1) return null;

        Matcher matcher = Pattern.compile("[?|&]" + key + "=([^&]*)|$").matcher(u);

        return matcher.find() ? matcher.group(1) : null;
    }

    public URL setParam(URL url, String key, String value) {
        String u = url.toString();
        try {
            if (param(url, key) != null) {
                String newUrl = u.replaceAll("([&|?])" + key + "=([^&]*|$)", "$1" + key + "=" + value);
                return new URL(newUrl);

            } else {
                if (u.indexOf('?') < 0) u += "?";
                if (!(u.endsWith("?") || u.endsWith("&")))
                    u += "&";


                u = u + key + "=" + value;
                return new URL(u);
            }
        } catch (MalformedURLException ignore) {
        }

        return null;
    }

    public URL toURL(Request req) {
        if (req.method() == HttpMethod.GET) {
            URL url = req.url();
//            for (Map.Entry<String, Object> it : req.params().entrySet()) {
//                if (it.getValue() == null) continue;
//                url = setParam(url, it.getKey(), it.getValue().toString());
//            }
//            return url;
        }

        return req.url();
    }
}
