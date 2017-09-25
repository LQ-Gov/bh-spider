package com.bh.spider.doc.impl;

import com.bh.spider.doc.Element;
import com.bh.spider.doc.Elements;
import us.codecraft.xsoup.Xsoup;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ElementImpl implements Element {

    private org.jsoup.nodes.Element base;

    public ElementImpl() {
    }


    ElementImpl(org.jsoup.nodes.Element el) {
        this.base = el;
    }


    @Override
    public List<String> links() {
        org.jsoup.select.Elements elements = el().select("a[href]");
        List<String> result = new LinkedList<>();

        elements.forEach(x -> result.add(x.attr("href")));
        return result;
    }

    @Override
    public List<String> links(Pattern pattern) {
        List<String> list = links();

        list = list.stream().filter(x -> pattern.matcher(x).find())
                .collect(Collectors.toList());


        return list;

    }


    @Override
    public String html() {
        return el().html();
    }

    @Override
    public String text() {
        return el().text();
    }

    @Override
    public String data() {
        return el().data();
    }

    @Override
    public Elements $(String selector) {
        org.jsoup.select.Elements elements = el().select(selector);
        return new ElementsImpl(elements);
    }

    @Override
    public Elements xpath(String xpath) {
        org.jsoup.select.Elements elements = Xsoup.compile(xpath).evaluate(el()).getElements();

        return new ElementsImpl(elements);
    }

    @Override
    public Element getElementById(String id) {
        org.jsoup.nodes.Element element = el().getElementById(id);

        return element == null ? null : new ElementImpl(el().getElementById(id));
    }

    @Override
    public String attr(String attributeName) {
        return el().attr(attributeName);
    }

    @Override
    public Elements children() {
        return new ElementsImpl(el().children());
    }

    @Override
    public boolean hasAttr(String attributeName) {
        return el().hasAttr(attributeName);
    }

    @Override
    public String nodeName() {
        return el().nodeName();
    }

    protected org.jsoup.nodes.Element el() {
        return base;
    }
}
