package com.bh.spider.consistent.raft.log;

import com.bh.common.utils.Json;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version $Id: Snapshotter, 2019-04-02 15:49 liuqi19
 */
public class Snapshotter {
    private final static Logger logger = LoggerFactory.getLogger(Snapshotter.class);
    public static final String SNAP_SUFFIX = ".snap";
    public static final int SNAP_COUNT_THRESHOLD = 1000;
    private Path dir;

    public Snapshotter(Path dir) {
        this.dir = dir;
    }

    public static Snapshotter create(Path dir) throws IOException {
        //创建快照目录
        Files.createDirectories(dir);
        Snapshotter snapshotter = new Snapshotter(dir);

        return snapshotter;
    }


    public Snapshot load() throws IOException {
        if (Files.exists(dir)) {

            List<Path> paths = Files.list(this.dir).filter(x -> x.endsWith(SNAP_SUFFIX)).sorted().collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(paths)) {
                Collections.reverse(paths);


                for (Path path : paths) {

                    try {
                        Snapshot snapshot = read(path);
                        if (snapshot != null) return snapshot;
                    } catch (Exception e) {
                        Path brokenPath = Paths.get(path.getParent().toString(), FilenameUtils.getBaseName(path.toString()) + ".broken");

                        Files.move(path, brokenPath);
                        logger.warn("failed to read a snap file:{},{}", path, e);
                    }

                }
            }
        }

        return null;

    }


    public long lastIndex() {
        return 0;
    }


    private Snapshot read(Path path) throws Exception {
        byte[] data = Files.readAllBytes(path);

        if (data.length == 0) throw new Exception("failed to read empty snapshot file," + path.toString());

        Snapshot snapshot = Json.get().readValue(data, Snapshot.class);


        return snapshot;
    }

    public void save(Snapshot snapshot) throws IOException {

        Snapshot.Metadata metadata = snapshot.metadata();

        String name = String.format("%016x-%016x%s", metadata.term(), metadata.index(), SNAP_SUFFIX);

        byte[] data = Json.get().writeValueAsBytes(snapshot);

        Path path = Paths.get(dir.toString(), name);


        Files.write(path, data);

    }
}
