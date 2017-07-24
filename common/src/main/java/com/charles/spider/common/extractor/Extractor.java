package com.charles.spider.common.extractor;

import com.charles.spider.common.http.FetchContext;

public interface Extractor {

    void run(FetchContext ctx,Document doc);


}
