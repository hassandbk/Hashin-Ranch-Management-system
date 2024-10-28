package com.example.hashinfarm.utils.logging;

import java.time.LocalDateTime;

public class AppLogger {

  private static final String LOG_FORMAT =
          "[%s %tT] [%s] %s - %s"; // Format with thread name, timestamp, level, and message
  private static Level minLogLevel = Level.INFO; // Configurable minimum logging level

  private static void log(Level level, String message, Throwable throwable) {
    // Only log messages equal to or higher than the minimum log level
    if (level.ordinal() >= minLogLevel.ordinal()) {
      String formattedMessage = String.format(
              LOG_FORMAT,
              Thread.currentThread().getName(),
              LocalDateTime.now(),
              level.name(),
              message,
              throwable != null ? "Exception: " + throwable : ""); // Add "Exception" for clarity

      // Log to console based on level
      if (level == Level.ERROR) {
        System.err.println(formattedMessage);
        if (throwable != null) {
          throwable.printStackTrace(System.err); // Print stack trace to standard error
        }
      } else {
        System.out.println(formattedMessage); // Log warnings, info, and debug to standard output
      }
    }
  }

  // Public methods for logging at different levels
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

  // Optional: Allows setting the minimum log level dynamically
  public static void setMinLogLevel(Level level) {
    minLogLevel = level;
  }

  // Enum to represent logging levels
  public enum Level {
    ERROR,
    WARN,
    INFO,
    DEBUG
  }
}
