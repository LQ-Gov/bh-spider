package com.charles.common.glob;

/**
 * Created by lq on 17-6-3.
 */
public class BackStantPattern extends AbstractPattern {

    public BackStantPattern(String pattern,int index){
        if(index!=pattern.length()){

            segment = pattern.substring(index,index+1);

        }

    }
}
