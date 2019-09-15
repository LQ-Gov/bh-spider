package com.bh.spider.consistent.raft.transport;


import com.bh.spider.consistent.raft.container.MarkMessage;
import com.bh.spider.consistent.raft.node.EventNode;
import com.bh.spider.consistent.raft.node.Node;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version Communicator, 2019/9/9 3:59 下午 liuqi19
 **/
public class Communicator {

    private Node me;

    private Map<Integer, Node> remotes;


    private Map<Integer, Connection> connections = new ConcurrentHashMap<>();

    private Map<String, List<CommandReceiveListener>> commandReceiveListeners = new HashMap<>();


    private List<NodeEventListener> nodeEventListeners = new LinkedList<>();

    public Communicator(Node me, Collection<Node> remotes) {
        this.me = me;

        this.remotes = remotes.stream().map(x -> new EventNode(x, this::event)).collect(Collectors.toMap(Node::id, x -> x));
    }


    protected Communicator() {
    }


    public void bind(Node node, Connection connection) {
        connections.put(node.id(), connection);
    }


    public Node local() {
        return me;
    }

    public Node remote(int id) {
        return remotes.get(id);
    }

    public Collection<Node> remotes() {
        return remotes.values();
    }


    public void sendTo(Node node, Object message) {
        connections.get(node.id()).write(message);
    }

    public void receive(Node from, MarkMessage message) {
        List<CommandReceiveListener> listeners = commandReceiveListeners.get(message.mark());

        for (CommandReceiveListener listener : listeners)
            listener.receive(from, message.data());
    }

    private void event(Node from,Node.Event event){
        for(NodeEventListener listener:nodeEventListeners){
            listener.handle(from,event);
        }
    }

    public synchronized <M> Communicator marked(String mark, CommandReceiveListener<M> listener,NodeEventListener nodeEventListener) {
        commandReceiveListeners.computeIfAbsent(mark, x -> new LinkedList<>()).add(listener);
        nodeEventListeners.add(nodeEventListener);
        return new MarkedCommunicator(mark, this);
    }


}
