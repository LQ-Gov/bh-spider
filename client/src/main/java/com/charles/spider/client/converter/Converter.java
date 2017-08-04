package com.charles.spider.client.converter;

import java.io.IOException;

public interface Converter<IN,OUT> {


    OUT convert(IN data) throws IOException;


}
