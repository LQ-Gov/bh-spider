package com.bh.spider.scheduler.cluster.consistent.operation;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OperationRecorder {
    private final static Logger logger = LoggerFactory.getLogger(OperationRecorder.class);

    /*
     *文件名称，由三部分组成,dir,name,index
     */
    private Path dir;

    private String name;

    private int index;


    private Entry snapshot;
    private Queue<Entry> cacheQueue;

    private int limit;


    private FileChannel channel;

    private Supplier<byte[]> snapshotCreator;


    public OperationRecorder(Path dir, int cacheSize) throws IOException {
        this("default", dir, cacheSize, null);
    }


    public OperationRecorder(String name, Path dir, int cacheSize, Supplier<byte[]> snapshotCreator) throws IOException {
        this.name = name;
        this.dir = dir;
        this.limit = cacheSize;
        this.cacheQueue = new CircularFifoQueue<>(cacheSize);
        this.snapshotCreator = snapshotCreator;
        init();
    }


    private void init() throws IOException {
        List<Path> files = Files.list(dir)
                .filter(x -> x.getFileName().toString().startsWith(name + "."))
                .sorted((o1, o2) -> {
                    int i1 = extractIndex(o1);

                    int i2 = extractIndex(o2);

                    if (i1 == i2) return 0;

                    return i1 > i2 ? 1 : -1;
                })
                .collect(Collectors.toList());


        Collections.reverse(files);

        for (Path file : files) {
            FileChannel channel = FileChannel.open(file);

            Entry entry;

            while ((entry = read0(channel)) != null) {
                if (entry.action() == Operation.SNAP) snapshot = entry;
                else {
                    cacheQueue.add(entry);
                }
            }
            if (!cacheQueue.isEmpty() || snapshot != null) {
                String name = file.getFileName().toString();
                this.index = Integer.parseInt(name.substring(this.name.length() + 1));
                this.channel = channel;
                return;
            } else
                Files.delete(file);
        }

        if (channel == null) {
            this.cut();
        }
    }

    private int extractIndex(Path path) {
        return Integer.parseInt(path.getFileName().toString().substring(name.length() + 1));
    }


    public String name() {
        return name;
    }


    public long committedIndex() {
        return cacheQueue.isEmpty() ? 0 : cacheQueue.peek().index();
    }

    public synchronized boolean write(Entry entry) {

        long nextIndex = committedIndex() + 1;

        entry.setIndex(nextIndex);

        try {
            return write0(entry);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private synchronized boolean write0(Entry entry) throws IOException {
        if (committedIndex() + 1 != entry.index()) {
            logger.error("不连续的操作索引,当前索引:{},entry索引:{}", committedIndex(), entry.index());
            return false;
        }

        channel.write(ByteBuffer.wrap(entry.serialize()));

        cacheQueue.add(entry);

        if (limit == cacheQueue.size())
            this.cut();


        return true;
    }


    private void cut() throws IOException {


        FileChannel nextChannel = FileChannel.open(Paths.get(dir.toString(), name + "." + String.valueOf(index + 1)),
                StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        Entry snap = new Entry(committedIndex(), Operation.SNAP, snapshotCreator.get());
        nextChannel.write(ByteBuffer.wrap(snap.serialize()));


        this.channel = nextChannel;

        this.index++;

        this.cacheQueue.add(snap);

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

    private Entry read() throws IOException {
        return read0(channel);
    }

    private Entry read0(FileChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        if (channel.read(buffer) == 4) {
            buffer.flip();

            int len = buffer.getInt() - 4;
            ByteBuffer data = ByteBuffer.allocate(len);

            if (channel.read(data) == len) {
                return Entry.deserialize(data.array(), 0, data.capacity() - 1);
            }
        }

        return null;
    }

    public List<Entry> load(long startCommittedIndex, long endCommittedIndex) {
        List<Entry> list = new LinkedList<>();
        if (startCommittedIndex < this.committedIndex() - cacheQueue.size()) {
            list.add(snapshot);

            startCommittedIndex = snapshot.index();
        }

        List<Entry> entries = new ArrayList<>(cacheQueue);

        if (!entries.isEmpty()) {
            int i = (int) (entries.size() - committedIndex() + startCommittedIndex);

            if (endCommittedIndex > committedIndex())
                endCommittedIndex = committedIndex();

            list.addAll(entries.subList(i, (int) (entries.size() - endCommittedIndex + committedIndex())));

        }

        return list;
    }


    public static Builder builder(String name) {
        return new Builder(name);
    }


    public static class Builder {
        private String name;
        private Path dir;

        private int limit;
        private Supplier<byte[]> snapshotCreator;

        private Builder(String name) {
            this.name = name;
        }


        public Builder snapshot(Supplier<byte[]> snapshotCreator, int limit) {

            this.snapshotCreator = snapshotCreator;
            this.limit = limit;
            return this;
        }


        public Builder dir(Path path) {
            this.dir = path;
            return this;
        }


        public OperationRecorder build() throws IOException {
            return new OperationRecorder(name, dir, limit, snapshotCreator);
        }

    }
}
