package com.bh.spider.consistent.raft.wal;

import com.bh.spider.consistent.raft.wal.pb.WalProto;
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
    private WalProto.Snapshot start;


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

    private WAL(Path dir, byte[] metadata) {
        this(dir,metadata,new Encoder());
    }


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


        WAL wal = new WAL(dir, metadata);
        //创建encoder(已在构造函数中创建)

        //将锁假如wal locks
        wal.locks.add(lock);


    }



    public void saveCrc(int prevCrc){
        WalProto.Record record = new WalProto.Record.Builder()
                .setType()
        return this.encoder.encode( {Type: crcType, Crc: prevCrc})
    }


}
