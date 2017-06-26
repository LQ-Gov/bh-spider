package com.charles.common.glob;

/**
 * Created by lq on 17-6-3.
 */
public class BracePattern extends AbstractPattern {

    public BracePattern(String pattern,int index){
        int end = pattern.indexOf('}');
        if(end>0) {
            pattern = pattern.substring(index, end);
            index=1;
            while (pattern.charAt(index)==',')index++;//过滤左边的逗号




            for(int i=index;i<pattern.length()-1;i++){
                char c = pattern.charAt(i);


            }
        }
    }
}
