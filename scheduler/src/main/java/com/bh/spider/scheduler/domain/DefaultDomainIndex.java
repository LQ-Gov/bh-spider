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
        String[] tokens = reverse(new StringTokenizer(path, "."));
        Node node = root;

        for (String token : tokens) {
            Node child = node.children(token);
            if (child == null)
                return exact ? node : null;
        }
        return node;
    }

    @Override
    public Node matchOrCreate(String path) {
        String[] tokens = reverse(new StringTokenizer(path, "."));
        Node node = root;

        for (String token : tokens) {
            node = node.children(token, true);
        }
        return node;
    }


    private String[] reverse(StringTokenizer tokenizer){
        int count = tokenizer.countTokens();
        String[] tokens = new String[count];

        while (tokenizer.hasMoreTokens()) {
            tokens[--count] = tokenizer.nextToken();
        }
        return tokens;
    }
}
