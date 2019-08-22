package com.bh.spider.scheduler.cluster.consistent.operation;

import com.bh.common.utils.Json;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OperationRecorder {
    private final static Logger logger = LoggerFactory.getLogger(OperationRecorder.class);


    private String name;


    private CircularFifoQueue<Entry> queue;

    private Persistent persistent;


    public OperationRecorder(String name, int limit, Persistent persistent) throws Exception {
        this.name = name;

        this.queue = new CircularFifoQueue<>(limit);
        this.persistent = persistent;
        init();


    }


    private void init() throws Exception {
        List<Entry> entries = this.persistent.recover();
        if (entries.isEmpty()) return;

        queue.addAll(entries);
    }


    public String name() {
        return name;
    }


    public long committedIndex() {
        if (!queue.isEmpty()) return queue.get(queue.size() - 1).index();

        return 0;
    }


    public long firstIndex() {
        if (!queue.isEmpty()) return queue.peek().index();
        return 0;
    }

    public synchronized boolean write(Entry entry) {

        try {
            long nextIndex = committedIndex() + 1;
            entry.setIndex(nextIndex);
            return write0(entry);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private synchronized boolean write0(Entry entry) throws Exception {
        if (committedIndex() + 1 != entry.index()) {
            logger.error("不连续的操作索引,当前索引:{},entry索引:{}", committedIndex(), entry.index());
            return false;
        }

        queue.offer(entry);

        return true;
    }

    public byte[] snapshot() throws JsonProcessingException {
        return Json.get().writeValueAsBytes(queue);
    }


    public synchronized void writeAll(List<Entry> entries) {
        try {
            for (Entry entry : entries) {
                if (!write0(entry)) return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Entry> load(long startCommittedIndex, long endCommittedIndex) {
        if (queue.isEmpty()) return Collections.emptyList();

        if (startCommittedIndex < firstIndex() || startCommittedIndex > committedIndex())
            return Collections.emptyList();


        int index = (int) (startCommittedIndex - firstIndex());

        int end = (int) (Math.min(committedIndex() + 1, (endCommittedIndex)) - firstIndex());

        List<Entry> entries = new LinkedList<>();
        for (; index < end; index++) {
            entries.add(queue.get(index));
        }

        return entries;
    }


    public static Builder builder(String name) {
        return new Builder(name);
    }


    public static class Builder {
        private String name;

        private int limit;
        private Persistent persistent;

        private Builder(String name) {
            this.name = name;
        }


        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder persistent(Persistent persistent) {
            this.persistent = persistent;
            return this;
        }


        public OperationRecorder build() throws Exception {
            return new OperationRecorder(name, limit, persistent);
        }

    }
}
