package com.charles.spider.doc;

import java.util.List;
import java.util.regex.Pattern;

public interface Element {
    List<String> links();

    List<String> links(Pattern pattern);

    String html();

    String text();

    List<Element> $(String selector);

    List<Element> xpath(String xpath);



}
