package com.bh.spider.consistent.raft.node;

/**
 * @author liuqi19
 * @version : RaftNode, 2019-05-24 14:30 liuqi19
 */
public class RaftNode extends Node {

    /**
     * 此节点commit的index
     */
    private long match = -1;

    private long next;

    private boolean active = false;


    public RaftNode(Node node) {
        super(node);
        this.next = match + 1;
    }

    public long next() {
        return next;
    }

    public long match() {
        return match;
    }


    /**
     * 更改index,当成功同步日志之后，进行update,next 有可能大于远超于match
     *
     * @param index
     * @return
     */
    public boolean update(long index) {
        boolean updated = false;
        if (this.match < index) {
            this.match = index;
            updated = true;
        }

        if (this.next < index + 1)
            this.next = index + 1;
        return updated;
    }


    /**
     * 减少
     *
     * @param rejected
     * @param lastIndex
     * @return
     */
    public boolean decrease(long rejected, long lastIndex) {
        if (rejected < this.match) return false;

        if (this.next - 1 != rejected) return false;

        this.next = Math.min(rejected, lastIndex + 1);

        if (this.next == rejected) this.next = this.match + 1;

        if (this.next <= this.match)
            this.match = this.next - 1;

        return true;
    }

    public void optimisticUpdate(long index) {
        this.next = index + 1;
    }


    public void active(boolean value) {
        if (this.active == value) return;

        if (value) {
            this.reset();
        }
        this.active = value;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * 变为复制者
     */
    public void becomeProbe() {

        this.next = this.match + 1;

    }

    protected void reset() {
        this.next = this.match + 1;
    }
}
