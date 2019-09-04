package com.bh.spider.scheduler.watch.handler;

import com.bh.spider.scheduler.watch.point.Points;
import org.slf4j.Marker;

/**
 * @author liuqi19
 * @version RuleTextStreamMarkerHandler, 2019/8/27 6:01 下午 liuqi19
 **/
@Support("rule.text.stream")
public class RuleTextStreamMarkerHandler implements MarkerHandler {
    @Override
    public void handle(Marker marker,String text, Object[] args) {
        if (args.length == 0) return;

        Long ruleId = (Long) args[0];
        if(ruleId!=null){
            Points.of("rule.text.stream:"+ruleId).set(text);
        }
    }
}
