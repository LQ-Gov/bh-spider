package com.charles.spider.store.base;

import com.google.common.base.Preconditions;

import java.util.*;

/**
 * Created by lq on 17-6-18.
 */
public class Criteria {
    private static final Object NOT_SET = new Object();
    private String key;
    private List<Criteria> criteriaChain;
    private LinkedHashMap<String, Object> criteria = new LinkedHashMap();
    private Object isValue;

    public Criteria() {
        this.isValue = NOT_SET;
        this.criteriaChain = new ArrayList();
    }

    public Criteria(String key) {
        this.isValue = NOT_SET;
        this.criteriaChain = new ArrayList();
        this.criteriaChain.add(this);
        this.key = key;
    }

    protected Criteria(List<Criteria> criteriaChain, String key) {
        this.isValue = NOT_SET;
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
        this.key = key;
    }

    public static Criteria where(String key) {
        return new Criteria(key);
    }


    public static Criteria or(Criteria c1,Criteria c2){
        //return new Criteria()
        return null;
    }


    public Criteria and(String key) {
        return new Criteria(this.criteriaChain, key);
    }

    public Criteria is(Object o) {
        Preconditions.checkArgument(!this.isValue.equals(NOT_SET)
                , "Multiple 'is' values declared. You need to use 'and' with multiple criteria");

        Preconditions.checkArgument(lastOperatorWasNot(), "Invalid query: 'not' can't be used with 'is' - use 'ne' instead.");

        this.isValue = o;
        return this;
    }

    private boolean lastOperatorWasNot() {
        return !this.criteria.isEmpty() && "$not".equals(this.criteria.keySet().toArray()[this.criteria.size() - 1]);
    }

    public Criteria ne(Object o) {
        this.criteria.put("$ne", o);
        return this;
    }

    public Criteria lt(Object o) {
        this.criteria.put("$lt", o);
        return this;
    }

    public Criteria lte(Object o) {
        this.criteria.put("$lte", o);
        return this;
    }

    public Criteria gt(Object o) {
        this.criteria.put("$gt", o);
        return this;
    }

    public Criteria gte(Object o) {
        this.criteria.put("$gte", o);
        return this;
    }

    public Criteria in(Object... o) {
        this.criteria.put("$in", Arrays.asList(o));
        return this;
    }

    public Criteria in(Collection<?> c) {
        this.criteria.put("$in", c);
        return this;
    }

    public Criteria nin(Object... o) {
        return this.nin(Arrays.asList(o));
    }

    public Criteria nin(Collection<?> o) {
        this.criteria.put("$nin", o);
        return this;
    }

    public Criteria mod(Number value, Number remainder) {
        List<Object> l = new ArrayList();
        l.add(value);
        l.add(remainder);
        this.criteria.put("$mod", l);
        return this;
    }

    public Criteria all(Object... o) {
        return this.all((Collection)Arrays.asList(o));
    }

    public Criteria all(Collection<?> o) {
        this.criteria.put("$all", o);
        return this;
    }

    public Criteria size(int s) {
        this.criteria.put("$size", Integer.valueOf(s));
        return this;
    }

    public Criteria exists(boolean b) {
        this.criteria.put("$exists", Boolean.valueOf(b));
        return this;
    }

    public Criteria type(int t) {
        this.criteria.put("$type", Integer.valueOf(t));
        return this;
    }

    public Criteria not() {
        return this.not((Object)null);
    }

    private Criteria not(Object value) {
        this.criteria.put("$not", value);
        return this;
    }


    public Criteria like(String pattern){
        return this;
    }


    public String getKey() {
        return this.key;
    }




    public boolean equals(Object obj) {
//        if(this == obj) {
//            return true;
//        } else if(obj != null && this.getClass().equals(obj.getClass())) {
//            Criteria that = (Criteria)obj;
//            if(this.criteriaChain.size() != that.criteriaChain.size()) {
//                return false;
//            } else {
//                for(int i = 0; i < this.criteriaChain.size(); ++i) {
//                    Criteria left = (Criteria)this.criteriaChain.get(i);
//                    Criteria right = (Criteria)that.criteriaChain.get(i);
//                    if(!this.simpleCriteriaEquals(left, right)) {
//                        return false;
//                    }
//                }
//
//                return true;
//            }
//        } else {
//            return false;
//        }
        return true;
    }


    private boolean isEqual(Object left, Object right) {
        //return left == null?right == null:(left instanceof Pattern?(right instanceof Pattern?((Pattern)left).pattern().equals(((Pattern)right).pattern()):false):ObjectUtils.nullSafeEquals(left, right));
        return true;
    }

    public int hashCode() {
//        int result = 17;
//        int result = result + ObjectUtils.nullSafeHashCode(this.key);
//        result += this.criteria.hashCode();
//        result += ObjectUtils.nullSafeHashCode(this.isValue);
//        return result;
        return 0;
    }

}
