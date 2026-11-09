import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 * THREADING CONCEPT: Thread Pool using ExecutorService.
 *
 * Manages a pool of background worker threads for CPU/IO-intensive tasks
 * (RSA encryption, database queries) so the Swing UI thread stays responsive.
 *
 * All result callbacks are dispatched on the EDT via SwingUtilities.invokeLater.
 *
 * Singleton pattern — one shared thread pool for the entire application.
 */
public class HospitalTaskManager {

    private final ExecutorService executor;
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final AtomicInteger completedTasks = new AtomicInteger(0);

    private static final HospitalTaskManager INSTANCE = new HospitalTaskManager();

    private HospitalTaskManager() {
        // Custom ThreadFactory to name threads for debugging
        AtomicInteger threadCount = new AtomicInteger(0);
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "HMS-Worker-" + threadCount.incrementAndGet());
            t.setDaemon(true); // Don't prevent JVM shutdown
            return t;
        };
        this.executor = Executors.newFixedThreadPool(4, factory);
    }

    public static HospitalTaskManager getInstance() {
        return INSTANCE;
    }

    /**
     * Run a task in the background thread pool, delivering the result on the EDT.
     *
     * @param task      The background computation (runs on worker thread)
     * @param onSuccess Called on EDT with the result if task succeeds
     * @param onError   Called on EDT with the exception if task fails
     */
    public <T> void runInBackground(Callable<T> task, Consumer<T> onSuccess, Consumer<Exception> onError) {
        activeTasks.incrementAndGet();

        executor.submit(() -> {
            try {
                T result = task.call();
                SwingUtilities.invokeLater(() -> {
                    activeTasks.decrementAndGet();
                    completedTasks.incrementAndGet();
                    if (onSuccess != null) onSuccess.accept(result);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    activeTasks.decrementAndGet();
                    completedTasks.incrementAndGet();
                    if (onError != null) onError.accept(e);
                });
            }
        });
    }

    /**
     * Simpler overload for tasks that return no result.
     */
    public void runInBackground(Runnable task, Runnable onComplete) {
        activeTasks.incrementAndGet();

        executor.submit(() -> {
            try {
                task.run();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    activeTasks.decrementAndGet();
                    completedTasks.incrementAndGet();
                    if (onComplete != null) onComplete.run();
                });
            }
        });
    }

    /** Number of currently executing tasks */
    public int getActiveTaskCount() {
        return activeTasks.get();
    }

    /** Total number of completed tasks since startup */
    public int getCompletedTaskCount() {
        return completedTasks.get();
    }

    /** Graceful shutdown — waits for running tasks to finish */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
