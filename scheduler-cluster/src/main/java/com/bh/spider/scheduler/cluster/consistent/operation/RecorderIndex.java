package com.bh.spider.scheduler.cluster.consistent.operation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class RecorderIndex {

    private final static byte BLOCK_SIZE= 17;
    private final byte[] memory = new byte[BLOCK_SIZE];
    private final ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);

    private Entry last;

    private FileChannel writeChannel;

    private FileChannel readChannel;


    public RecorderIndex(Path path) throws IOException {

        this.last = null;

        this.writeChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        this.readChannel = FileChannel.open(path, StandardOpenOption.READ);


        checkLastCommit();
    }




    private void checkLastCommit() throws IOException {
        long len = readChannel.size();
        if (len < BLOCK_SIZE) return;


        long pos = len - BLOCK_SIZE;
        writeToBuffer(pos);

        buffer.flip();

        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                pos = pos + i+1 - BLOCK_SIZE;
                writeToBuffer(pos);
                buffer.flip();

                last = new Entry(buffer.getLong(), pos);
                writeChannel.position(readChannel.position());
                break;
            }
        }
    }

    private void writeToBuffer(long pos) throws IOException {
        buffer.clear();
        readChannel.position(pos);
        readChannel.read(buffer);
    }



    public void write(long committedIndex,long pos) throws IOException {

        buffer.clear();
        buffer.putLong(committedIndex).putLong(pos).put((byte) '\n');
        buffer.flip();
        writeChannel.write(buffer);
        this.writeChannel.force(true);

        this.last = new Entry(committedIndex, pos);
    }



    public long position(long index) throws IOException {
        Entry entry = last;
        if (index < 0 || entry == null || entry.committedIndex < index) return -1;


        long offset = (entry.committedIndex - index) * BLOCK_SIZE;

        writeToBuffer(entry.position - offset);

        buffer.flip();

        return buffer.getLong(8);
    }


    public long committedIndex(){
        return last==null?-1:last.committedIndex;
    }


    private class Entry{
        private long committedIndex;
        private long position;
        public Entry(long committedIndex,long position){
            this.committedIndex =committedIndex;
            this.position = position;
        }
    }
}
