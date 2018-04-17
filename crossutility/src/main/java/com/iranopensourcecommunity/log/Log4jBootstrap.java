package com.iranopensourcecommunity.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4jBootstrap {

    private static Logger log = Logger.getLogger(Log4jBootstrap.class);

    public static void loader(String contextName) {
        System.out.println(String.format("%n%n----------> CONSOLE LOG-COLOR TEST for [%s] <----------", contextName == null ? "NOT DEFINED" : contextName));
        log.setLevel(Level.ALL);
        log.trace("Trace SomeThing");
        log.debug("Debug SomeThing");
        log.info("INFO SomeThing");
        log.warn("WARN SomeThing");
        log.error("ERROR SomeThing");
        log.fatal("FATAL SomeThing");

        String DEFINED_LEVEL = log.getRootLogger().getLevel().toString();
        System.out.println(String.format("----------> CONSOLE LOG LEVEL [%S] ACTIVATED FOR THIS SYSTEM <----------%n%n", DEFINED_LEVEL));

    }

    public static void main(String[] args) {
        loader("TEST Application");
    }
}
