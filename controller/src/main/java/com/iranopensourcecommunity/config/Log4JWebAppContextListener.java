package com.iranopensourcecommunity.config;

import com.iranopensourcecommunity.log.Log4jBootstrap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Log4JWebAppContextListener implements ServletContextListener {

    private static final String LOG4J_PATH_ENV = "rootPath";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        System.setProperty(LOG4J_PATH_ENV, context.getRealPath("/"));
        String contextName = context.getServletContextName();
        Log4jBootstrap.loader(contextName);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
