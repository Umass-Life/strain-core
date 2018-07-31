package util;

import java.util.logging.Logger;

public class ColorLogger {
    private Logger log;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public ColorLogger(Logger log){
        this.log = log;
    }

    public String color(Object in, String color, Object... params){
        String text = "\n" + color + in.toString() + ANSI_RESET;
        if (params.length == 0){
            return text;
        }
        return String.format(text, params);
    }

    public void info(Object in, Object... params){
        log.info(color(in, ANSI_GREEN, params));
    }

    public void warning(Object in, Object... params){
        log.warning(color(in, ANSI_YELLOW, params));
    }

    public void severe(Object in, Object... params){
        log.severe(color(in, ANSI_RED, params));
    }
}
