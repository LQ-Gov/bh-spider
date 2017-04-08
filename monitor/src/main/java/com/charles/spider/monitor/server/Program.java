package com.charles.spider.monitor.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by lq on 17-4-8.
 */
public class Program {

    public static void main(String[] args) throws Exception {
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
        System.getProperties().setProperty("port","8080");


        int port =Integer.parseInt(System.getProperty("port"));

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();

        context.setContextPath("/");
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("src/main/webapp");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());

        context.setConfigurationDiscovered(true);
        context.setParentLoaderPriority(true);

//        context.setAttribute( "org.eclipse.jetty.containerInitializers", jspInitializers());
//        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
//        context.addBean(new ServletContainerInitializersStarter(context), true);


        server.setHandler(context);

        server.start();
        server.join();
    }
}
