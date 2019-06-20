package com.bh.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author liuqi19
 * @version URLUtils, 2019-06-03 11:05 liuqi19
 **/
public class URLUtils {


    public static String format(String original,String paddingProtocol,String paddingHost) {
        String protocol = paddingProtocol;
        String host = paddingHost;
        if (original.startsWith("http://"))
            protocol = "http://";
        else if (original.startsWith("https://"))
            protocol = "https://";

        if(StringUtils.isNotBlank(protocol)&&!protocol.endsWith("://"))
            protocol+="://";



        if (original.startsWith(protocol))
            original = original.replaceFirst(Pattern.quote(protocol), "");


        int biasIndex = original.indexOf("/");
        if (biasIndex > 0) {
            host = "";
        }


        String newUrl = protocol + host;
        if (!(newUrl.endsWith("/") || original.startsWith("/")))
            newUrl += "/";

        newUrl += original;


        return newUrl;

    }
}
