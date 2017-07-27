package com.charles.spider.common.extractor;

import com.charles.spider.common.http.FetchContext;

public interface Extractor {

    String STATUS_100 = "100";
    String STATUS_101 = "101";
    String STATUS_102="102";
    String STATUS_200="200";



    void run(FetchContext ctx,Document doc);


}
