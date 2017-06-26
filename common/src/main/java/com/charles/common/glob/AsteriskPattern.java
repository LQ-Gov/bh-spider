package com.charles.common.glob;

/**
 * Created by lq on 17-6-3.
 */
public class AsteriskPattern extends AbstractPattern {

    public AsteriskPattern(String pattern,int index) {
        int end = index;

        while (pattern.charAt(end++) == '*') ;

        segment = pattern.substring(index, end);


    }

}
