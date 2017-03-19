package com.charles.common;

/**
 * Created by lq on 17-3-16.
 */
public class Pair<X,Y> {
    private X first;
    private Y second;

    public Pair(){}
    public Pair(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    public X getFirst() {
        return first;
    }

    public void setFirst(X first) {
        this.first = first;
    }

    public Y getSecond() {
        return second;
    }

    public void setSecond(Y second) {
        this.second = second;
    }
}
