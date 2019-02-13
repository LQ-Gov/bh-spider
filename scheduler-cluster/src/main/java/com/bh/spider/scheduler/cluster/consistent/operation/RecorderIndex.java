package com.bh.spider.scheduler.cluster.consistent.operation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Conversion;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class RecorderIndex {

    private final byte[] memory = new byte[17];

    private long committedIndex;

    private RandomAccessFile reader;

    private RandomAccessFile writer;


    public RecorderIndex(Path path) throws IOException {
        this(path.toFile());

    }


    public RecorderIndex(File file) throws IOException {

        file.createNewFile();


        reader = new RandomAccessFile(file, "r");

        writer = new RandomAccessFile(file, "w");


        checkLastCommit(reader);
    }


    private void checkLastCommit(RandomAccessFile reader) throws IOException {
        long len = reader.length();
        if (len == 0) return;

        long pos = len - 1;
        int ch = -1;

        do{
            reader.seek(pos);
            ch = reader.read();
        } while (ch != '\n' && (--pos) >= 0);

        if(ch=='\n') {
            reader.seek(pos-16);
            reader.read(memory);
            committedIndex = Conversion.byteArrayToLong(memory, 0, 0, 0, Long.BYTES);
        }
        else {
            committedIndex = 0;
        }
        writer.seek(pos+1);
    }



    public void write(long committedIndex,long pos) throws IOException {

        byte[] data = new byte[17];
        Conversion.longToByteArray(committedIndex, 0, data, 0, Long.BYTES);
        Conversion.longToByteArray(pos, 0, data, 8, Long.BYTES);
        data[16] = '\n';


        writer.write(data);

        this.committedIndex = committedIndex;

    }



    public long position(long index) throws IOException {
        if (committedIndex() < index) return -1;

        long offset = (committedIndex - index + 1) * 17;

        reader.seek(reader.length() - offset);

        reader.read(memory);

        return Conversion.byteArrayToLong(memory,0,0,0,Long.BYTES);
    }


    public long committedIndex(){
        return committedIndex;
    }
}
