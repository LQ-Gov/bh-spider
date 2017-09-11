package com.bh.spider.doc;

import java.nio.charset.Charset;

public interface Document extends Element {

    String title();

    Element body();

    Charset charset();

    String language();


}
