package com.ccharles.spider.fetch;


import com.ccharles.spider.fetch.FetchContext;

public interface Extractor {

    String STATUS_100 = "100";
    String STATUS_101 = "101";
    String STATUS_102="102";
    String STATUS_200="200";



    boolean run(FetchContext ctx) throws Exception;


}
