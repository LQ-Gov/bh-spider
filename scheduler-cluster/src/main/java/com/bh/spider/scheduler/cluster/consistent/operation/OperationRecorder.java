package com.bh.spider.scheduler.cluster.consistent.operation;

import com.google.common.collect.EvictingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class OperationRecorder {
    private final static Logger logger = LoggerFactory.getLogger(OperationRecorder.class);

    private String name;


    private Queue<Entry> cacheQueue;


    private FileChannel reader;


    private FileChannel writer;


    private RecorderIndex recorderIndex;

    public OperationRecorder(Path path,int cacheSize) throws IOException {
        this("default",path,cacheSize);
    }


    public OperationRecorder(String name, Path path, int cacheSize) throws IOException {
        this.name = name;
        this.cacheQueue = EvictingQueue.create(cacheSize);

        Path filePath = Paths.get(path.toString(), name);

        writer = FileChannel.open(filePath, StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE);

        reader = FileChannel.open(filePath, StandardOpenOption.READ);

        Path indexPath = Paths.get(path.toString(), name + ".index");

        recorderIndex = new RecorderIndex(indexPath);

        long committedIndex =recorderIndex.committedIndex();
        if (committedIndex != -1) {

            long pos = recorderIndex.position(committedIndex);
            skip(writer, pos, 1);

        }
    }







    public String name(){
        return name;
    }


    public long committedIndex(){
        return recorderIndex.committedIndex();
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
        if (committedIndex() + 1 != entry.index()) return false;
        long pos = writer.position();

        writer.write(ByteBuffer.wrap(entry.serialize()));

        recorderIndex.write(entry.index(), pos);
        cacheQueue.add(entry);

        return true;
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


    private void skip(FileChannel channel,long pos,int len) throws IOException {

        long end = channel.size();


        ByteBuffer buffer = ByteBuffer.allocate(4);

        channel.position(pos);
        for (int i = 0; i < len && pos < end; i++) {
            buffer.clear();
            if (channel.read(buffer) == buffer.capacity()) {
                buffer.flip();

                pos += buffer.getInt();
                channel.position(pos);

            } else break;
        }


        logger.info("当前writer position:{}", channel.position());
    }

    private Entry read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        if( reader.read(buffer)==4) {
            buffer.flip();

            int len = buffer.getInt()-4;
            ByteBuffer data = ByteBuffer.allocate(len);

            if(reader.read(data)==len) {
                return Entry.deserialize(data.array(), 0, data.capacity() - 1);
            }
        }

        return null;

    }


    private List<Entry> readEntries(long startCommittedIndex,long endCommittedIndex) throws IOException {


        startCommittedIndex = Math.max(startCommittedIndex, 0);
        endCommittedIndex = Math.min(committedIndex(), endCommittedIndex);

        if (endCommittedIndex < startCommittedIndex) return Collections.emptyList();

        List<Entry> result = new LinkedList<>();

        reader.position(recorderIndex.position(startCommittedIndex));

        for (; startCommittedIndex <= endCommittedIndex; startCommittedIndex++) {
            result.add(read());

        }

        return result;
    }

    public List<Entry> load(long startCommittedIndex,long endCommittedIndex) throws IOException {
        startCommittedIndex = Math.max(startCommittedIndex,0);

        endCommittedIndex = Math.min(committedIndex(),endCommittedIndex);

        if (endCommittedIndex < 0 || startCommittedIndex > endCommittedIndex) return Collections.emptyList();

        List<Entry> entries = new ArrayList<>(cacheQueue);

        Entry first = entries.isEmpty() ? null : entries.get(0);

        if (first == null || first.index() > endCommittedIndex)
            return readEntries(startCommittedIndex, endCommittedIndex);


        if(startCommittedIndex>first.index()) {
            return entries.subList((int) (startCommittedIndex - first.index()), (int) (endCommittedIndex - first.index() + 1));
        }

        List<Entry> head = readEntries(startCommittedIndex,first.index()-1);

        List<Entry> tail = entries.subList(0,(int)(endCommittedIndex-first.index())+1);


        head.addAll(tail);


        return head;
    }
}
