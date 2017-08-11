package com.charles.spider.monitor.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by lq on 17-4-8.
 */
public class Program {

    private static Logger logger = LoggerFactory.getLogger(Program.class);

    private static final int PORT = 8080;
    private static final String CONTEXT = "/";
    private static final String DEFAULT_WEBAPP_PATH = "monitor/src/main/webapp";

    public static void main(String[] args) throws Exception {


        int port = Integer.getInteger("init.watch.port", PORT);


        String webapp = System.getProperty("init.watch.app.path", DEFAULT_WEBAPP_PATH);


        logger.info("the watch server started,listen port:{},webapp path:{}",port,webapp);



        Server server = new Server();


        //这是http的连接器
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);

        // 解决Windows下重复启动Jetty居然不报告端口冲突的问题.
        //connector.setReuseAddress(false);
        server.addConnector(connector);


        WebAppContext context = new WebAppContext(webapp, CONTEXT);
        context.setDescriptor(webapp + "/WEB-INF/web.xml");
        context.setResourceBase(webapp);

        server.setHandler(context);

        server.start();
        server.join();
    }
}
