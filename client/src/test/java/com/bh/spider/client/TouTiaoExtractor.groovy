package com.bh.spider.client

import com.bh.spider.common.fetch.Extractor
import com.bh.spider.common.fetch.FetchContext

class TouTiaoExtractor implements Extractor {
    @Override
    void run(FetchContext ctx) throws Exception {
        println("头条的抽取文件")
    }
}
