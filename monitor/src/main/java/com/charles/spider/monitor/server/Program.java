package com.charles.spider.monitor.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by lq on 17-4-8.
 */
public class Program {

    public static final int PORT = 8080;
    public static final String CONTEXT = "/";

    private static final String DEFAULT_WEBAPP_PATH = "monitor/src/main/webapp";

    public static void main(String[] args) throws Exception {
//        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
//        System.getProperties().setProperty("port","8080");
//
//
//        int port =Integer.parseInt(System.getProperty("port"));

        Server server = new Server();

        //这是http的连接器
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(PORT);
        // 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
        //connector.setReuseAddress(false);
        server.setConnectors(new Connector[] { connector });


        WebAppContext context = new WebAppContext(DEFAULT_WEBAPP_PATH,CONTEXT);

        context.setDescriptor("monitor/src/main/webapp/WEB-INF/web.xml");
        context.setClassLoader(Thread.currentThread().getContextClassLoader());

//        context.setConfigurationDiscovered(true);
//        context.setParentLoaderPriority(true);

//        context.setAttribute( "org.eclipse.jetty.containerInitializers", jspInitializers());
//        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
//        context.addBean(new ServletContainerInitializersStarter(context), true);


        server.setHandler(context);

        server.start();
        server.join();
    }
}
