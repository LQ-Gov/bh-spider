package com.charles.spider.fetch.context.doc;

import com.charles.spider.common.extractor.Document;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.jsoup.Jsoup;

import java.nio.charset.Charset;

public class DocumentImpl extends ElementImpl implements Document {
    private String data = null;
    private Charset charset;

    private volatile org.jsoup.nodes.Document doc0 = null;


    public DocumentImpl(byte[] data) {

        this(data, null);
    }


    public DocumentImpl(byte[] data, Charset charset) {
        if (charset == null) {
            CharsetDetector detector = new CharsetDetector();
            detector.setText(data);
            CharsetMatch match = detector.detect();
            charset = Charset.forName(match.getName());
        }


        this.charset = charset;
        this.data = new String(data, charset);
    }


    @Override
    public String title() {
        return el().title();

    }

    @Override
    protected org.jsoup.nodes.Document el() {
        if (doc0 == null) {
            synchronized (this) {
                doc0 = Jsoup.parse(data);
            }
        }
        return doc0;
    }

}
