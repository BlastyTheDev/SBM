package dev.blasty.sbm.client.macro;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class Macro implements Runnable {
    public static final BlockingQueue<Boolean> tickDelayQueue = new ArrayBlockingQueue<>(1);

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
}
