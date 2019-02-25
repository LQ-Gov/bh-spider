package com.bh.spider.client;

import com.bh.spider.doc.Document;
import com.bh.spider.common.fetch.Extractor;
import com.bh.spider.common.fetch.FetchContext;

public class CrawlerExtractor implements Extractor {
    @Override
    public void run(FetchContext ctx) throws Exception {
        System.out.println("测试抽取");

        Document doc = ctx.document();


    }
}
