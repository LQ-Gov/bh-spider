package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.Assemble;
import com.charles.spider.common.protocol.Token;

/**
 * Created by LQ on 2015/11/10.
 * Update by LQ on 2017/4/16
 *
 */
public class SimpleAssemble implements Assemble {
    private byte[] data = null;
    private int pos =0;
    private int end =0;
    private int start=0;



    public SimpleAssemble(byte[] data, int pos, int len) {
        this.data = data;
        this.pos = pos;
        this.end = pos + len;
        this.start = pos;
        if (end > data.length || len < 1 || pos < 0)
            throw new ArrayIndexOutOfBoundsException(end);
    }

    @Override
    public Token next() throws Exception {
        if (pos >= end)
            return null;
        Token token = new SimpleToken(data, pos);
        if (!token.isVaild())
            throw new Exception("error token");
        pos += token.length();
        return token;
    }


    public int  pos()
    {
        return pos;
    }
}
