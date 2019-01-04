package com.bh.spider.rule;

import com.bh.spider.fetch.Extractor;
import com.bh.spider.fetch.FetchContext;

import java.util.*;
import java.util.function.Consumer;

public class ExtractorGroup implements Iterable<String> {
    private String description;

    private Set<String> chain = new LinkedHashSet<>();

    public ExtractorGroup() {
    }

    public ExtractorGroup(String... names) {
        bind(names);
    }


    public void bind(String... names) {
        chain = new LinkedHashSet<>(Arrays.asList(names));
    }

    public void delete(String name) {
        chain.remove(name);
    }

    public void extract(FetchContext context){

    }

    @Override
    public Iterator<String> iterator() {
        return chain.iterator();
    }

    @Override
    public void forEach(Consumer<? super String> action) {
        chain.forEach(action);
    }

    @Override
    public Spliterator<String> spliterator() {
        return chain.spliterator();
    }


    private class Node {
        public Node(Extractor it) {

        }


        public Node next() {
            return null;
        }
    }


}
