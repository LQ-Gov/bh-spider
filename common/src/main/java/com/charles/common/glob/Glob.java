package com.charles.common.glob;

/**
 * Created by lq on 17-6-3.
 */
public class Glob extends AbstractPattern {

    private String segment = null;

    private AbstractPattern base = null;

    public Glob(String pattern){
        this.segment = pattern;
        base = analysis(pattern,0);

    }


    AbstractPattern analysis(String input, int index) {
        if(input==null ||input.isEmpty()|| index==input.length()-1) return null;

        char c = input.charAt(index);

        AbstractPattern pattern;
        switch (c) {
            case '\\':
                return new BackStantPattern(input,index);
            case '*':
                return new AsteriskPattern(input,index);
            case '?':
                return new QmarkPattern(input,index);
            case '[':
                return new BracketPattern(input,index);
            case '{':
                //return new BraceToken();
                return null;
//            case '-':
//                return new ShortLineToken();
//            case '!':
//                return new ExclMarkToken();
            default:
                pattern = new GeneralPattern(input, index);
        }

        AbstractPattern next = analysis(input, index + pattern.length());
        if(next!=null) {
            pattern.next = next;
            next.parent = pattern;
        }

        return pattern;

    }


    @Override
    public String segment() {
        return null;
    }

    @Override
    public AbstractPattern next() {
        return null;
    }

    @Override
    public AbstractPattern parent() {
        return null;
    }
}
