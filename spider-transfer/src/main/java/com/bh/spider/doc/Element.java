package com.bh.spider.doc;

import java.util.List;
import java.util.regex.Pattern;

public interface Element {
    List<String> links();

    List<String> links(Pattern pattern);

    String html();

    String text();

    String data();

    Elements $(String selector);

    Elements xpath(String xpath);

    Element getElementById(String id);

    String attr(String attributeName);

    Elements children();

    boolean hasAttr(String attributeName);

    String nodeName();


}
