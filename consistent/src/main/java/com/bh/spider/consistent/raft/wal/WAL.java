package com.bh.spider.consistent.raft.wal;

import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.HardState;
import com.bh.spider.consistent.raft.log.Entry;
import com.bh.spider.consistent.raft.log.Snapshot;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    /**
     * dirFile is a fd for the wal directory for syncing on Rename
     */
    private File dirFile;

    /**
     * metadata recorded at the head of each WAL
     */
    private byte[] metadata;

    /**
     * snapshot to start reading
     */
    private Index start;


    /**
     * index of the last entry saved to the wal
     */
    private long lastIndex;

    //这个锁用synchronized(this)代替
    //mu      sync.Mutex

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


//
//    state    raftpb.HardState // hardstate recorded at the head of WAL
//
//    readClose func() error   // closer for decode reader
//
//    fp    *filePipeline

    private WAL() {
    }

    private WAL(Path dir, byte[] metadata, Encoder encoder) {
        this.dir = dir;
        this.metadata = metadata;
        this.channels = new LinkedList<>();
        this.encoder = encoder;
    }


    private WAL(Path dir, List<InduceFileChannel> channels) {
        this.dir = dir;
        this.channels = channels;
        this.decoder = new Decoder(channels);
    }

    /**
     * Create creates a WAL ready for appending records. The given metadata is
     * recorded at the head of each WAL file, and can be retrieved with ReadAll.
     *
     * @param dir
     * @param metadata
     * @return
     */

    public static WAL open(Path dir, byte[] metadata) throws IOException {
        Files.createDirectories(dir);

        // keep temporary wal directory so WAL initialization appears atomic
        Path tmp = Paths.get(dir.toString() + ".tmp");

        FileUtils.deleteDirectory(tmp.toFile());

        Files.createDirectories(tmp);


        Path walFilePath = Paths.get(tmp.toString(), Utils.walName(0, 0));


        InduceFileChannel channel = InduceFileChannel.open(walFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);


        //跳转到文件结尾
        channel.position(channel.size());

        //将当前文件填充为64M
        if (SEGMENT_SIZE_BYTES > channel.size()) {

            channel.write(ByteBuffer.allocate((int) (SEGMENT_SIZE_BYTES - channel.size())));
        }


        WAL wal = new WAL(dir, metadata, new Encoder(channel));
        //创建encoder(已在构造函数中创建)

        //将锁假如wal locks
        wal.channels.add(channel);

        wal.saveCRC(0);
//        wal.encoder.encode(Record.newBuilder().setType(RecordType.METADATA_VALUE).setData(ByteString.copyFrom(metadata)));
        ;

//        wal.saveSnapshot(Snapshot.newBuilder().build());

        wal.renameWAL(tmp);

        return wal;

    }


    public void saveCRC(int prevCrc) throws IOException {
//        Record.Builder recordBuilder = Record.newBuilder().setType(RecordType.CRC_VALUE).setCrc(prevCrc);
//        this.encoder.encode(recordBuilder);
    }


    public void saveSnapshot(Snapshot snapshot) throws IOException {
//        ByteString bs = snapshot.toByteString();

        synchronized (this) {
//            this.encoder.encode(Record.newBuilder().setType(RecordType.SNAPSHOT_VALUE).setData(bs));
//            if(this.lastIndex<snapshot.getIndex())
//                this.lastIndex=snapshot.getIndex();


            this.sync();
        }

    }

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

        WAL wal = openAtIndex(dir, metadata == null ? 0 : 0, true);


        return wal;

    }

    private static WAL openAtIndex(Path dir, long index, boolean write) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        List<InduceFileChannel> channels = new LinkedList<>();
        List<Path> wals = Files.list(dir).filter(x -> x.toString().endsWith(".wal")).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(wals)) {

            index = Math.max(0, index);
            for (int i = wals.size() - 1; i >= 0; i--) {
                Matcher matcher = WAL_NAME_PATTERN.matcher(wals.get(i).toString());
                if (matcher.find()) {
                    long startIndex = Long.valueOf(matcher.group(2));
                    if (index >= startIndex) {
                        for (int j = i; j < wals.size(); j++) {
                            InduceFileChannel ch = InduceFileChannel.open(wals.get(j), StandardOpenOption.WRITE, StandardOpenOption.READ);
                            channels.add(ch);
                        }
                        break;
                    }
                }
            }
        } else {
            Path path = Paths.get(dir.toString(), buildWalName(0, 0));
            channels.add(InduceFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ));
        }

        return new WAL(dir, channels);
    }


    public Stashed readAll() throws IOException {

        List<Entry> entries = new LinkedList<>();
        HardState state = null;
        Snapshot.Metadata metadata = null;
//
        Record record;
        try {
            while ((record = decoder.decode()) != null) {

                switch (record.type()) {
                    case ENTRY:
                        Entry entry = Json.get().readValue(record.data(), Entry.class);
//                    if(entry.index()>start.getIndex())
                        entries.add(entry);
                        break;

                    case STATE:
                        state = Json.get().readValue(record.data(), HardState.class);
                        break;


                    case SNAPSHOT:
                        metadata =Json.get().readValue(record.data(), Snapshot.Metadata.class);
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


        return new Stashed(state, entries,metadata);

    }


    public synchronized void save(HardState state, List<Entry> entries) throws IOException {
        if(true) return;


        if (state == null || !state.isValid() || CollectionUtils.isEmpty(entries)) return;


        //保存Entries
        for (Entry entry : entries) {
            byte[] data = Json.get().writeValueAsBytes(entry);


            Record record = new Record(RecordType.ENTRY, data);

            encoder.encode(record);
        }

        //保存state
        Record record = new Record(RecordType.STATE, Json.get().writeValueAsBytes(state));

        encoder.encode(record);

        encoder.flush();

        if (tail().size() < SEGMENT_SIZE_BYTES) {
            return;
        }


        this.cut();
    }


    public void save(Snapshot.Metadata metadata) throws IOException{

        Record record = new Record(RecordType.SNAPSHOT,Json.get().writeValueAsBytes(metadata));
        encoder.encode(record);

        if(metadata.index()>this.lastIndex)
            this.lastIndex = metadata.index();


        encoder.flush();
    }


    private void cut() throws IOException {


        Path path = Paths.get(this.dir.toString(), buildWalName(this.seq() + 1, this.lastIndex + 1));

//        Path tmp = Paths.get(path.toString()+".tmp");
        InduceFileChannel ch = InduceFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
        this.channels.add(ch);

        this.encoder = new Encoder(tail());

//        Files.move(tmp,path);

    }


    private long seq() {
        InduceFileChannel channel = tail();
        Matcher matcher = WAL_NAME_PATTERN.matcher(channel.filename());

        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }

        return 0;
    }


    private static String buildWalName(long seq, long index) {
        return String.format("%016x-%016x.wal", seq, index);
    }


    public void reply() {
    }
}
