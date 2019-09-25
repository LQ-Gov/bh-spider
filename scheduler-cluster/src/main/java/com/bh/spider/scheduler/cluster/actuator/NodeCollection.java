package com.bh.spider.scheduler.cluster.actuator;

import com.bh.common.utils.Json;
import com.bh.spider.common.member.Node;
import com.bh.spider.consistent.raft.Actuator;
import com.bh.spider.consistent.raft.Raft;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author liuqi19
 * @version NodeCollection, 2019-08-01 11:02 liuqi19
 **/
public class NodeCollection extends HashMap<Long, Node> implements Actuator {
    private final static Logger logger = LoggerFactory.getLogger(NodeCollection.class);
    private final static ObjectMapper mapper = Json.get();

    private transient TreeMap<Integer, Node> map = new TreeMap<>();

    @Raft.Bind
    private transient Raft raft;

    public NodeCollection(List<Node> nodes) {

        Collections.sort(nodes);

        for (Node node : nodes) {

            for (int i = 10; i > 0; i--) {

                String key = (Integer.MAX_VALUE - i) + "#" + node.getId();
                int hashCode = fnvHash(key);
                map.put(hashCode, node);
            }
            this.put(node.getId(),node);
        }

    }


    private static int fnvHash(String key) {
        final int p = 16777619;
        long hash = (int) 2166136261L;
        for (int i = 0, n = key.length(); i < n; i++) {
            hash = (hash ^ key.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return ((int) hash & 0x7FFFFFFF);
    }


    public void update(Node node) {
        try {
            raft.write(Json.get().writeValueAsBytes(node));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Node consistentHash(int value) {
        Entry<Integer, Node> entry = map.ceilingEntry(value);
        if (entry == null) entry = map.firstEntry();

        return entry == null ? null : entry.getValue();
    }

    @Override
    public String name() {
        return "NODE_COLLECTION_ACTUATOR";
    }

    @Override
    public byte[] snapshot() {
        try {
            return mapper.writeValueAsBytes(this);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public void recover(byte[] data) throws Exception {
        this.clear();
        if (data != null) {
            this.putAll(mapper.readValue(data, Json.mapType(Long.class, Node.class)));
        }
    }

    @Override
    public void apply(byte[] entry) throws Exception {
        Node node = mapper.readValue(entry, Node.class);
        if (node != null) {
            this.put(node.getId(), node);
        }
    }

    @Override
    public Object read(byte[] data, boolean wait) {
        return null;
    }
}
