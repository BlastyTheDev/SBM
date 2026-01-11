package dev.blasty.sbm.client.macro;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Macro extends Thread {
    public static final BlockingQueue<Boolean> tickDelayQueue = new ArrayBlockingQueue<>(1);

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition unpaused = lock.newCondition();

    protected volatile boolean paused;
    protected volatile boolean wasPaused;

    protected final MinecraftClient mc = MinecraftClient.getInstance();
    protected final GameOptions opts = mc.options;

    public Macro() {
        setName("Macro thread");
    }

    protected final void pressKey(KeyBinding key) {
        mc.execute(() -> key.setPressed(true));
    }

    protected final void releaseKey(KeyBinding key) {
        mc.execute(() -> key.setPressed(false));
    }

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

    protected void runCommand(String command) {
        mc.execute(() -> {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.networkHandler.sendChatCommand(command);
            }
        });
    }

    public void pause() {
        lock.lock();
        try {
            paused = true;
        } finally {
            lock.unlock();
        }
    }

    public void unpause() {
        lock.lock();
        try {
            paused = false;
            wasPaused = true;
            unpaused.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isPaused() {
        return paused;
    }
}
