package com.iranopensourcecommunity.log.appender;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;


public class ANSIConsoleAppender extends ConsoleAppender {

    private static final int NORMAL = 0;
    private static final int BRIGHT_BOLD = 1;

    private static final int FOREGROUND_BLACK = 30;
    private static final int FOREGROUND_RED = 31;
    private static final int FOREGROUND_GREEN = 32;
    private static final int FOREGROUND_YELLOW = 33;
    private static final int FOREGROUND_BLUE = 34;
    private static final int FOREGROUND_MAGENTA = 35;
    private static final int FOREGROUND_CYAN = 36;
    private static final int FOREGROUND_WHITE = 37;

    private static final int BACKGROUND_BLACK = 40;
    private static final int BACKGROUND_RED = 41;
    private static final int BACKGROUND_GREEN = 42;
    private static final int BACKGROUND_YELLOW = 43;
    private static final int BACKGROUND_BLUE = 44;
    private static final int BACKGROUND_MAGENTA = 45;
    private static final int BACKGROUND_CYAN = 46;
    private static final int BACKGROUND_WHITE = 47;

    private static final String PATTERN_FG_DENSITY_BG_PATTERN = "\u001b[%d;%d;%dm";
    private static final String PATTERN_FG_DENSITY_PATTERN = "\u001b[%d;%dm";
    private static final String END_COLOUR = "\u001b[m";

    //https://en.wikipedia.org/wiki/ANSI_escape_code#Colors

    private static final String FATAL_COLOUR = String.format(PATTERN_FG_DENSITY_BG_PATTERN, 31, BRIGHT_BOLD, 107);
    private static final String ERROR_COLOUR = FATAL_COLOUR;
    private static final String WARN_COLOUR = String.format(PATTERN_FG_DENSITY_BG_PATTERN, FOREGROUND_YELLOW, BRIGHT_BOLD, 107);
    private static final String INFO_COLOUR = String.format(PATTERN_FG_DENSITY_BG_PATTERN, FOREGROUND_BLUE, NORMAL, 107);
    private static final String DEBUG_COLOUR = String.format(PATTERN_FG_DENSITY_PATTERN, FOREGROUND_BLACK, NORMAL);
    private static final String TRACE_COLOUR = String.format(PATTERN_FG_DENSITY_PATTERN, FOREGROUND_WHITE, NORMAL);

    /**
     * Wraps the ANSI control characters around the
     * output from the super-class Appender.
     */
    protected void subAppend(LoggingEvent event) {
        this.qw.write(getColour(event.getLevel()));
        super.subAppend(event);
        this.qw.write(END_COLOUR);

        if (this.immediateFlush) {
            this.qw.flush();
        }
    }

    /**
     * Get the appropriate control characters to change
     * the colour for the specified logging level.
     */
    private String getColour(Level level) {
        switch (level.toInt()) {
            case Priority.FATAL_INT:
                return FATAL_COLOUR;
            case Priority.ERROR_INT:
                return ERROR_COLOUR;
            case Priority.WARN_INT:
                return WARN_COLOUR;
            case Priority.INFO_INT:
                return INFO_COLOUR;
            case Priority.DEBUG_INT:
                return DEBUG_COLOUR;
            default:
                return TRACE_COLOUR;
        }
    }
}