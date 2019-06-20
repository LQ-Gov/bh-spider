package com.bh.spider.common.rule;

import java.util.LinkedList;
import java.util.List;

public class SeleniumRule extends Rule {
    private Class<?> _class = SeleniumRule.class;

    private List<Script> scripts = new LinkedList<>();

    public List<Script> scripts(){return scripts;}
}
