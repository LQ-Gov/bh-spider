package com.bh.spider.scheduler.cluster.consistent.operation;

import com.google.common.collect.EvictingQueue;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class OperationRecorder {

    private String name;


    private Queue<Entry> cacheQueue;


    private RandomAccessFile reader;


    private RandomAccessFile writer;


    private RecorderIndex recorderIndex;

    public OperationRecorder(Path path,int cacheSize) throws IOException {
        this("default",path,cacheSize);
    }


    public OperationRecorder(String name, Path path, int cacheSize) throws IOException {
        this.name = name;
        this.cacheQueue = EvictingQueue.create(cacheSize);

        File file = new File(path.toFile(),name);

        file.createNewFile();

        reader = new RandomAccessFile(file, "r");

        writer = new RandomAccessFile(file, "w");


        recorderIndex = new RecorderIndex(new File(path.toFile(),name+".index"));

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
            long pos = writer.getFilePointer();
            writer.write(entry.serialize());
            writer.write('\n');

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

    private Entry read() throws IOException {
        int len = reader.readInt();

        byte[] data = new byte[len+1];

        reader.read(data);

        return Entry.deserialize(data,0,len);

    }


    private List<Entry> read(long startPos,long size) throws IOException {
        List<Entry> result = new LinkedList<>();

        reader.seek(startPos);

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
