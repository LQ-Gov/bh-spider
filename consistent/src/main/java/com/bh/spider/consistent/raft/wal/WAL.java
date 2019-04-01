package com.bh.spider.consistent.raft.wal;

import com.bh.spider.consistent.raft.wal.pb.Record;
import com.bh.spider.consistent.raft.wal.pb.RecordType;
import com.bh.spider.consistent.raft.wal.pb.Snapshot;
import com.google.protobuf.ByteString;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liuqi19
 * @version $Id: WAL, 2019-04-01 16:21 liuqi19
 */
public class WAL {

    private final static long SEGMENT_SIZE_BYTES = 64 * 1000 * 1000; // 64MB


    private final static Logger logger = LoggerFactory.getLogger(WAL.class);

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
    private Snapshot start;


    /**
     * index of the last entry saved to the wal
     */
    private long lastIndex;

    //这个锁用synchronized(this)代替
    //mu      sync.Mutex

    /**
     * the locked files the WAL holds (the name is increasing)
     */
    List<FileLock> locks;


    private Encoder encoder;


//
//    state    raftpb.HardState // hardstate recorded at the head of WAL
//
//    decoder   *decoder       // decoder to decode records
//    readClose func() error   // closer for decode reader
//
//    encoder *encoder // encoder to encode records
//
//    fp    *filePipeline


    private WAL(Path dir,byte[] metadata,Encoder encoder){
        this.dir = dir;
        this.metadata = metadata;
        this.locks = new LinkedList<>();
        this.encoder = encoder;
    }

    /**
     * Create creates a WAL ready for appending records. The given metadata is
     * recorded at the head of each WAL file, and can be retrieved with ReadAll.
     *
     * @param dir
     * @param metadata
     * @return
     */

    public static WAL create(Path dir, byte[] metadata) throws IOException {
        if (!Files.exists(dir)) throw new IOException(dir.toString() + "不存在");

        // keep temporary wal directory so WAL initialization appears atomic
        Path tmp = Paths.get(dir.toString() + ".tmp");

        FileUtils.deleteDirectory(tmp.toFile());

        Files.createDirectories(tmp);


        Path walFilePath = Paths.get(tmp.toString(), Utils.walName(0, 0));


        FileChannel channel = FileChannel.open(walFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        FileLock lock = channel.lock();


        //跳转到文件结尾
        channel.position(channel.size());

        //将当前文件填充为64M
        if (SEGMENT_SIZE_BYTES > channel.size()) {

            channel.write(ByteBuffer.allocate((int) (SEGMENT_SIZE_BYTES - channel.size())));
        }


        WAL wal = new WAL(dir, metadata,new Encoder(channel));
        //创建encoder(已在构造函数中创建)

        //将锁假如wal locks
        wal.locks.add(lock);

        wal.saveCRC(0);
        wal.encoder.encode(Record.newBuilder().setType(RecordType.METADATA_VALUE).setData(ByteString.copyFrom(metadata)));;

        wal.saveSnapshot(Snapshot.newBuilder().build());

        wal.renameWAL(tmp);

        return wal;

    }



    public void saveCRC(int prevCrc) throws IOException {
        Record.Builder recordBuilder = Record.newBuilder().setType(RecordType.CRC_VALUE).setCrc(prevCrc);
        this.encoder.encode(recordBuilder);
    }


    public void saveSnapshot(Snapshot snapshot) throws IOException {
        ByteString bs  = snapshot.toByteString();

        synchronized (this){
            this.encoder.encode(Record.newBuilder().setType(RecordType.SNAPSHOT_VALUE).setData(bs));
            if(this.lastIndex<snapshot.getIndex())
                this.lastIndex=snapshot.getIndex();


             this.sync();
        }

    }

    public void sync() throws IOException {
        if(this.encoder!=null)
            this.encoder.flush();
    }


    public FileLock tail(){
        if(!this.locks.isEmpty())
            return this.locks.get(this.locks.size()-1);

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
}
