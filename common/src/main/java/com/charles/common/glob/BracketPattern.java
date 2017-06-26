package com.charles.common.glob;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lq on 17-6-3.
 */
public class BracketPattern extends AbstractPattern {
    private boolean flag = true;
    private Set<Character> content = new HashSet<>();
    private int length = 0;



    public BracketPattern(String pattern,int index) {

        int offset = 1;

        if (pattern.charAt(offset) == '!') {
            flag = true;
            offset++;
        }

        for (int i = index + offset; i < pattern.length() - 1; i++) {
            char c = pattern.charAt(i);

            if (c == '-') {
                if (i == index + offset || i == pattern.length() - 2) content.add('-');
                else {
                    combine(pattern.charAt(i - 1), pattern.charAt(i + 1));
                    i++;
                }
            } else if (c == ']') length = i - index;
            else
                content.add(c);
        }

        if (length == 0 && pattern.charAt(pattern.length() - 1) != ']') content.clear();
        else segment = pattern.substring(index, index + length());

    }

    private void combine(char left,char right) {
        char min = left > right ? right : left;
        char max = left >= right ? left : right;

        for (; min <= max; min++) content.add(min);
    }

    @Override
    public int length() {
        return length;
    }
}
