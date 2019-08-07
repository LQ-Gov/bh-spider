package com.bh.spider.consistent.raft.log;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version $Id: Snapshotter, 2019-04-02 15:49 liuqi19
 */
public class Snapshotter {
    private final static Logger logger = LoggerFactory.getLogger(Snapshotter.class);

    private final static Pattern SNAPSHOT_NAME_PATTERN = Pattern.compile("([a-fA-F0-9]+)-([a-fA-F0-9]+)\\.snap");

    public static final String SNAP_SUFFIX = ".snap";

    public static final int SNAP_COUNT_THRESHOLD = 50;
    private final int SNAP_FILE_MAX_SIZE;
    private Path dir;


    private List<Path> files;

    private Snapshot.Metadata last;

    public Snapshotter(Path dir) {
        this.dir = dir;
        this.SNAP_FILE_MAX_SIZE = 3;
    }

    public static Snapshotter create(Path dir) throws IOException {
        //创建快照目录
        Files.createDirectories(dir);
        Snapshotter snapshotter = new Snapshotter(dir);

        return snapshotter;
    }


    public Snapshot load() throws IOException {
        if (Files.exists(dir)) {

            this.files = Files.list(this.dir).filter(x -> x.toString().endsWith(SNAP_SUFFIX)).sorted(
                    (o1, o2) -> {
                        long i1 = extractIndex(o1);
                        long i2 = extractIndex(o2);
                        if (i1 == i2) return 0;
                        return i1 > i2 ? 1 : -1;
                    }
            ).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(files)) {
                Collections.reverse(files);
                try {
                    Iterator<Path> iterator = files.iterator();

                    while (iterator.hasNext()) {
                        Path path = iterator.next();

                        try {
                            Snapshot snapshot = read(path);
                            if (snapshot != null) {
                                last = snapshot.metadata();
                                return snapshot;
                            }
                        } catch (Exception e) {
                            iterator.remove();

                            Path brokenPath = Paths.get(path.getParent().toString(), FilenameUtils.getBaseName(path.toString()) + ".broken");
                            Files.move(path, brokenPath);
                            logger.warn("failed to read a snap file:{},{}", path, e);
                        }

                    }
                } finally {
                    Collections.reverse(files);
                }

            }
        }

        return null;

    }


    public long lastIndex() {
        return last == null ? -1 : last.index();
    }


    public Snapshot.Metadata lastMetadata() {
        return last;
    }


    public Snapshot lastSnapshot() {
        if (files.isEmpty()) return null;

        Path path = files.get(files.size() - 1);
        try {
            return read(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Snapshot read(Path path) throws Exception {
        byte[] data = Files.readAllBytes(path);

        if (data.length == 0) throw new Exception("failed to read empty snapshot file," + path.toString());


        Snapshot snapshot = Snapshot.deserialize(data);


        return snapshot;
    }

    public void save(Snapshot snapshot) throws IOException {

        Snapshot.Metadata metadata = snapshot.metadata();

        String name = String.format("%016x-%016x%s", metadata.term(), metadata.index(), SNAP_SUFFIX);

        byte[] data = snapshot.serialize();

        Path path = Paths.get(dir.toString(), name);


        Files.write(path, data);

        if (files.isEmpty() || !files.get(files.size() - 1).equals(path))
            files.add(path);

        if (metadata.index() > lastIndex())
            this.last = metadata;


        if (files.size() > SNAP_FILE_MAX_SIZE) {
            List<Path> discard = files.subList(0, files.size() - SNAP_FILE_MAX_SIZE);
            for (Path it : discard) {
                Files.deleteIfExists(it);
                files.remove(it);
            }
        }
    }

    private long extractIndex(Path path) {
        Matcher matcher = SNAPSHOT_NAME_PATTERN.matcher(path.getFileName().toString());
        if (matcher.find()) {
            return Long.parseLong(matcher.group(2), 16);
        }

        return -1;
    }
}
