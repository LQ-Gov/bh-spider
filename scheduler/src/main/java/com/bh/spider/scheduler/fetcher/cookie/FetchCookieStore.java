package com.bh.spider.scheduler.fetcher.cookie;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.*;

public class FetchCookieStore implements CookieStore {
    private Node root = new Node(".");

    @Override
    public void add(URI uri, HttpCookie cookie) {
        if (!cookie.hasExpired()) {
            root.setCookie(uri, cookie);
        }


    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return null;
    }

    @Override
    public List<HttpCookie> getCookies() {
        return null;
    }

    @Override
    public List<URI> getURIs() {
        return null;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return false;
    }

    @Override
    public boolean removeAll() {
        return false;
    }


    private static class Node {
        private Map<String, HttpCookie> cookies = new HashMap<>();
        private Map<String, Node> children = new HashMap<>();
        private Node parent;
        private String name;

        Node(String name) {
            this.name = name;

        }

        private Node addHost(String[] blocks, int index) {
            if (blocks == null || blocks.length == 0) return null;
            if (blocks[index].equals(name) || ".".equals(name)) {
                if (index == blocks.length - 1) return this;
                if (!".".equals(name)) index++;

                Node next = children.putIfAbsent(blocks[index], new Node(blocks[index]));
                next.parent = this;
                return next.addHost(blocks, index);
            }

            return null;
        }



        public synchronized void setCookie(URI uri, HttpCookie cookie) {
            String host = cookie.getDomain();
            String[] blocks = host.split(".");
            Node node = addHost(blocks, 0);
            if (node != null && !cookie.hasExpired()) {
                node.cookies.put(cookie.getName(), cookie);
            }
        }


        public synchronized List<HttpCookie> matchCookies(String host) {
            return null;
        }


        public synchronized void clearExpired() {

        }


    }
}
