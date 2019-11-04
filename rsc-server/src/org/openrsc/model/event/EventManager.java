package org.openrsc.model.event;

import java.util.concurrent.TimeUnit;

import org.openrsc.task.TaskEngine;

/**
 */
public class EventManager {

    private final TaskEngine engine;

    public EventManager(TaskEngine engine) {
        this.engine = engine;
    }

    /**
     * Submits a new event.
     *
     * @param event
     *            The event to submit.
     */
    public void submit(Event event) {
        submit(event, event.getDelay());
    }

    /**
     * Schedules an event to run after the specified delay.
     *
     * @param event
     *            The event.
     * @param delay
     *            The delay.
     */
    private void submit(Event event, long delay) {
        engine.scheduleLogic(new Runnable() {
            @Override
            public void run() {
                if (!event.isRunning()) {
                    return;
                }
                long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
                event.execute(engine);
                long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) - start;
                long remaining = event.getDelay() - elapsed;
                submit(event, remaining < 0 ? 0 : remaining);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

}