package com.bh.spider.doc.impl;

import com.bh.spider.doc.Document;
import org.jsoup.select.Elements;

import java.util.List;

public class DocumentBuilder {

    public static Document builder(String data) {
        List<String> s;
        return new DocumentImpl(data.getBytes());
    }
}
