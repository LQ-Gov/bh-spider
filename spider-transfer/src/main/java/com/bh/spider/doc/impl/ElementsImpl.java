package com.bh.spider.doc.impl;

import com.bh.spider.doc.Element;
import com.bh.spider.doc.Elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ElementsImpl extends LinkedList<Element> implements Elements {

    public ElementsImpl() {
    }

    ElementsImpl(org.jsoup.select.Elements elements) {
        this.addAll(elements.stream().map(ElementImpl::new).collect(Collectors.toList()));
    }

    @Override
    public List<String> links() {

        List<String> list = new LinkedList<>();

        this.forEach(x -> list.addAll(x.links()));

        return list;
    }

    @Override
    public List<String> links(Pattern pattern) {
        return links().stream().filter(x -> pattern.matcher(x).find()).collect(Collectors.toList());
    }

    @Override
    public String html() {
        StringBuilder builder = new StringBuilder();

        Element element;
        for (Iterator<Element> it = this.iterator(); it.hasNext(); builder.append(element.html())) {
            element = it.next();

            if (builder.length() != 0)
                builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public String text() {
        StringBuilder builder = new StringBuilder();

        Element element;
        for (Iterator<Element> it = this.iterator(); it.hasNext(); builder.append(element.text())) {
            element = it.next();

            if (builder.length() != 0)
                builder.append(" ");
        }
        return builder.toString();
    }

    @Override
    public String data() {
        StringBuilder builder = new StringBuilder();

        Element element;
        for (Iterator<Element> it = this.iterator(); it.hasNext(); builder.append(element.data())) {
            element = it.next();
        }
        return builder.toString();
    }

    @Override
    public Elements $(String selector) {
        Elements elements = new ElementsImpl();
        for (Element el : this) {
            elements.addAll(el.$(selector));
        }
        return elements;
    }

    @Override
    public Elements xpath(String xpath) {
        return null;
    }

    @Override
    public Element getElementById(String id) {

        for (Element el : this) {
            Element element = el.getElementById(id);
            if (element != null) return element;
        }
        return null;
    }

    @Override
    public String attr(String attributeName) {
        for (Element el : this) {
            if (el.hasAttr(attributeName)) return el.attr(attributeName);
        }
        return null;
    }

    @Override
    public Elements children() {
        Elements elements = new ElementsImpl();
        for (Element it : this) {
            elements.addAll(it.children());
        }
        return elements;
    }

    @Override
    public boolean hasAttr(String attributeName) {
        for (Element element : this) {
            if (element.hasAttr(attributeName)) return true;
        }

        return false;
    }

    @Override
    public String nodeName() {
        throw new RuntimeException("not support this function");
    }


    public Element first() {
        return this.isEmpty() ? null : this.get(0);
    }
}
