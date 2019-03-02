package com.bh.spider.scheduler.domain;

import java.util.*;

public interface DomainIndex {
    Node root();

    Node match(String path);

    Node match(String path, boolean exact);

    Node matchOrCreate(String path);


    public static class Node {
        private String name;
        private Node parent;
        private Map<String, Node> children;

        private List<RuleFacade> rules;


        public Node(String name, Node parent) {
            this.name = name;
            this.parent = parent;
            this.children = new HashMap<>();
            this.rules = new LinkedList<>();
        }


        public String name() {
            return name;
        }

        public Node parent() {
            return parent;
        }

        public void bind(RuleFacade rule) {
            rules.add(rule);
        }


        public void unbind(RuleFacade rule) {
            rules.removeIf(x->x==rule);
        }

        public Collection<RuleFacade> rules() {
            return rules;
        }

        public String host() {
            return (parent != null && parent.name != null) ? name + "." + parent.host() : name;
        }


        public Collection<Node> children() {
            return children.values();
        }

        public Node children(String name) {
            return children(name,false);
        }

        public Node children(String name, boolean force) {
            if (force)
                return children.computeIfAbsent(name, x -> new Node(x, this));
            else
                return children.get(name);
        }

        public void removeListener() {
        }
    }
}
