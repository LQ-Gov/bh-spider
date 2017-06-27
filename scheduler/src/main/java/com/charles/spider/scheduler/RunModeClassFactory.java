package com.charles.spider.scheduler;

/**
 * Created by lq on 17-6-27.
 */
public class RunModeClassFactory {
    private final static String STAND_ALONE="stand-alone";

    public static Class<?> get(String mode){
        switch (mode){
            case STAND_ALONE:return BasicScheduler.class;
        }

        return null;
    }
}
