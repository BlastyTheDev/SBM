package dev.blasty.sbm.client.macro;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Macro implements Runnable {
    public static final BlockingQueue<Boolean> tickDelayQueue = new ArrayBlockingQueue<>(1);

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition unpaused = lock.newCondition();
    private volatile boolean paused;

    protected final void sleep(int ticks) {
        ticks += tickDelayQueue.size();
        for (int i = 0; i < ticks; i++) {
            try {
                tickDelayQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected final void checkPaused() {
        lock.lock();
        try {
            while (paused) {
                try {
                    unpaused.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public final void pause() {
        lock.lock();
        try {
            paused = true;
        } finally {
            lock.unlock();
        }
    }

    public final void resume() {
        lock.lock();
        try {
            paused = false;
            unpaused.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isPaused() {
        return paused;
    }
}
