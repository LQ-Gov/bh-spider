package com.bh.spider.common.fetch;

import java.net.URL;
import java.util.Date;
import java.util.Map;

/**
 * Created by lq on 17-6-3.
 */
public interface Request extends Cloneable {
    enum State {
        QUEUE, GOING, EXCEPTION, FINISHED, FAILED
    }

    long id();


    URL url();

    FetchMethod method();

    /**
     * 请求头设置
     *
     * @return
     */
    Map<String, String> headers();

    /**
     * 向服务器请求附带的内容,如果是Get请求,且为key/value,则会作为参数附加到url上
     *
     * @return
     */
    //Object data();


    /**
     * 框架内消息传递附带的数据,这部分内容不参与实际请求，但可以在extractor中获取到
     *
     * @return
     */
    Map<String, Object> extra();


    String hash();


    Date createTime();


    Object clone() throws CloneNotSupportedException;

}
