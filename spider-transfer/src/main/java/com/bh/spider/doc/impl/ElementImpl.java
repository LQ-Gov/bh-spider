package com.bh.spider.doc.impl;

import com.bh.spider.doc.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class ElementImpl implements Element {

    private org.jsoup.nodes.Element base;

    public ElementImpl(){}


    ElementImpl(org.jsoup.nodes.Element el){
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

        Iterator iterator = list.iterator();
        while (iterator.hasNext()){
            String link = (String) iterator.next();
            if(!pattern.matcher(link).find())
                iterator.remove();
        }

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
    public List<Element> $(String selector) {
        Elements elements = el().select(selector);
        List<Element> result = new LinkedList<>();

        for(org.jsoup.nodes.Element it:elements){
            result.add(new ElementImpl(it));
        }

        return result;
    }

    @Override
    public List<Element> xpath(String xpath) {
        Elements elements = Xsoup.compile(xpath).evaluate(el()).getElements();
        List<Element> result = new LinkedList<>();

        for(org.jsoup.nodes.Element it:elements){
            result.add(new ElementImpl(it));
        }

        return result;
    }

    protected org.jsoup.nodes.Element el(){
        return base;
    }
}
