package com.bh.spider.common.protocol;

import com.bh.spider.common.protocol.simple.SimpleProtocol;

/**
 * Created by lq on 17-4-16.
 */
public class ProtocolFactory {
    private static volatile Protocol protocol = null;

    public static Protocol get(){
        if(protocol==null){
            synchronized (ProtocolFactory.class) {
                if (protocol == null)
                    try {
                        String cls = System.getProperty("class.serializer.protocol", SimpleProtocol.class.getName());
                        protocol = (Protocol) Class.forName(cls).newInstance();
                    } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
            }
        }
        return protocol;
    }

}
