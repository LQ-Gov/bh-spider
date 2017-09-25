package com.bh.spider.fetch;


public interface Extractor {



    void run(FetchContext ctx) throws Exception;


    static FetchContextUtils utils() {
        return FetchContextUtils.instance();
    }


}
