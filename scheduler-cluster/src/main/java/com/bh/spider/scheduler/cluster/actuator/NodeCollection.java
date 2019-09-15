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
public class NodeCollection extends HashMap<Long, Node> {
    private final static Logger logger = LoggerFactory.getLogger(NodeCollection.class);
    private final static ObjectMapper mapper = Json.get();
    private Actuator actuator;

    private TreeMap<Integer, Node> map = new TreeMap<>();

    private Raft raft;

    public NodeCollection(List<Node> nodes) {
        this.actuator = new NodeCollectionActuator(this);


        nodes.sort((o1, o2) -> {
            if (o1.getId() == o2.getId()) return 0;

            return o1.getId() > o2.getId() ? 1 : -1;
        });




        for (Node node:nodes) {

            for(int i=10;i>0;i--) {

                String key = (Integer.MAX_VALUE - i) + "#" + node.getId();
                int hashCode = fnvHash(key);
                map.put(hashCode,node);
            }
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
            CombineActuator.CombineEntry entry = new CombineActuator.CombineEntry(actuator.name(), mapper.writeValueAsBytes(node));
            raft.write(Json.get().writeValueAsBytes(entry));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Node consistentHash(int value) {
         Entry<Integer,Node> entry = map.ceilingEntry(value);
         if(entry==null) entry = map.firstEntry();

         return entry==null?null:entry.getValue();
    }


    public Actuator actuator() {
        return actuator;
    }


    private static class NodeCollectionActuator implements Actuator {


        private NodeCollection collection;

        public NodeCollectionActuator(NodeCollection collection) {
            this.collection = collection;

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
            collection.clear();
            if(data!=null) {
                collection.putAll(mapper.readValue(data, Json.mapType(Long.class, Node.class)));
            }
        }

        @Override
        public void apply(byte[] entry) throws Exception {
            logger.info("apply entry NodeCollection");
            Node node = mapper.readValue(entry, Node.class);
            if (node != null) {
                collection.put(node.getId(), node);
            }
        }

        @Override
        public Object read(byte[] data, boolean wait) {
            return null;
        }

    }
}
