package com.bh.spider.scheduler.component;

import com.bh.common.utils.Json;
import com.bh.spider.common.component.Component;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Metadata {
    private final static int FIXED_ROW_SIZE = 500;

    private Map<String, Position<Component>> components = new LinkedHashMap<>();

    private FileChannel channel;


    public Metadata(Path path) throws IOException {
        this.channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        long len = this.channel.size();
        if (Files.exists(path) && len > 0) {
            long pos = 0;
            while (len - pos >= FIXED_ROW_SIZE) {
                Position<Component> position = read0(pos, null);
                if (position != null)
                    components.put(position.data().getName(), position);
                pos += FIXED_ROW_SIZE;
            }

            if (len - pos > 0) this.channel.truncate(this.channel.position());
        }
    }


    public List<Component> components() {
        return components.values().stream().map(Position::data).collect(Collectors.toList());
    }

    private long write0(Component component, long pos) throws IOException {
        Preconditions.checkArgument(pos <= channel.size(), "error metadata position");
        Preconditions.checkNotNull(component, "component can't null");
        Preconditions.checkArgument(StringUtils.isNotBlank(component.getName()), "component.name can't null");
        Preconditions.checkArgument(component.getName().length() <= 100, "component.name max length is 100");
        Preconditions.checkArgument(component.getDescription() == null || component.getDescription().length() < 200, "component.description max length is 200");

        long cur = channel.position();

        if (cur != pos) channel.position(pos);

        byte[] data = Json.get().writeValueAsBytes(component);
        /*1:有效/无效 2:过期 3:data size 4:data 5:padding */
        ByteBuffer buffer = ByteBuffer.allocate(FIXED_ROW_SIZE)
                .put((byte) 1).put((byte) (component.isExpired() ? 1 : 0))
                .putInt(data.length).put(data)
                .put(new byte[FIXED_ROW_SIZE - 1 - 1 - 4 - data.length]);

        buffer.flip();

        channel.write(buffer);

        return channel.position();
    }

    private Position<Component> read0(long pos, Position<Component> position) throws IOException {
        if (channel.size() - pos < FIXED_ROW_SIZE) return null;

        channel.position(pos);

        ByteBuffer buffer = ByteBuffer.allocate(FIXED_ROW_SIZE);

        channel.read(buffer);

        buffer.flip();

        if (buffer.get() == 0) return null;

        boolean expired = buffer.get() == 1;

        byte[] data = new byte[buffer.getInt()];



        Component component = Json.get().readValue(data, Component.class);
        component.setExpired(expired);
        return position == null ? new Position<>(component, pos) : position.cover(component, pos, false);
    }


    public void write(Component component) throws IOException {

        long pos;
        Position<Component> position = components.get(component.getName());
        if (position != null) {
            pos = write0(component, position.pos());
        } else {
            position = new Position<>();
            pos = write0(component, channel.size());
        }

        components.put(component.getName(), position.cover(component, pos, true));
    }

    public Component get(String name) {
        Position<Component> position = components.get(name);
        return position == null ? null : position.data();
    }

    public boolean delete(String name) throws IOException {
        Position<Component> position = components.get(name);
        if (position != null) {
            long old = channel.position();

            channel.position(position.pos);
            channel.write(ByteBuffer.wrap(new byte[]{0}));

            channel.position(old);

            components.remove(name);

            return true;

        }
        return false;
    }

    public void reset(List<Component> components, boolean all) {
        this.components.clear();
        Map<String, Component> map = components.stream().collect(Collectors.toMap(Component::getName, x -> x));

        List<Position<Component>> contains = this.components.values().stream().filter(x -> map.containsKey(x.data.getName())).collect(Collectors.toList());


    }

    /**
     * 对指定的name 进行wait
     *
     * @param name
     */
    public boolean waitFor(String name) throws InterruptedException {
        final Position<Component> position = components.get(name);
        if (position == null) return false;

        synchronized (position) {
            position.wait();
        }

        return true;
    }


    public boolean waitFor(String name, long timeout) throws InterruptedException {
        final Position<Component> position = components.get(name);
        if (position == null) return false;

        synchronized (position) {
            position.wait(timeout);
        }

        return true;
    }


    private class Position<T> {
        private T data;
        private long pos;


        public Position() {
        }


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


        public Position<T> cover(T data, long pos, boolean notify) {
            this.data = data;
            this.pos = pos;

            if (notify) {
                synchronized (this) {
                    this.notifyAll();
                }
            }


            return this;
        }


    }
}
