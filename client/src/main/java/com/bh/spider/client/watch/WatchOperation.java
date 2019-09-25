package com.bh.spider.client.watch;

import com.bh.common.utils.CommandCode;
import com.bh.common.watch.WatchEvent;
import com.bh.spider.client.ClientConnection;
import com.bh.spider.client.Communicator;
import com.bh.spider.client.converter.DefaultConverter;
import com.bh.spider.client.converter.TypeConverter;
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

    private final Communicator communicator;

    private final Set<String> points;


    public WatchOperation(Communicator communicator) {


        this.communicator = communicator;

        this.points = watchedPoints();
    }


    private Set<String> watchedPoints() {
        ParameterizedType returnType = ParameterizedTypeImpl.make(List.class, new Type[]{String.class}, null);
        List<String> list = communicator.write(CommandCode.GET_WATCH_POINT_LIST, returnType);

        if (list == null) list = Collections.emptyList();

        return new HashSet<>(list);

    }


    private boolean validate(String point) {

        point = point.toLowerCase();
        if (points.contains(point)) return true;

        int colonIndex = point.indexOf(":");
        if (colonIndex <= 0) return false;

        return points.contains(point.substring(0, colonIndex));
    }


    private boolean fastidious(String point) {
        if (point.startsWith("rule.text.stream")) return true;
        return false;
    }

    public synchronized <T> void watch(String point, Consumer<T> consumer, Class<T> cls) throws Exception {

        if (validate(point)) {

            boolean initialized = watched.containsKey(point);


            final Set<Consumer> consumers = this.watched.computeIfAbsent(point, x -> ConcurrentHashMap.newKeySet());
            consumers.add(consumer);

            if (!initialized) {

                ClientConnection connection = null;

                /**
                 * 对于需要指定机器的节点
                 */
                if (fastidious(point)) {
                    Map<ClientConnection, Boolean> map = communicator.writeAll(CommandCode.CHECK_SUPPORT_WATCH_POINT, Boolean.class, point);

                    for (Map.Entry<ClientConnection, Boolean> entry : map.entrySet()) {
                        if (entry.getValue()) {
                            connection = entry.getKey();
                            break;
                        }
                    }

                    if (connection == null) throw new Exception("未查询到支持该点的server");

                }


                communicator.stream(connection, CommandCode.WATCH, bytes -> {
                    try {
                        WatchEvent event = new TypeConverter<WatchEvent>(WatchEvent.class).convert(bytes);

                        Object value = cls == WatchEvent.class ? event : event.value();

                        List<Consumer> list = new LinkedList<>(consumers);

                        for (Consumer it : list)
                            it.accept(value);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }, new DefaultConverter(), point);
            }

            return;

        }

        throw new Exception("不存在的监控点" + point);
    }


    public synchronized void unwatch(String point, Consumer consumer) {
        if (watched.containsKey(point)) {
            Set<Consumer> consumers = watched.get(point);

            if (consumers.remove(consumer)) {
                if (consumers.isEmpty()) {
                    unwatch(point);
                }
            }
        }
    }


    public synchronized void unwatch(String point) {
        if (watched.containsKey(point)) {
            watched.remove(point);

            communicator.write(CommandCode.UNWATCH, null, point);
        }
    }


}
