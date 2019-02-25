package com.bh.spider.common.fetch;


public interface Extractor {



    @Code("default")
    void run(FetchContext ctx) throws Exception;


    static FetchContextUtils utils() {
        return FetchContextUtils.instance();
    }


}
