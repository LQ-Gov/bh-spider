package com.charles.common.glob;

import com.charles.common.utils.ArrayUtils;

/**
 * Created by lq on 17-6-3.
 */
public abstract class AbstractPattern {

    private final static char[] SPECIAL_CHARS= new char[] {'*','?','[','!','\\','-'};

    protected String segment =null;


    protected AbstractPattern next = null;
    protected AbstractPattern parent = null;


    protected boolean isSpecial(char c) {
        return ArrayUtils.indexOf(SPECIAL_CHARS, c) != ArrayUtils.INDEX_NOT_FOUND;
    }

    public String segment(){return segment;}

    public AbstractPattern next(){return next;}

    public AbstractPattern parent(){return parent;}

    public int length(){return segment==null?0:segment.length(); }


}
