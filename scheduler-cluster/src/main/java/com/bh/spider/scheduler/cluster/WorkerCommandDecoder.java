package com.bh.spider.scheduler.cluster;

import com.bh.spider.scheduler.CommandDecoder;
import com.bh.spider.scheduler.cluster.context.WorkerContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.transfer.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class WorkerCommandDecoder extends CommandDecoder {

    @Override
    protected Context buildContext(ChannelHandlerContext ctx, long commandId, CommandCode key) {
        Session session = ctx.channel().<Session>attr(AttributeKey.valueOf("SESSION")).get();
        return new WorkerContext(session,commandId);
    }
}
