package com.comphenix.protocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.LogEvent;

import java.util.ArrayList;
import java.util.List;

public final class CommandCapture {

    public static List<String> executeAndCapture(String command) {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        CaptureAppender appender = new CaptureAppender();
        appender.start();
        rootLogger.addAppender(appender);

        try {
            org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), command);
        } catch (Exception e) {
            appender.append("Exception: " + e.getMessage());
        } finally {
            appender.stop();
            rootLogger.removeAppender(appender);
        }
        return appender.getLines();
    }

    /* ---------------- Appender sementara ---------------- */
    private static class CaptureAppender extends AbstractAppender {
        private final List<String> buffer = new ArrayList<>();

        CaptureAppender() {
            super("Armor3Capture", null, null, false, null);
        }

        @Override
        public void append(LogEvent event) {
            String line = event.getMessage().getFormattedMessage();
            if (line != null && !line.isEmpty()) buffer.add(line);
        }

        void append(String line) { buffer.add(line); }
        List<String> getLines() { return new ArrayList<>(buffer); }
    }
}
