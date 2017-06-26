package com.charles.common.glob;

/**
 * Created by lq on 17-6-3.
 */
public class GeneralPattern extends AbstractPattern {

    public GeneralPattern(String pattern,int index){
        StringBuilder builder = new StringBuilder();

        for (int i = index; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            if (!isSpecial(c)) builder.append(c);
            else break;
        }
        segment = builder.toString();

    }
}
