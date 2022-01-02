package util;

import java.io.PrintStream;

public class Logger {

    private final PrintStream printStream;

    public Logger(PrintStream printStream) {
        this.printStream = printStream;
    }

    public enum LogLevel {
        INFORMATION("INFO"), ERROR("ERROR"), WARNING("WARNING");
        private final String message;

        LogLevel(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public void log(LogLevel logLevel, String message) {
        String timeString = String.format("[%1$tY-%1$tm-%1$td %1$tT]", System.currentTimeMillis());
        String logLevelString = String.format("[%s]", logLevel.getMessage());
        printStream.println(timeString + logLevelString + message);

    }
}
