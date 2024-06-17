package model;

import java.time.Instant;

abstract class Clock {
    protected Instant time;

    public Clock() {
        this.time = Instant.now();
        Thread timeUpdater = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    this.time = this.time.plusSeconds(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        timeUpdater.start();
    }

    public abstract void displayTime();

    public Instant getTime() {
        return time;
    }

    public void adjustTime(long offsetSeconds) {
        this.time = this.time.plusSeconds(offsetSeconds);
    }
}