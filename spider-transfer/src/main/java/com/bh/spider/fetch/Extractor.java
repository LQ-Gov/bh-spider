package com.bh.spider.fetch;


public interface Extractor {



    @Code("default")
    void run(FetchContext ctx) throws Exception;


    static FetchContextUtils utils() {
        return FetchContextUtils.instance();
    }


}
