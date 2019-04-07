package com.bh.spider.consistent.raft.snap;

import com.bh.spider.consistent.raft.pb.Snapshot;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version $Id: Snapshotter, 2019-04-02 15:49 liuqi19
 */
public class Snapshotter {
    private final static Logger logger = LoggerFactory.getLogger(Snapshotter.class);
    public static final String SNAP_SUFFIX = ".snap";
    private Path dir;

    public Snapshotter(Path dir) {
        this.dir = dir;
    }

    public static Snapshotter create(Path dir) {
        Snapshotter snapshotter = new Snapshotter(dir);
        return snapshotter;
    }


    public Snapshot load() throws IOException {

        List<Path> paths = Files.list(this.dir).filter(x -> x.endsWith(SNAP_SUFFIX)).collect(Collectors.toList());

        for (Path path : paths) {

            try {
                Snapshot snapshot = read(path);
                if(snapshot!=null) return snapshot;
            } catch (Exception e) {
                Path brokenPath = Paths.get(path.getParent().toString(), FilenameUtils.getBaseName(path.toString()) + ".broken");

                Files.move(path, brokenPath);
                logger.warn("failed to read a snap file:{},{}", path, e);
            }

        }

        return null;

    }


    private Snapshot read(Path path) throws Exception {
        byte[] data = Files.readAllBytes(path);

        if (data.length == 0) throw new Exception("failed to read empty snapshot file," + path.toString());


        Snapshot snapshot = Snapshot.parseFrom(data);


        return snapshot;
    }
}
