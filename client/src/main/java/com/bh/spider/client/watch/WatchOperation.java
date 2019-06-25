package com.bh.spider.client.watch;

import com.bh.common.utils.CommandCode;
import com.bh.spider.client.converter.TypeConverter;
import com.bh.spider.client.sender.Sender;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author liuqi19
 * @version WatchOperation, 2019-06-21 18:07 liuqi19
 **/
public class WatchOperation {

    private Map<String, Set<Consumer>> watched = new ConcurrentHashMap<>();

    private final Sender sender;

    private final Set<String> points;



    public WatchOperation(Sender sender) {


        this.sender = sender;

        this.points = watchedPoints();
    }




    private Set<String> watchedPoints(){
        ParameterizedType returnType = ParameterizedTypeImpl.make(List.class, new Type[]{String.class}, null);
        List<String> list = sender.write(CommandCode.GET_WATCH_POINT_LIST, returnType);

        return new HashSet<>(list);

    }


    private boolean validate(String point) {

        point = point.toLowerCase();
        if (points.contains(point)) return true;

        int colonIndex = point.indexOf(":");
        if (colonIndex <= 0) return false;

        return points.contains(point.substring(0, colonIndex));
    }

    public synchronized  <T> void watch(String point,Consumer<T> consumer,Class<T> cls) throws Exception {

        if (validate(point)) {

            boolean initialized = watched.containsKey(point);


            final Set<Consumer> consumers = this.watched.computeIfAbsent(point, x -> ConcurrentHashMap.newKeySet());
            consumers.add(consumer);

            if (!initialized) {
                sender.stream(CommandCode.WATCH, (Consumer<T>) t -> {
                    List<Consumer> list = new LinkedList<>(consumers);

                    for (Consumer it : list)
                        it.accept(t);


                }, new TypeConverter<>(cls));
            }

            return;

        }

        throw new Exception("不存在的监控点");
    }


    public synchronized void unwatch(String point,Consumer consumer) {
        if (watched.containsKey(point)) {
            Set<Consumer> consumers = watched.get(point);

            if (consumers.remove(consumer)) {
                if (consumers.isEmpty()) {
                    unwatch(point);
                }
            }
        }
    }


    public synchronized void unwatch(String point){
        if(watched.containsKey(point)) {
            watched.remove(point);

            sender.write(CommandCode.UNWATCH, null, point);
        }
    }



}
