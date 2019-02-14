package com.bh.spider.scheduler.cluster.consistent.operation;

import com.google.common.collect.EvictingQueue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class OperationRecorder {

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

        writer = FileChannel.open(filePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        reader = FileChannel.open(filePath, StandardOpenOption.READ);

        Path indexPath = Paths.get(path.toString(), name + ".index");

        recorderIndex = new RecorderIndex(indexPath);

        if (recorderIndex.committedIndex() != -1) {
            long pos = recorderIndex.position(recorderIndex.committedIndex());
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
            long pos = writer.position();

            writer.write(ByteBuffer.wrap(entry.serialize()));

            recorderIndex.write(nextIndex, pos);
            cacheQueue.add(entry);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized void writeAll(List<Entry> entries){

    }


    private void skip(FileChannel channel,long pos,int len) throws IOException {

        long end = channel.size();


        ByteBuffer buffer = ByteBuffer.allocate(4);

        for(int i=0;i<len&&pos<end;i++) {
            channel.position(pos);

            buffer.clear();
            if(channel.read(buffer)==buffer.capacity()) {
                buffer.flip();

                pos += buffer.getInt() + 4;
            }
            else break;
        }
    }

    private Entry read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        reader.read(buffer);
        buffer.flip();

        ByteBuffer data = ByteBuffer.allocate(buffer.getInt());

        reader.read(data);


        return Entry.deserialize(data.array(), 0, data.capacity() - 1);

    }


    private List<Entry> read(long startPos,long size) throws IOException {
        List<Entry> result = new LinkedList<>();

        reader.position(startPos);

        for (long i = 1; i < size; i++) {
            result.add(read());
        }

        return result;
    }

    public List<Entry> load(long start,long end) throws IOException {
        List<Entry> entries = new ArrayList<>(cacheQueue);

        Entry first = null;
        //start,end 都在entries内
        if (!entries.isEmpty() && (first = entries.get(0)).index() <= start)
            return entries.subList((int) (start - first.index()), Math.min((int) (end - first.index()), entries.size() - 1));


        List<Entry> tail = new LinkedList<>();
        //只有end在entries内
        if (first != null && end > first.index())
            tail = entries.subList(0, Math.min((int) (end - first.index()), entries.size() - 1));


        long pos = recorderIndex.position(start);
        List<Entry> head = read(pos, Math.min(end, first == null ? Long.MAX_VALUE : first.index()) - start);

        head.addAll(tail);


        return head;
    }
}
