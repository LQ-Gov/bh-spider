package com.bh.spider.scheduler.cluster.consistent.operation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version FilePersistent, 2019-08-20 23:28 liuqi19
 **/
public class FilePersistent implements Persistent {
    private FileChannel channel;

    /*
     *文件名称，由三部分组成,dir,name,index
     */
    private Path dir;

    private String name;

    private int index;


    public FilePersistent(Path dir, String name) {
        this.dir = dir;
        this.name = name;
    }

    @Override
    public List<Entry> recover() throws IOException {
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

        List<Entry> entries = new LinkedList<>();
        for (Path file : files) {
            FileChannel channel = FileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.READ);

            Entry entry;

            while ((entry = read0(channel)) != null) {
                entries.add(entry);
            }

            if (!entries.isEmpty()) {
                String name = file.getFileName().toString();
                this.index = Integer.parseInt(name.substring(this.name.length() + 1));
                this.channel = channel;
            } else {
                Files.delete(file);
            }
        }

        if (channel == null) {
            this.channel = FileChannel.open(Paths.get(dir.toString(), name + "." + (index + 1)),
                    StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
        }

        return entries;
    }

    private int extractIndex(Path path) {
        return Integer.parseInt(path.getFileName().toString().substring(name.length() + 1));
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

    @Override
    public void cut(long committedIndex, Entry snap) throws IOException {


        FileChannel nextChannel = FileChannel.open(Paths.get(dir.toString(), name + "." + (index + 1)),
                StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        nextChannel.write(ByteBuffer.wrap(snap.serialize()));


        this.channel = nextChannel;

        this.index++;

    }

    @Override
    public boolean write(Entry entry) throws IOException {
        channel.write(ByteBuffer.wrap(entry.serialize()));
        return true;
    }
}
