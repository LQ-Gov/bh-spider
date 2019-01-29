package com.bh.spider.rule;

import java.util.Map;

public class ExtractQueue {
    private String name;
    private String description;
    private Map<String,String[]> chains;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String[]> getChains() {
        return chains;
    }

    public void setChains(Map<String, String[]> chains) {
        this.chains = chains;
    }
}
