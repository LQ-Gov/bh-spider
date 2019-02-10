package com.bh.spider.scheduler.cluster.consistent.operation;

import com.google.common.collect.EvictingQueue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;

public class OperationRecorder {

    private String name;

    private Path path;

    private long committedIndex;

    private Queue<Entry> cacheQueue;


    private RandomAccessFile reader;


    private FileOutputStream writer;

    public OperationRecorder(Path path,int cacheSize) throws IOException {
        this("default",path,cacheSize);
    }


    public OperationRecorder(String name, Path path, int cacheSize) throws IOException {
        this.name = name;
        this.path = path;
        this.cacheQueue = EvictingQueue.create(cacheSize);

        File file = path.toFile();
        if(!file.exists())
            file.createNewFile();


        reader = new RandomAccessFile(file, "r");

        writer = new FileOutputStream(file, true);


        Entry last = lastEntry(reader);

        committedIndex = last == null ? 0 : last.index();

    }

    private Entry lastEntry(RandomAccessFile reader) throws IOException {
        long len = reader.length();
        if (len == 0) return null;

        long pos = len - 1;
        int ch = -1;

        while (ch != '\n' && (--pos) >= 0) {
            reader.seek(pos);

            ch = reader.read();
        }

        byte[] data = new byte[(int) (len - pos - 1 - 1)];
        reader.seek(pos + 1);

        reader.read(data);


        return Entry.deserialize(data);
    }



    public String name(){
        return name;
    }


    public long committedIndex(){
        return committedIndex;
    }

    public synchronized boolean write(Entry entry) {

        entry.setIndex(committedIndex + 1);

        try {
            writer.write(entry.serialize());
            writer.write('\n');
            writer.flush();
            committedIndex = entry.index();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }


    List<Entry> load(long start,long end){
        return null;
    }
}
