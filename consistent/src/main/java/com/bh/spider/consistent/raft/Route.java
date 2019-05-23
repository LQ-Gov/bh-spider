package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.node.LocalNode;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.consistent.raft.node.RemoteNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author liuqi19
 * @version : Route, 2019-05-23 15:25 liuqi19
 */
public class Route {
    private LocalNode me;

    private List<RemoteNode> remotes;
    public Route(LocalNode me, Collection<RemoteNode> remotes) {
        this.me = me;
        this.remotes = new ArrayList<>(remotes);
    }



    public void broadcast(Message message,boolean toSelf) {

        if (toSelf)
            me.sendTo(me, message);

        for (Node node : remotes) {
            try {
                me.sendTo(node, message);
            } catch (Exception ignored) {
            }
        }
    }


    public void broadcast(Function<Node, Message> function) {
        for (Node node : remotes) {
            me.sendTo(node, function.apply(node));
        }
    }


    public void broadcast(Consumer<Node> consumer){
        for(Node node:remotes){
            consumer.accept(node);
        }
    }

    public void send(Node to, Message msg) {
        me.sendTo(to, msg);
    }
}
