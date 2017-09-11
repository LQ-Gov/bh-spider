package com.bh.spider.doc.impl;

import com.bh.spider.doc.Document;
import com.bh.spider.doc.Element;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.jsoup.Jsoup;

import java.nio.charset.Charset;

public class DocumentImpl extends ElementImpl implements Document {
    private byte[] data = null;
    private Charset charset;
    private String lang;
    private volatile boolean isDetected = false;

    private volatile org.jsoup.nodes.Document doc0 = null;


    public DocumentImpl(byte[] data) {

        this(data, null);
    }


    public DocumentImpl(byte[] data, Charset charset) {
        this.data = data;
        this.charset = charset;
    }

    private synchronized void detect() {
        if (isDetected) return;
        CharsetDetector detector = new CharsetDetector();
        detector.setText(data);
        CharsetMatch match = detector.detect();
        charset = Charset.forName(match.getName());
        lang = match.getLanguage();

        isDetected = true;

    }


    @Override
    public Charset charset() {
        if (charset == null) {
            detect();
        }

        return charset;
    }


    @Override
    public String language() {
        if (lang == null) detect();

        return lang;
    }


    @Override
    public String title() {
        return el().title();

    }

    @Override
    public Element body() {
        return new ElementImpl(el().body());
    }

    @Override
    protected org.jsoup.nodes.Document el() {
        if (doc0 == null) {
            synchronized (this) {
                doc0 = Jsoup.parse(new String(data, charset()));
            }
        }
        return doc0;
    }

}
