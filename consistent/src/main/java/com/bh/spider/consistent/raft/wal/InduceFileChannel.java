package com.bh.spider.consistent.raft.wal;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author liuqi19
 * @version : InduceFileChannel, 2019-04-23 18:11 liuqi19
 */
public class InduceFileChannel extends FileChannel {

    private FileChannel channel;

    private Path path;

    private InduceFileChannel(Path path, FileChannel channel) {

        this.path = path;
        this.channel = channel;
    }


    public static InduceFileChannel open(Path path,
                                         Set<? extends OpenOption> options,
                                         FileAttribute<?>... attrs) throws IOException {
         FileChannel channel = FileChannel.open(path,options,attrs);


         return new InduceFileChannel(path,channel);
    }


    public static InduceFileChannel open(Path path,OpenOption... options) throws IOException {
        Set<OpenOption> set = new HashSet<>(options.length);
        Collections.addAll(set,options);

        return open(path,set);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        return channel.read(dsts,offset,length);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return channel.write(src);
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        return channel.write(srcs,offset,length);
    }

    @Override
    public long position() throws IOException {
        return channel.position();
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        return channel.position(newPosition);
    }

    @Override
    public long size() throws IOException {
        return channel.size();
    }

    @Override
    public FileChannel truncate(long size) throws IOException {
        return channel.truncate(size);
    }

    @Override
    public void force(boolean metaData) throws IOException {
        channel.force(metaData);
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        return channel.transferTo(position,count,target);
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        return channel.transferFrom(src,position,count);
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        return channel.read(dst,position);
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        return channel.write(src,position);
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        return channel.map(mode,position,size);
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        return channel.lock(position,size,shared);
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        return channel.tryLock(position,size,shared);
    }

    @Override
    protected void implCloseChannel() throws IOException {

        try {
            Method method = AbstractInterruptibleChannel.class.getDeclaredMethod("implCloseChannel");
            method.invoke(channel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String filename(){
        return path.getFileName().toString();
    }



    public Path path(){return path;}



//    public void truncate(long size){
//
//    }
}
