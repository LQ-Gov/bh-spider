package com.bh.spider.scheduler.domain;

import com.bh.spider.common.fetch.Request;

import java.util.Comparator;

public interface RulePattern {


    String host();

    String domain();

    String path();



    Comparator<RulePattern> getComparator(Request request);





}
