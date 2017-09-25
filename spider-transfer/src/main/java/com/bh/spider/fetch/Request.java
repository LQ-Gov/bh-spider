package com.bh.spider.fetch;

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


    URL url();

    HttpMethod method();

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
     * 框架内消息传递附带的数据
     *
     * @return
     */
    Map<String, Object> extra();


    Date createTime();


    Object clone() throws CloneNotSupportedException;
}
