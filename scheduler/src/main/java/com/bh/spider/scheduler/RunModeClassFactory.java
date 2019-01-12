package com.bh.spider.scheduler;

/**
 * Created by lq on 17-6-27.
 */
public class RunModeClassFactory {
    public final static String STAND_ALONE = "stand-alone";
    public final static String CLUSTER_MASTER = "cluster-master";

    public static Class<?> get(String mode) {
        switch (mode) {
            case STAND_ALONE:
                return BasicScheduler.class;
//            case CLUSTER_MASTER:
//                return ClusterScheduler.class;
        }

        return null;
    }
}
