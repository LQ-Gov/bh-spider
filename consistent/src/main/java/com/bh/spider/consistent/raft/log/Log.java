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

import java.util.*;

/**
 * @author liuqi19
 * @version : Log, 2019-04-08 16:05 liuqi19
 */
public class Log {

    private final static Logger logger = LoggerFactory.getLogger(Log.class);

    private final Raft raft;

//    private Unstable unstable;

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


    private final Persistent persistent;


    private Snapshot.Metadata snapshot;


    public Log(Raft raft, Snapshotter snapshotter, WAL wal, Actuator actuator) {

        this.raft = raft;

        this.committed = -1;

        this.offset = 0;

        this.applied = -1;

        this.stable = -1;

//        this.unstable = new Unstable(0);

        this.persistent = new Persistent(raft, wal, snapshotter, actuator);

        this.persistent.start();
    }


    public void recover(Snapshot.Metadata metadata, List<Entry> entries) {
        this.committed = metadata.index();
        this.offset = metadata.index() + 1;

        this.applied = metadata.index();

        if (CollectionUtils.isNotEmpty(entries)) {
            this.entries.addAll(entries);
            this.stable = entries.get(entries.size() - 1).index();
            this.offset = entries.get(0).index();

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

        if (offset + this.entries.size() == after)
            this.entries.addAll(Arrays.asList(entries));

            // The log is being truncated to before our current offset
            // portion, so set the offset and replace the entries
        else {
            logger.info("truncate the unstable entries before index {}", after);

            List<Entry> newCollection = new ArrayList<>(this.slice(offset, after, Integer.MAX_VALUE));
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


        Entry[] entries = ArrayUtils.subarray(collection.entries(), (int) (conflictIndex - collection.firstIndex()), collection.size());

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

            if (this.term(index) != entry.index()) {

                if (index < this.lastIndex()) {
                    logger.error("found conflict at index {} [existing term: {}, conflicting term: {}]",
                            index, this.term(index), entry.term());
                }

                return index;
            }
        }

        return 0;
    }


    public void commitTo(long index) {
        // never decrease commit
        if (this.committed < index) {
            if (this.lastIndex() < index) {
                logger.error("index {} is out of range [lastIndex{}]. Was the raft log corrupted, truncated, or lost?", index, this.lastIndex());
            } else
                this.committed = index;
        }

        synchronized (this.persistent) {
            this.persistent.notify();
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
        Entry[] ents = entries(index, 1);
        return ents != null && ents.length > 0 ? ents[0] : null;
    }


    /**
     * 未持久化的entries
     *
     * @return
     */
    private List<Entry> unstableEntries() {
        if (stable >= lastIndex()) return Collections.emptyList();
        return new UnmodifiableList<>(this.slice(stable + 1, lastIndex(), Integer.MAX_VALUE));
    }


    /**
     * nextEntries returns all the available entries for execution.
     * If applied is smaller than the index of snapshot, it returns all committed
     * entries after the index of snapshot.
     */

    private List<Entry> nextEntries() {
        long off = Math.max(this.applied, this.firstIndex());

        if (committed > off) {
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

        if (index < firstIndex() || index > lastIndex())
            return -1;


        return entries.get((int) (index - offset)).term();

    }


    private long firstIndex() {
        return offset;
    }


    public long lastIndex() {
        if (!entries.isEmpty()) return entries.get(entries.size() - 1).index();

        return -1;
    }


    public long lastTerm() {
        return 0;
    }


    private List<Entry> slice(long lo, long hi, int size) {


        hi = Math.min(lastIndex() + 1, Math.min(lo + size, hi));

        if (lo >= this.offset) {
            List<Entry> entries = this.entries.subList((int) (lo - this.offset), (int) (hi - offset));

            return Collections.unmodifiableList(entries);
        }

        return Collections.emptyList();

    }


    public boolean compare(long term, long index) {
        return true;
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
        this.snapshot = metadata;


        if (stable < metadata.index())
            stable = metadata.index();


        this.entries = new ArrayList<>(this.entries.subList((int) (metadata.index() - offset), this.entries.size()));

        this.offset = metadata.index() + 1;

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

                        if (CollectionUtils.isEmpty(entries) && CollectionUtils.isEmpty(committedEntries)) {
                            raft.wait(1000 * 60 * 10);
                            continue;

                        }


                        HardState state = raft.hardState();

                        if (CollectionUtils.isNotEmpty(entries)) {

                            this.wal.save(state, entries);
                            Entry entry = entries.get(entries.size() - 1);

                            Log.this.stableTo(entry.term(), entry.index());

                        }

                        //应用到状态机
                        if (CollectionUtils.isNotEmpty(committedEntries)) {

                            for (Entry entry : committedEntries) {
                                if (entry.data() == null || entry.data().length == 0)
                                    continue;

                                this.actuator.apply(entry.data());


                                appliedIndex = entry.index();

                                logger.info("applyIndex:{}", appliedIndex);
                            }
                        }

                        try {
                            //生成快照
                            if (appliedIndex - snapshotter.lastIndex() >= Snapshotter.SNAP_COUNT_THRESHOLD) {


                                Entry entry = Log.this.entry(appliedIndex);

                                byte[] snap = this.actuator.snapshot();

                                Snapshot snapshot = new Snapshot(new Snapshot.Metadata(entry.term(), entry.index()), snap);


                                snapshotter.save(snapshot);


                                this.wal.save(snapshot.metadata());

                                this.wal.release(snapshot.metadata().index());


                                Log.this.snapshotTo(snapshot.metadata());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (appliedIndex > 0) {

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
