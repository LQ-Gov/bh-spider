package com.bh.spider.client

import com.bh.spider.common.fetch.Extractor
import com.bh.spider.common.fetch.FetchContext

class ZhiHuCrawler implements Extractor {
    @Override
    void run(FetchContext ctx) throws Exception {
        System.out.println("知乎测试")
    }
}
