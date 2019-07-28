package com.bh.spider.consistent.raft.wal;

import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.HardState;
import com.bh.spider.consistent.raft.log.Entry;
import com.bh.spider.consistent.raft.log.Snapshot;
import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version $Id: WAL, 2019-04-01 16:21 liuqi19
 */
public class WAL {
    private final static Logger logger = LoggerFactory.getLogger(WAL.class);

    private final static long SEGMENT_SIZE_BYTES = 64 * 1000 * 1000; // 64MB


    private final static Pattern WAL_NAME_PATTERN = Pattern.compile("(\\d+)-(\\d+)\\.wal");


    /**
     * the living directory of the underlay files
     */
    private Path dir;


    private Snapshot.Metadata metadata;

    /**
     * snapshot to start reading
     */
    private Snapshot.Metadata start;


    /**
     * index of the last entry saved to the wal
     */
    private long lastIndex;

    /**
     * the locked files the WAL holds (the name is increasing)
     */
    private List<InduceFileChannel> channels;


    /**
     * encoder to encode records
     */
    private Encoder encoder;

    /**
     * decoder to decode records
     */
    private Decoder decoder;


    private HardState state;


    private WAL() {
    }

//    private WAL(Path dir, byte[] metadata, Encoder encoder) {
//        this.dir = dir;
//        this.metadata = metadata;
//        this.channels = new LinkedList<>();
//        this.encoder = encoder;
//    }


    private WAL(Path dir, List<InduceFileChannel> channels, Snapshot.Metadata start) {
        this.dir = dir;
        this.channels = channels;
        this.decoder = new Decoder(channels);
        this.start = start;
    }

    /**
     * Create creates a WAL ready for appending records. The given metadata is
     * recorded at the head of each WAL file, and can be retrieved with ReadAll.
     *
     * @return
     */


    public void sync() throws IOException {
        if (this.encoder != null)
            this.encoder.flush();
    }


    public InduceFileChannel tail() {
        if (!this.channels.isEmpty())
            return this.channels.get(this.channels.size() - 1);


        return null;
    }


    private void renameWAL(Path tmp) throws IOException {
        FileUtils.deleteDirectory(this.dir.toFile());
        // On non-Windows platforms, hold the lock while renaming. Releasing
        // the lock and trying to reacquire it quickly can be flaky because
        // it's possible the process will fork to spawn a process while this is
        // happening. The fds are set up as close-on-exec by the Go runtime,
        // but there is a window between the fork and the exec where another
        // process holds the lock.
        Files.move(tmp, this.dir);
    }


    public static WAL open(Path dir, Snapshot.Metadata metadata) throws IOException {

        WAL wal = openAtIndex(dir, metadata == null ? new Snapshot.Metadata(0, 0) : metadata, true);

        return wal;

    }

    private static WAL openAtIndex(Path dir, Snapshot.Metadata metadata, boolean write) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        List<InduceFileChannel> channels = new LinkedList<>();
        List<Path> paths = Files.list(dir).filter(x -> x.toString().endsWith(".wal")).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(paths)) {

            long index = Math.max(0, metadata.index());
            for (int i = paths.size() - 1; i >= 0; i--) {
                Matcher matcher = WAL_NAME_PATTERN.matcher(paths.get(i).toString());
                if (matcher.find()) {
                    long startIndex = Long.valueOf(matcher.group(2));
                    if (index >= startIndex) {
                        for (int j = i; j < paths.size(); j++) {
                            InduceFileChannel ch = InduceFileChannel.open(paths.get(j), StandardOpenOption.WRITE, StandardOpenOption.READ);
                            channels.add(ch);
                        }
                        break;
                    }
                }
            }
        } else {
            Path path = Paths.get(dir.toString(), concat(0, 0));
            channels.add(InduceFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ));
        }

        return new WAL(dir, channels, metadata);
    }


    public Stashed readAll() throws IOException {

        ArrayList<Entry> entries = new ArrayList<>(1);

        HardState state = HardState.EMPTY;
        Snapshot.Metadata metadata = null;
//
        Record record;
        try {
            while ((record = decoder.decode()) != null) {

                switch (record.type()) {
                    case ENTRY:
                        Entry entry = ProtoBufUtils.deserialize(record.data(), Entry.class);

                        if (entry.index() > start.index()) {
                            int index = (int) (entry.index() - start.index()) - 1;

                            if (entries.size() == index) entries.add(entry);
                            else if (index < entries.size()) {
                                entries.set(index, entry);
                            } else throw new Error("日志错误");
                        }
                        this.lastIndex = entry.index();
                        break;

                    case STATE:
                        state = ProtoBufUtils.deserialize(record.data(), HardState.class);
                        break;


                    case SNAPSHOT:
                        metadata = Json.get().readValue(record.data(), Snapshot.Metadata.class);
                        if (metadata.index() == this.start.index() && metadata.term() != this.start.term()) {
                            return new Stashed(HardState.EMPTY, null, null);
                        }
                        break;

//
//                case METADATA:break;
                }

            }
        } catch (EOFException e) {

        }

        if (this.tail() != null) {
            this.encoder = new Encoder(this.tail());
        }


        return new Stashed(state, entries, metadata);

    }


    public synchronized void save(HardState state, List<Entry> entries) throws IOException {

        if (state == null || state == HardState.EMPTY || CollectionUtils.isEmpty(entries)) return;


        //保存Entries
        for (Entry entry : entries) {
            Record record = new Record(RecordType.ENTRY, ProtoBufUtils.serialize(entry));
            this.encoder.encode(record);
            this.lastIndex = entry.index();
        }

        /********保存state*******/
        Record record = new Record(RecordType.STATE, ProtoBufUtils.serialize(state));

        this.state = state;

        this.encoder.encode(record);


        /********end************/

        encoder.flush();

        if (tail().size() < SEGMENT_SIZE_BYTES) {
            return;
        }


        this.cut();
    }

    public void release(long index) throws IOException {
        List<InduceFileChannel> released = new LinkedList<>();
        for (InduceFileChannel channel : channels) {
            Matcher matcher = WAL_NAME_PATTERN.matcher(channel.filename());
            if (matcher.find()) {
                long startIndex = Long.valueOf(matcher.group(2));
                if (startIndex < index)
                    released.add(channel);
                else if (startIndex == index) {
                    released.add(channel);
                    break;
                }
            }
        }

        if (released.isEmpty()) return;

        released.remove(released.size() - 1);

        if (released.isEmpty()) return;

        for (InduceFileChannel channel : released) {
            Files.delete(channel.path());
        }
    }


    public void save(Snapshot.Metadata metadata) throws IOException {

        Record record = new Record(RecordType.SNAPSHOT, ProtoBufUtils.serialize(metadata));
        encoder.encode(record);

        if (metadata.index() > this.lastIndex)
            this.lastIndex = metadata.index();


        encoder.flush();
    }


    private void cut() throws IOException {


        Path path = Paths.get(this.dir.toString(), concat(this.seq() + 1, this.lastIndex + 1));

        InduceFileChannel ch = InduceFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
        this.channels.add(ch);

        this.encoder = new Encoder(tail());


        if (state != null) {
            Record record = new Record(RecordType.STATE, ProtoBufUtils.serialize(state));
            this.encoder.encode(record);

            this.encoder.flush();
        }
    }


    private long seq() {
        InduceFileChannel channel = tail();
        Matcher matcher = WAL_NAME_PATTERN.matcher(channel.filename());

        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }

        return 0;
    }


    private static String concat(long seq, long index) {
        return String.format("%016x-%016x.wal", seq, index);
    }


}
