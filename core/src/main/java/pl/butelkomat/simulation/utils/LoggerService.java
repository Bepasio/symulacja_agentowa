package pl.butelkomat.simulation.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * centralka logowania dla symulacji.
 * tu macie przechowanie logów i przerzut do GUI
 */
public class LoggerService {
    private static final LoggerService instance = new LoggerService();
    private static final int MAX_LOGS = 500;
    private final ArrayList<String> logs = new ArrayList<>();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LoggerService() {}

    public static LoggerService getInstance() {
        return instance;
    }

    /**
     * loguje wiadomość z czasem
     */
    public void log(String message) {
        String timeStamp = LocalTime.now().format(timeFormatter);
        String logEntry = "[" + timeStamp + "] " + message;

        logs.add(logEntry);

        // limit logów, żeby nie zapchać pamięci
        if (logs.size() > MAX_LOGS) {
            logs.remove(0);
        }

        // print w konsoli
        System.out.println(logEntry);
    }

    /**
     * log z errorem
     */
    public void logError(String message) {
        log("[BŁĄD] " + message);
    }

    /**
     * return wszystkich logów
     */
    public ArrayList<String> getLogs() {
        return new ArrayList<>(logs);
    }

    /**
     * zwraca ostatnie N logów
     */
    public ArrayList<String> getLastLogs(int count) {
        ArrayList<String> result = new ArrayList<>();
        int startIndex = Math.max(0, logs.size() - count);
        for (int i = startIndex; i < logs.size(); i++) {
            result.add(logs.get(i));
        }
        return result;
    }

    /**
     * czyszci wszystkie logi
     */
    public void clearLogs() {
        logs.clear();
    }

    /**
     * zwraca ostatni log jako jedną linię
     */
    public String getLastLog() {
        return logs.isEmpty() ? "" : logs.get(logs.size() - 1);
    }
}

