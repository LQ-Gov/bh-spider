package com.bh.spider.scheduler.domain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface DomainIndex {
    Node root();

    Node match(String path);

    Node match(String path, boolean exact);

    Node matchOrCreate(String path);


    public static class Node {
        private String name;
        private Node parent;
        private Map<String, Node> children;

        private List<RuleWrapper> rules;


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

        public void bind(RuleWrapper rule) {
            if (rules == null)
                rules = new LinkedList<>();
            rules.add(rule);
        }


        public void unbind(RuleWrapper rule) {
        }

        public Collection<RuleWrapper> rules() {
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
