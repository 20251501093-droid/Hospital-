import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;

/**
 * THREADING CONCEPT: Thread-safe Activity Log using ConcurrentLinkedDeque.
 *
 * Multiple threads (worker threads, EDT) can safely log activities simultaneously.
 * Listeners are notified on the EDT for safe Swing UI updates.
 *
 * Singleton pattern — one shared activity feed across the application.
 */
public class ActivityLog {

    private static final int MAX_ENTRIES = 100;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Thread-safe deque — newest entries at the front
    private final ConcurrentLinkedDeque<ActivityEntry> entries = new ConcurrentLinkedDeque<>();

    // Thread-safe listener list
    private final CopyOnWriteArrayList<ActivityListener> listeners = new CopyOnWriteArrayList<>();

    private static final ActivityLog INSTANCE = new ActivityLog();

    private ActivityLog() {
        log("SYSTEM", "Hospital Management System initialized");
    }

    public static ActivityLog getInstance() {
        return INSTANCE;
    }

    /** Represents a single activity entry */
    public static class ActivityEntry {
        public final String message;
        public final String category;
        public final LocalDateTime timestamp;

        public ActivityEntry(String category, String message) {
            this.category = category;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        /** Returns emoji icon for category */
        public String getIcon() {
            switch (category) {
                case "AUTH":    return "\uD83D\uDD12"; // 🔒
                case "PATIENT": return "\uD83C\uDFE5"; // 🏥
                case "BILLING": return "\uD83D\uDCB0"; // 💰
                case "RX":      return "\uD83D\uDC8A"; // 💊
                case "THREAD":  return "\u2699\uFE0F"; // ⚙️
                case "SYSTEM":  return "\uD83D\uDDA5\uFE0F"; // 🖥️
                default:        return "\u2022";       // •
            }
        }

        /** Formatted time string */
        public String getTimeString() {
            return timestamp.format(TIME_FMT);
        }

        @Override
        public String toString() {
            return "[" + getTimeString() + "] " + getIcon() + " " + message;
        }
    }

    /** Listener interface for real-time activity updates */
    public interface ActivityListener {
        void onNewActivity(ActivityEntry entry);
    }

    /**
     * Log a new activity. Thread-safe — can be called from any thread.
     * Listeners are notified on the EDT.
     */
    public void log(String category, String message) {
        ActivityEntry entry = new ActivityEntry(category, message);
        entries.addFirst(entry);

        // Prune oldest entries if over limit
        while (entries.size() > MAX_ENTRIES) {
            entries.removeLast();
        }

        // Notify listeners on EDT
        for (ActivityListener listener : listeners) {
            SwingUtilities.invokeLater(() -> listener.onNewActivity(entry));
        }
    }

    /** Returns the most recent N entries */
    public List<ActivityEntry> getRecent(int count) {
        List<ActivityEntry> recent = new ArrayList<>();
        int i = 0;
        for (ActivityEntry entry : entries) {
            if (i++ >= count) break;
            recent.add(entry);
        }
        return recent;
    }

    /** Register a listener for new activity events */
    public void addListener(ActivityListener listener) {
        listeners.add(listener);
    }

    /** Remove a listener */
    public void removeListener(ActivityListener listener) {
        listeners.remove(listener);
    }
}
