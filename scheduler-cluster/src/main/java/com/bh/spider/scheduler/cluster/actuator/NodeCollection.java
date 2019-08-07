package com.bh.spider.scheduler.cluster.actuator;

import com.bh.common.utils.Json;
import com.bh.spider.common.member.Node;
import com.bh.spider.consistent.raft.Actuator;
import com.bh.spider.consistent.raft.Raft;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author liuqi19
 * @version NodeCollection, 2019-08-01 11:02 liuqi19
 **/
public class NodeCollection extends HashMap<Long, Node> {
    private final static Logger logger = LoggerFactory.getLogger(NodeCollection.class);
    private final static ObjectMapper mapper = Json.get();
    private Actuator actuator;

    private Raft raft;


    public NodeCollection(Raft raft) {
        this.actuator = new NodeCollectionActuator(this);
        this.raft = raft;
    }


    public void update(Node node) {
        try {
            raft.write(mapper.writeValueAsBytes(node));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            collection.putAll(mapper.readValue(data, Json.mapType(Long.class, Node.class)));
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
