package com.bh.spider.scheduler.cluster;

import com.bh.common.utils.CommandCode;
import com.bh.spider.scheduler.BasicSchedulerWatchAssistant;
import com.bh.spider.scheduler.cluster.worker.Worker;
import com.bh.spider.scheduler.context.ClientContext;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.watch.point.Point;
import com.bh.spider.scheduler.watch.point.Points;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterSchedulerWatchAssistant extends BasicSchedulerWatchAssistant {

    private Map<Long, Integer> watched = new HashMap<>();

    private ClusterScheduler scheduler;

    public ClusterSchedulerWatchAssistant(ClusterScheduler scheduler) {
        this.scheduler = scheduler;
    }


    @CommandHandler(autoComplete = false)
    @Override
    public void WATCH_HANDLER(ClientContext ctx, String key) throws Exception {
        super.WATCH_HANDLER(ctx, key);

        //对子节点的日志流进行单独处理
        Point point = Points.of(key);
        if (point != null && point.createBy("log.stream")) {

            String extendKey = point.extendKey();

            if (extendKey != null) {

                List<Worker> workers = scheduler.workers().search(extendKey);
                if (CollectionUtils.isEmpty(workers)) return;


                for (Worker worker : workers) {
                    if (!watched.containsKey(worker.id())) {
                        worker.write(new Command(new LocalContext(scheduler), CommandCode.WATCH.name()));
                        watched.put(worker.id(), 1);
                    } else
                        watched.compute(worker.id(), (k, v) -> (++v));


                    ctx.whenComplete(x -> {
                        int count = watched.compute(worker.id(), (k, v) -> (--v));
                        if (count <= 0) {
                            worker.write(new Command(new LocalContext(scheduler), CommandCode.UNWATCH.name()));
                            watched.remove(worker.id());
                        }
                    });


                }


            }
        }
    }

    @CommandHandler
    @Override
    public void UNWATCH_HANDLER(ClientContext ctx, String key) throws JsonProcessingException {
        super.UNWATCH_HANDLER(ctx, key);


        //对子节点的日志流进行单独处理
        Point point = Points.of(key);

        if (point != null && point.createBy("log.stream")) {
            String extendKey = point.extendKey();

            if (extendKey != null) {

                List<Worker> workers = scheduler.workers().search(extendKey);

                for (Worker worker : workers) {

                    Integer count = watched.computeIfPresent(worker.id(), (k, v) -> v--);
                    if (count != null && count <= 0) {
                        worker.write(new Command(new LocalContext(scheduler), CommandCode.UNWATCH.name()));
                        watched.remove(worker.id());
                    }

                }
            }
        }
    }


    private void unwatch(Worker worker) {


    }


}
