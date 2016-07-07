package com.pabula.fw.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Dekn
 */
public class StackTraceUtil {
    /**
     *	This method takes a exception as an input argument and returns the stacktrace as a string.
     */
public static String getStackTrace(Throwable exception)
{
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exception.printStackTrace(pw);

    return sw.toString();
}
}
