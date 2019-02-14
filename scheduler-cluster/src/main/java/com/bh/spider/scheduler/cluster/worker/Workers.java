package com.bh.spider.scheduler.cluster.worker;

import com.bh.spider.scheduler.Session;
import com.bh.spider.scheduler.cluster.ClusterScheduler;
import com.bh.spider.scheduler.event.Command;

import java.util.*;

public class Workers implements Iterable<Worker> {
    private ClusterScheduler scheduler;
    private Map<Long, Session> sessions = new HashMap<>();

    public Workers(ClusterScheduler scheduler, Collection<Session> sessions) {
        sessions.forEach(this::add);
    }

    public Workers(ClusterScheduler scheduler){
        this.scheduler = scheduler;
    }

    public void add(Session session){
        sessions.put(session.id(),session);
    }

    public void remove(long id){

    }

    public void tell(long id, Command cmd){
        Session session = sessions.get(id);
        if(session!=null){
            session.tell(cmd);
        }
    }

    public void tellAll(Command cmd) {
        for (Session session : sessions.values()) {
            session.tell(cmd);
        }
    }


    public Workers random() {
        Random random = new Random();
        Collection<Session> sessionCollection = sessions.values();

        int randomValue = random.nextInt(sessionCollection.size());

        Iterator<Session> it = sessionCollection.iterator();
        Session session = null;
        for (int i = 0; i < randomValue; i++)
            session = it.next();


        return new Workers(this.scheduler, Collections.singleton(session));


    }

    @Override
    public Iterator<Worker> iterator() {
        return null;
    }
}
