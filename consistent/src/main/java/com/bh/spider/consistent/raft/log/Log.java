package com.bh.spider.consistent.raft.log;

import com.bh.common.utils.ArrayUtils;
import com.bh.spider.consistent.raft.Actuator;
import com.bh.spider.consistent.raft.HardState;
import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.consistent.raft.wal.WAL;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version : Log, 2019-04-08 16:05 liuqi19
 */
public class Log {

    private final static Logger logger = LoggerFactory.getLogger(Log.class);

    private final Raft raft;

    // applied is the highest log position that the application has
    // been instructed to apply to its state machine.
    // Invariant: applied <= committed
    private long applied;


    // committed is the highest log position that is known to be in
    // stable storage on a quorum of nodes.
    private long committed;


    private long stable;


    private long offset;


    private List<Entry> entries = new ArrayList<>();

    private Snapshotter snapshotter;

    private final Persistent persistent;

    private Snapshot snapshot;


    public Log(Raft raft, Snapshotter snapshotter, WAL wal, Actuator actuator) {

        this.raft = raft;

        this.committed = -1;

        this.offset = -1;

        this.applied = -1;

        this.stable = -1;

        this.snapshotter = snapshotter;
        this.persistent = new Persistent(raft, wal, snapshotter, actuator);

        this.persistent.start();
    }


    public void recover(Snapshot.Metadata metadata, List<Entry> entries) throws Exception {

        if(metadata!=null) {
            this.committed = metadata.index();
            this.offset = metadata.index();
            this.stable = metadata.index();
            this.applied = metadata.index();
        }

        if (entries != null) {
            entries = entries.stream().filter(x -> x.index() >= this.offset).collect(Collectors.toList());
            if (entries.isEmpty()) return;

            Entry first = entries.get(0);

            if (first.index() != offset+1) throw new Exception("非连续的entries");

            this.entries.addAll(entries);

            this.stable = entries.get(entries.size() - 1).index();
        }
    }


    public long committedIndex() {
        return committed;
    }

    public long append(Entry[] entries) {
        if (ArrayUtils.isEmpty(entries)) return this.lastIndex();

        if (entries[0].index() - 1 < this.committed) {
            logger.error("after({}) is out of range [committed({})]", entries[0].index(), this.committed);
            return -1;
        }

        long after = entries[0].index();

        if (offset + this.entries.size() + 1 == after)
            this.entries.addAll(Arrays.asList(entries));

            // The log is being truncated to before our current offset
            // portion, so set the offset and replace the entries
        else {
            logger.info("truncate the unstable entries before index {}", after);

            List<Entry> newCollection = new ArrayList<>(this.slice(offset + 1, after, Integer.MAX_VALUE));
            newCollection.addAll(Arrays.asList(entries));
            this.entries = newCollection;

            if (after <= this.stable)
                this.stable = after - 1;
        }
        synchronized (raft) {
            this.raft.notify();
        }

        return this.lastIndex();
    }


    public long append(Entry.Collection collection) {
        if (collection == null) return -1;

        if (this.term(collection.index()) != collection.term()) {
            logger.error("Entry collection term:({}),index:({}),not match with current log", collection.term(), collection.index());
            return -1;
        }


        long conflictIndex = findConflict(collection.entries());

        if (conflictIndex == -1) return collection.lastIndex();

        if (conflictIndex <= this.committed) {
            logger.error("entry {} conflict with committed entry [committed({})]", conflictIndex, this.committed);
            return -1;
        }

        Entry[] entries = collection.entries();

        if (conflictIndex > collection.firstIndex()) {
            entries = ArrayUtils.subarray(entries, (int) (conflictIndex - collection.firstIndex()), collection.size());
        }


        this.append(entries);

        this.commitTo(Math.min(collection.committedIndex(), collection.lastIndex()));

        synchronized (raft) {
            this.raft.notify();
        }

        return collection.lastIndex();

    }


    private long findConflict(Entry[] entries) {
        for (Entry entry : entries) {
            long index = entry.index();

            if (this.term(index) != entry.term()) {

                if (index < this.lastIndex()) {
                    logger.error("found conflict at index {} [existing term: {}, conflicting term: {}]",
                            index, this.term(index), entry.term());
                }

                return index;
            }
        }

        return -1;
    }


    public void commitTo(long index) {
        // never decrease commit
        if (this.committed < index) {
            if (this.lastIndex() < index) {
                logger.error("index {} is out of range [lastIndex:{}]. Was the raft log corrupted, truncated, or lost?", index, this.lastIndex());
            } else {
                this.committed = index;
                synchronized (raft) {
                    this.raft.notify();
                }
            }
        }


    }


    public boolean commit(long term, long index) {

        if (index > this.committed && this.term(index) == term) {
            this.commitTo(index);
            return true;
        }

        return false;

    }


    public Entry[] entries(long startIndex, int size) {
        return this.slice(startIndex, this.lastIndex() + 1, size).toArray(new Entry[0]);
    }


    public Entry entry(long index) {
        if (index <= offset || index > lastIndex()) return null;

        return this.entries.get((int) (index - offset - 1));
    }

    public void restore(Snapshot snapshot) {
        if (snapshot.metadata().index() <= committedIndex()) {
            logger.error("snapshot index:{} lte than committed index:{}", snapshot.metadata().index(), committedIndex());
            return;
        }
        this.snapshot = snapshot;

        this.committed = snapshot.metadata().index();

        this.offset = snapshot.metadata().index();

        this.stable = snapshot.metadata().index();

        this.entries = new ArrayList<>();

        synchronized (raft) {
            this.raft.notify();
        }

    }


    /**
     * 未持久化的entries
     *
     * @return
     */
    private List<Entry> unstableEntries() {
        if (stable >= lastIndex()) return Collections.emptyList();
        return new UnmodifiableList<>(this.slice(stable + 1, lastIndex()+1, Integer.MAX_VALUE));
    }


    /**
     * nextEntries returns all the available entries for execution.
     * If applied is smaller than the index of snapshot, it returns all committed
     * entries after the index of snapshot.
     */

    private List<Entry> nextEntries() {
        long off = Math.max(this.applied + 1, this.firstIndex()+1);

        if (committed + 1 > off) {
            return this.slice(off, committed + 1, Integer.MAX_VALUE);
        }
        return null;
    }

    /**
     * 返回index所在的term
     *
     * @param index
     * @return
     */
    public long term(long index) {

        if (index < 0 || index < firstIndex() || index > lastIndex())
            return -1;

        if (this.snapshot != null && this.snapshot.metadata().index() == index)
            return this.snapshot.metadata().term();

        Snapshot.Metadata metadata = snapshotter.lastMetadata();
        if (metadata != null && metadata.index() == index)
            return metadata.term();

        return entries.get((int) (index - offset - 1)).term();
    }


    public long firstIndex() {

        return offset;

    }


    public long lastIndex() {
        return offset + entries.size();
    }


    public long lastTerm() {
        if (CollectionUtils.isEmpty(entries)) {
            if (snapshot != null && snapshot.metadata().index() == offset)
                return snapshot.metadata().term();
            if (snapshotter.lastMetadata() != null && snapshotter.lastIndex() == offset)
                return snapshotter.lastMetadata().term();

            return 0;
        }

        return entries.get(entries.size() - 1).term();
    }


    private List<Entry> slice(long lo, long hi, int size) {

        if (lo > this.offset) {
            List<Entry> entries = this.entries.subList((int) (lo - this.offset - 1), (int) (hi - offset - 1));

            return Collections.unmodifiableList(entries);
        }

        return Collections.emptyList();

    }


    public int compare(long term, long index) {

        if (term > lastTerm()) return -1;

        if (term == lastTerm() && index > lastIndex()) return -1;

        if (term == lastTerm() && index == lastIndex()) return 0;

        return 1;
    }


    private void stableTo(long term, long index) {
        if (term == this.term(index))
            this.stable = index;
    }

    private void applyTo(long index) {

        if (index > committed || applied > index) {
            logger.error("applied({}) is out of range [prevApplied({}), committed({})]", index, applied, committed);
            return;
        }
        this.applied = index;
    }

    private void snapshotTo(Snapshot.Metadata metadata) {
        if (snapshot != null && snapshot.metadata().equals(metadata))
            this.snapshot = null;

        if (stable < metadata.index())
            stable = metadata.index();

        this.entries = new ArrayList<>(this.slice(metadata.index()+1, lastIndex() + 1, Integer.MAX_VALUE));


        this.offset = metadata.index();

        if(metadata.index()>applied)
            this.applied = metadata.index();

    }


    public Snapshot snapshot() {
        return snapshotter.lastSnapshot();
    }


    private class Persistent extends Thread {


        private final Raft raft;

        private WAL wal;


        private Snapshotter snapshotter;

        private Actuator actuator;

        public Persistent(Raft raft, WAL wal, Snapshotter snapshotter, Actuator actuator) {

            this.raft = raft;

            this.wal = wal;

            this.snapshotter = snapshotter;

            this.actuator = actuator;

            this.setDaemon(true);
        }


        @Override
        public void run() {

            while (true) {

                synchronized (raft) {
                    try {
                        long appliedIndex = -1;
                        List<Entry> entries = Log.this.unstableEntries();

                        List<Entry> committedEntries = Log.this.nextEntries();

                        Snapshot snapshot = Log.this.snapshot;


                        if (CollectionUtils.isEmpty(entries) && CollectionUtils.isEmpty(committedEntries) && snapshot == null) {
                            raft.wait(1000 * 60 * 10);
                            continue;

                        }


                        HardState state = raft.hardState();

                        //检查是否有未持久化的entries，进行持久化
                        if (CollectionUtils.isNotEmpty(entries)) {

                            this.wal.save(state, entries);
                            Entry entry = entries.get(entries.size() - 1);

                            Log.this.stableTo(entry.term(), entry.index());

                        }

                        //检查是否有未持久化的snap(由leader同步过来的),进行持久化并recover到状态机
                        if (snapshot != null) {
                            snapshotter.save(snapshot);
                            this.wal.save(snapshot.metadata());
                            this.wal.release(snapshot.metadata().index());
                            Log.this.snapshotTo(snapshot.metadata());
                            this.actuator.recover(snapshot.data());
                        }

                        //将已经commit的日志应用到状态机
                        if (CollectionUtils.isNotEmpty(committedEntries)) {

                            for (Entry entry : committedEntries) {
                                if (entry.data() == null || entry.data().length == 0)
                                    continue;

                                this.actuator.apply(entry.data());
                                appliedIndex = entry.index();
                            }
                        }

                        try {
                            //生成快照
                            if (appliedIndex - snapshotter.lastIndex() >= Snapshotter.SNAP_COUNT_THRESHOLD) {


                                Entry entry = Log.this.entry(appliedIndex);

                                byte[] snap = this.actuator.snapshot();

                                snapshot = new Snapshot(new Snapshot.Metadata(entry.term(), entry.index()), snap);


                                snapshotter.save(snapshot);

                                this.wal.save(snapshot.metadata());

                                this.wal.release(snapshot.metadata().index());


                                Log.this.snapshotTo(snapshot.metadata());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (appliedIndex >= 0) {

                            Log.this.applyTo(appliedIndex);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
