package com.bh.spider.scheduler.watch;


import com.bh.common.WatchPointKeys;
import com.bh.spider.scheduler.watch.handler.MarkerHandler;
import com.bh.spider.scheduler.watch.handler.Support;
import com.bh.spider.scheduler.watch.point.Points;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.Message;
import org.slf4j.MarkerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Plugin(name = "Watch", category = "Core", elementType = "appender", printObject = true)
public class WatchAppender extends AbstractAppender {

    private Map<Marker, MarkerHandler> markers = new HashMap<>();

    protected WatchAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties, HandlerElement[] handlerElements) {
        super(name, filter, layout, ignoreExceptions, properties);

        if (handlerElements != null) {
            for (HandlerElement element : handlerElements) {
                addHandler(element);
            }
        }
    }


    private void addHandler(HandlerElement element) {

        try {
            Class<?> handlerClass = Class.forName(element.getClassName());

            MarkerHandler handler = (MarkerHandler) handlerClass.newInstance();

            Support support = handler.getClass().getAnnotation(Support.class);
            if (support != null) {

                for (String name : support.value()) {
                    Marker marker = MarkerManager.getMarker(name);
                    markers.put(marker, handler);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void append(LogEvent event) {

        Marker marker = event.getMarker();

        org.slf4j.Marker slf4jMarker = marker == null ? null : MarkerFactory.getMarker(marker.getName());


        if (slf4jMarker != Markers.LOG_STREAM) {
            Points.<String>of(WatchPointKeys.LOG_STREAM).set(new String(getLayout().toByteArray(event)));

        }


        MarkerHandler handler;
        if (marker == null || (handler = markers.get(marker)) == null) return;

        Message message = event.getMessage();

        handler.handle(slf4jMarker, message.getParameters());

    }

    @PluginFactory
    public static WatchAppender createAppender(@PluginAttribute("name") String name,
                                               @PluginElement("Filter") final Filter filter,
                                               @PluginElement("Layout") Layout<? extends Serializable> layout,
                                               @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                               @PluginElement("handler") HandlerElement[] handlerElements
    ) {


        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new WatchAppender(name, filter, layout, ignoreExceptions, null, handlerElements);

    }


    @Plugin(name = "Handler", category = "Core", printObject = true)
    public static class HandlerElement {
        private String className;


        public HandlerElement(String className) {
            this.className = className;
        }


        @PluginFactory
        public static HandlerElement createHandlerElement(@PluginAttribute("class") String className) {

            return new HandlerElement(className);

        }


        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}
