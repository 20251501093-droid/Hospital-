import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.SwingUtilities;

/**
 * THREADING CONCEPT: Producer-Consumer Pattern using BlockingQueue.
 *
 * Doctor (producer) adds prescriptions → Pharmacist (consumer) retrieves them.
 * Thread-safe by design: LinkedBlockingQueue handles all synchronization internally.
 *
 * Singleton pattern ensures a single shared queue across the entire application.
 */
public class PrescriptionQueue {

    // Thread-safe queue — blocks consumer if empty (take), never blocks producer (add)
    private final LinkedBlockingQueue<Prescription> queue = new LinkedBlockingQueue<>();

    // Thread-safe listener list — safe for concurrent iteration and modification
    private final CopyOnWriteArrayList<QueueListener> listeners = new CopyOnWriteArrayList<>();

    // Singleton instance
    private static final PrescriptionQueue INSTANCE = new PrescriptionQueue();

    private PrescriptionQueue() {}

    public static PrescriptionQueue getInstance() {
        return INSTANCE;
    }

    /** Callback interface for new prescription notifications */
    public interface QueueListener {
        void onPrescriptionAdded(Prescription rx);
    }

    /**
     * PRODUCER METHOD — called by Doctor to add a prescription.
     * Non-blocking. Notifies all listeners on the EDT (Event Dispatch Thread).
     */
    public void addPrescription(Prescription rx) {
        queue.add(rx);
        // Notify listeners on EDT for thread-safe Swing updates
        for (QueueListener listener : listeners) {
            SwingUtilities.invokeLater(() -> listener.onPrescriptionAdded(rx));
        }
    }

    /**
     * CONSUMER METHOD (blocking) — called by Pharmacist.
     * Blocks the calling thread until a prescription is available.
     */
    public Prescription takePrescription() throws InterruptedException {
        return queue.take();
    }

    /**
     * CONSUMER METHOD (non-blocking) — returns null if queue is empty.
     */
    public Prescription pollPrescription() {
        return queue.poll();
    }

    /** Returns a snapshot of all pending prescriptions (for display purposes) */
    public List<Prescription> peekAll() {
        return new ArrayList<>(queue);
    }

    /** Returns the number of pending prescriptions */
    public int getPendingCount() {
        return queue.size();
    }

    /** Register a listener for new prescription events */
    public void addQueueListener(QueueListener listener) {
        listeners.add(listener);
    }

    /** Remove a previously registered listener */
    public void removeQueueListener(QueueListener listener) {
        listeners.remove(listener);
    }
}
