package com.bh.spider.scheduler.domain.pattern;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.scheduler.domain.RulePattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 复制为spring的antPathMatcher
 */
public class AntRulePattern implements RulePattern {
    private final static Logger logger = LoggerFactory.getLogger(AntRulePattern.class);

    private final static Pattern PATH_SPLIT_PATTERN =
            Pattern.compile("(http|https|\\[http\\|https])://([^/]+)(/.*)?", Pattern.CASE_INSENSITIVE);

    private final static String DOT_SEPARATOR = ".";
    private final static String SLASH_SEPARATOR = "/";


    private String pattern;
    private String[] scheme;
    private String host;


    private AntMatcher domain;
    private AntMatcher path;

    private Map<String, AntPatternMatcher.AntPatternSection> parameters;

    public AntRulePattern(String pattern) {
        logger.debug("original pattern:{}", pattern);

        this.pattern = pattern;

        int index = pattern.indexOf("\\?");

        String principal, query = null;


        if (index > 0) {
            principal = pattern.substring(0, index);
            query = pattern.substring(index + 2);
        } else principal = pattern;

        Matcher matcher = PATH_SPLIT_PATTERN.matcher(principal);
        if (matcher.find()) {
            this.scheme = schemeToArray(matcher.group(1));
            this.host = matcher.group(2);
            this.domain = toPatternMatcher(matcher.group(2), DOT_SEPARATOR);
            this.path = toPatternMatcher(matcher.group(3), SLASH_SEPARATOR);
        }

        if (StringUtils.isNotBlank(query))
            this.parameters = formatParameters(query);
    }

    private String[] schemeToArray(String section) {
        logger.debug("pattern schema section:{}", section);

        switch (section.toLowerCase()) {
            case "http":
                return new String[]{"http"};
            case "https":
                return new String[]{"https"};
            case "[http|https]":
            case "[https|http]":
                return new String[]{"http", "https"};
            default:
                return new String[0];
        }
    }

    private Map<String, AntPatternMatcher.AntPatternSection> formatParameters(String section) {

        logger.debug("pattern parameters section:{}", section);

        Map<String, AntPatternMatcher.AntPatternSection> map = new HashMap<>();


        for(Map.Entry<String,String> kv: paramsToMap(section).entrySet()) {
            map.put(kv.getKey(), kv.getValue() == null ? null : new AntPatternMatcher.AntPatternSection(kv.getValue()));
        }


        return map;
    }

    private Map<String,String> paramsToMap(String query){
        if(query==null) return null;
        Map<String,String> map = new HashMap<>();
        String[] blocks = query.split("&");
        for (String it : blocks) {
            String[] kv = it.split("=");

            map.put(kv[0], kv.length < 2 ? null : kv[1]);
        }
        return map;
    }


    private AntPatternMatcher toPatternMatcher(String section, String separator) {
        logger.debug("pattern parameters section:{},separator:{}", section, separator);
        return new AntPatternMatcher(section, separator);
    }


    private boolean isPattern(String section) {
        return (section.indexOf('*') != -1 || section.indexOf('?') != -1);
    }


    public boolean match(URL url) {
        if (url == null) return false;
        //判断request协议 是否匹配
        if (scheme.length > 1 && !ArrayUtils.contains(scheme, url.getProtocol()))
            return false;


        Map<String, String> variables = new HashMap<>();
        //判断request domain 是否匹配
        if (!this.domain.match(url.getHost(), variables))
            return false;

        //判断path是否匹配
        if (!this.path.match(url.getPath(), variables))
            return false;

        //判断参数是否匹配
        Map<String, String> params = paramsToMap(url.getQuery());

        if(params!=null&&parameters!=null) {

            for (Map.Entry<String, AntPatternMatcher.AntPatternSection> entry : parameters.entrySet()) {
                String key = entry.getKey();
                AntPatternMatcher.AntPatternSection section = entry.getValue();

                if (section == null) continue;

                String value = params.get(key);

                if (!section.matchStrings(StringUtils.defaultString(value, ""), variables))
                    return false;

            }
        }

        return true;

    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public Comparator<RulePattern> getComparator(Request request) {
        return new AntPatternComparator(request);
    }


    public String domain(){
        return domain.pattern();
    }

    public String path(){
        return path.pattern();
    }


    public String value(){return pattern;}


}
