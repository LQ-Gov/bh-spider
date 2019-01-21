package com.bh.spider.scheduler.domain;

import java.util.StringTokenizer;

public class DefaultDomainIndex implements DomainIndex {
    private Node root = new Node(null,null);

    @Override
    public Node root() {
        return root;
    }

    @Override
    public Node match(String path) {
        return match(path,true);
    }

    @Override
    public Node match(String path, boolean exact) {
        StringTokenizer tokenizer = new StringTokenizer(path,".");
        Node node = root;

        while (tokenizer.hasMoreTokens()&&node!=null) {
            String token = tokenizer.nextToken();
            Node child = root.children(token);
            if (child == null && !exact)
                return node;
            node = child;
        }
        return node;
    }

    @Override
    public Node matchOrCreate(String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, ".");
        Node node = root;

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            node = root.children(token, new Node(token, root));
        }
        return node;
    }
}
