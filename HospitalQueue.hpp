#ifndef HOSPITAL_QUEUE_HPP
#define HOSPITAL_QUEUE_HPP

#include <queue>
#include <vector>
#include <mutex>
#include <condition_variable>
#include <chrono>
#include <stdexcept>

/**
 * ============================================================================
 * OOP & CONCURRENCY CONCEPTS:
 * - Generic Programming / Class Templates: HospitalQueue<T>
 * - Thread-Safety: Thread synchronization via std::mutex & std::condition_variable
 * - Producer-Consumer Pattern: Doctor threads produce, Pharmacist threads consume
 * - RAII: std::lock_guard and std::unique_lock handle automatic lock acquisition/release
 * ============================================================================
 */
template <typename T>
class HospitalQueue {
private:
    std::queue<T> internalQueue;
    mutable std::mutex queueMutex;
    std::condition_variable cv;

public:
    HospitalQueue() = default;

    HospitalQueue(const HospitalQueue&) = delete;
    HospitalQueue& operator=(const HospitalQueue&) = delete;

    // Thread-safe push (Producer method)
    void push(const T& item) {
        {
            std::lock_guard<std::mutex> lock(queueMutex);
            internalQueue.push(item);
        }
        cv.notify_one();
    }

    // Non-blocking pop: throws exception if empty
    T pop() {
        std::lock_guard<std::mutex> lock(queueMutex);
        if (internalQueue.empty()) {
            throw std::runtime_error("Attempted to pop from an empty HospitalQueue!");
        }
        T item = internalQueue.front();
        internalQueue.pop();
        return item;
    }

    // Non-blocking tryPop: returns false if empty
    bool tryPop(T& value) {
        std::lock_guard<std::mutex> lock(queueMutex);
        if (internalQueue.empty()) {
            return false;
        }
        value = internalQueue.front();
        internalQueue.pop();
        return true;
    }

    // Blocking wait-and-pop with timeout (Consumer method)
    bool waitAndPop(T& value, std::chrono::milliseconds timeout) {
        std::unique_lock<std::mutex> lock(queueMutex);
        if (!cv.wait_for(lock, timeout, [this] { return !internalQueue.empty(); })) {
            return false;
        }
        value = internalQueue.front();
        internalQueue.pop();
        return true;
    }

    bool empty() const {
        std::lock_guard<std::mutex> lock(queueMutex);
        return internalQueue.empty();
    }

    size_t size() const {
        std::lock_guard<std::mutex> lock(queueMutex);
        return internalQueue.size();
    }

    // Thread-safe snapshot for display without modifying the queue
    std::vector<T> snapshot() const {
        std::lock_guard<std::mutex> lock(queueMutex);
        std::queue<T> copy = internalQueue;
        std::vector<T> result;
        while (!copy.empty()) {
            result.push_back(copy.front());
            copy.pop();
        }
        return result;
    }
};

#endif // HOSPITAL_QUEUE_HPP
