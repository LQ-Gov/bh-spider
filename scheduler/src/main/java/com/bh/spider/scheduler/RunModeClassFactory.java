package com.bh.spider.scheduler;

import com.bh.spider.scheduler.cluster.ClusterScheduler;

/**
 * Created by lq on 17-6-27.
 */
public class RunModeClassFactory {
    private final static String STAND_ALONE = "stand-alone";
    private final static String CLUSTER_MASTER = "cluster-master";

    public static Class<?> get(String mode) {
        switch (mode) {
            case STAND_ALONE:
                return BasicScheduler.class;
            case CLUSTER_MASTER:
                return ClusterScheduler.class;
        }

        return null;
    }
}
