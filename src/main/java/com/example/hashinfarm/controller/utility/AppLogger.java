package com.example.hashinfarm.controller.utility;

import java.time.LocalDateTime;

public class AppLogger {

  private static final String LOG_FORMAT =
      "[%s %tT] [%s] %s: %s"; // Format with timestamp, thread, level, and message
  private static Level minLogLevel = Level.INFO; // Minimum level to log (can be configured)

  private static void log(Level level, String message, Throwable throwable) {
    if (level.ordinal() >= minLogLevel.ordinal()) {
      String formattedMessage =
          String.format(
              LOG_FORMAT,
              Thread.currentThread().getName(),
              LocalDateTime.now(), // Pass LocalDateTime object directly
              level.name(),
              message,
              throwable != null ? throwable.toString() : "");
      switch (level) {
        case ERROR:
          System.err.println(formattedMessage);
          if (throwable != null) {
            throwable.printStackTrace(System.err);
          }
          break;
        case WARN:
        case INFO:
          System.out.println(formattedMessage);
          break;
        case DEBUG:
          // Implement custom logic for debug messages (e.g., separate file)
          System.out.println(formattedMessage); // Placeholder for now
          break;
      }
    }
  }

  public static void error(String message, Throwable throwable) {
    log(Level.ERROR, message, throwable);
  }

  public static void error(String message) {
    error(message, null);
  }

  public static void warn(String message) {
    log(Level.WARN, message, null);
  }

  public static void info(String message) {
    log(Level.INFO, message, null);
  }

  public static void debug(String message) {
    log(Level.DEBUG, message, null);
  }

  // Optional: Set minimum log level (useful for configuration)
  public static void setMinLogLevel(Level level) {
    minLogLevel = level;
  }

  public enum Level {
    ERROR,
    WARN,
    INFO,
    DEBUG
  }
}
