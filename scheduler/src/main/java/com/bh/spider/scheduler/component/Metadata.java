package com.bh.spider.scheduler.component;

import com.bh.common.utils.Json;
import com.bh.spider.common.component.Component;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Metadata {
    private final static int FIXED_ROW_SIZE = 500;

    private Map<String, Position<Component>> components = new LinkedHashMap<>();

    private RandomAccessFile accessor;

    private long lastPositionIndex;


    public Metadata(Path path) throws IOException {
        this.accessor = new RandomAccessFile(path.toFile(), "rws");
        this.lastPositionIndex =0;

        long len = this.accessor.length();
        if (Files.exists(path) && len > 0) {
            long pos = 0;
            while(len-pos>=FIXED_ROW_SIZE) {
                Position<Component> position = read0(pos,null);
                if (position != null)
                    components.put(position.data().getName(), position);
                pos += FIXED_ROW_SIZE;
            }

            lastPositionIndex = pos;

        }
    }


    public List<Component> components() {
        return components.values().stream().map(Position::data).collect(Collectors.toList());
    }

    private long write0(Component component,long pos) throws IOException {
        Preconditions.checkArgument(pos <= accessor.length(), "error metadata position");
        Preconditions.checkNotNull(component, "component can't null");
        Preconditions.checkArgument(StringUtils.isNotBlank(component.getName()), "component.name can't null");
        Preconditions.checkArgument(component.getName().length() <= 100, "component.name max length is 100");
        Preconditions.checkArgument(component.getDescription() == null || component.getDescription().length() < 200, "component.description max length is 200");

        if (pos >= 0 && pos != accessor.getFilePointer())
            accessor.seek(pos);

        accessor.writeBoolean(true);//先设置此行无效
        accessor.writeBoolean(component.isExpired());//设置没有过期
        byte[] data = Json.get().writeValueAsBytes(component);
        accessor.writeInt(data.length);//设置组件数据长度
        accessor.write(data);
        byte[] surplus = new byte[FIXED_ROW_SIZE - 1 - 1 - data.length - 4];//1:valid,1:expired,4:data.length
        accessor.write(surplus);


        if (pos + FIXED_ROW_SIZE >= accessor.length()) lastPositionIndex = pos + FIXED_ROW_SIZE;

        return pos;
    }

    private Position<Component> read0(long pos,Position<Component> position) throws IOException {
        if (accessor.length() - pos < FIXED_ROW_SIZE) return null;
        accessor.seek(pos);

        if (!accessor.readBoolean())
            return null;
        boolean expired = accessor.readBoolean();

        int size = accessor.readInt();

        byte[] data = new byte[size];
        accessor.read(data);


        Component component = Json.get().readValue(data, Component.class);
        component.setExpired(expired);
        return position == null ? new Position<>(component, pos) : position.cover(component, pos,false);
    }


    public void write(Component component) throws IOException {

        long pos;
        Position<Component> position = components.get(component.getName());
        if (position != null) {
            pos = write0(component, position.pos());
        } else {
            position = new Position<>();
            pos = write0(component, lastPositionIndex);
        }

        components.put(component.getName(), position.cover(component, pos,true));
    }

    public Component get(String name) {
        Position<Component> position = components.get(name);
        return position == null ? null : position.data();
    }

    public boolean delete(String name) throws IOException {
        Position<Component> position = components.get(name);
        if (position != null) {
            long end = accessor.getFilePointer();
            accessor.seek(position.pos());

            accessor.writeBoolean(false);
            accessor.seek(end);

            components.remove(name);

            return true;

        }
        return false;
    }

    /**
     * 对指定的name 进行wait
     * @param name
     */
    public boolean waitFor(String name) throws InterruptedException {
        final Position<Component> position = components.get(name);
        if(position==null) return false;

        synchronized (position){
            position.wait();
        }

        return true;
    }


    public boolean waitFor(String name,long timeout) throws InterruptedException {
        final Position<Component> position = components.get(name);
        if(position==null) return false;

        synchronized (position){
            position.wait(timeout);
        }

        return true;
    }


    private class Position<T> {
        private T data;
        private long pos;


        public Position(){}


        public Position(T data, long pos) {
            this.data = data;
            this.pos = pos;
        }

        public T data() {
            return data;
        }

        public long pos() {
            return pos;
        }


        public Position<T> cover(T data,long pos,boolean notify) {
            this.data = data;
            this.pos = pos;

            if(notify) {
                synchronized (this) {
                    this.notifyAll();
                }
            }


            return this;
        }



    }
}
