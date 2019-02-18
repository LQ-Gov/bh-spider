package com.bh.spider.scheduler;

/**
 * Created by lq on 17-6-27.
 */
public class RunModeClassFactory {
    public final static String STAND_ALONE = "stand-alone";
    public final static String CLUSTER_MASTER = "cluster-master";
    private final static String CLUSTER_WORKER="cluster-worker";

    public static Class<?> get(String mode) throws ClassNotFoundException {
        switch (mode) {
            case STAND_ALONE:
                return BasicScheduler.class;
            case CLUSTER_MASTER:
                return Class.forName("com.bh.spider.scheduler.cluster.ClusterScheduler");

            case CLUSTER_WORKER:
                return Class.forName("com.bh.spider.scheduler.cluster.worker.WorkerScheduler");
        }

        return null;
    }
}
