package com.charles.common.glob;

import com.charles.common.utils.ArrayUtils;

/**
 * Created by lq on 17-6-3.
 */
public abstract class GlobToken {

    private final static char[] SPECIAL_CHARS= new char[] {'*','?','[','!','\\','-'};

    private GlobToken next = null;

    private GlobToken parent = null;


    protected boolean isSpecial(char c) {
        return ArrayUtils.indexOf(SPECIAL_CHARS, c) != ArrayUtils.INDEX_NOT_FOUND;
    }


    public abstract int length();

    public GlobToken getNext() {
        return next;
    }

    public void setNext(GlobToken next) {
        this.next = next;
    }

    public GlobToken getParent() {
        return parent;
    }

    public void setParent(GlobToken parent) {
        this.parent = parent;
    }

}
