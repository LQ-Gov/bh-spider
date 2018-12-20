package com.bh.spider.client

import com.bh.spider.fetch.Extractor
import com.bh.spider.fetch.FetchContext

class TouTiaoExtractor implements Extractor {
    @Override
    void run(FetchContext ctx) throws Exception {
        println("头条的抽取文件")
    }
}
