package com.bh.spider.scheduler.domain;

import com.bh.spider.rule.Rule;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DomainIndex {

    Node match(String path);

    Node match(String path, boolean exact);

    Node matchOrCreate(String path);

    void delete(String path, boolean force) throws Exception;

    void delete(String path) throws Exception;


    public static class Node {
        private String name;
        private Node parent;
        private Map<String, Node> children;

        private List<Rule> rules;


        public Node(String name, Node parent) {
            this.name = name;
            this.parent = parent;
        }


        public String name() {
            return name;
        }

        public Node parent() {
            return parent;
        }

        public void bind(Rule rule) {
        }


        public void unbind(Rule rule) {
        }

        public Collection<Rule> rules() {
            return null;
        }

        public String host() {
            return null;
        }


        public Collection<Node> children() {
            return null;
        }

        public Node children(String name) {
            return null;
        }

        public Node children(String name, Node child) {
            return children.computeIfAbsent(name, x -> child);
        }

        public void removeListener() {
        }
    }
}
