package org.openrsc.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrsc.Config;
import org.openrsc.net.Server;
import org.openrsc.task.util.BlockingExecutorService;

/**
 * Processes all the scheduled tasks in one single scheduled
 * <code>ExecutorService</code>. This service is scheduled which means
 * <code>Event</code>s are also submitted to it.
 */
public class TaskEngine implements Runnable {

    /**
     * A queue of pending tasks.
     */
    private final BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();

    /**
     * The scheduled service.
     */
    private final ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1);

    /**
     * The task service, used by <code>ParallelTask</code>s.
     */
    private final BlockingExecutorService taskService;

    /**
     * Running flag.
     */
    private boolean running = false;

    /**
     * Thread instance.
     */
    private Thread thread;

    @SuppressWarnings("unused")
    public TaskEngine() {
        this.taskService = Config.TASK_ENGINE_THREAD_COUNT == 1
                ? new BlockingExecutorService(Executors.newSingleThreadExecutor())
                : new BlockingExecutorService(Executors.newFixedThreadPool(Config.TASK_ENGINE_THREAD_COUNT));
    }

    /**
     * Submits a new task which is processed on the scheduled thread as soon as
     * possible.
     *
     * @param task
     *            The task to submit.
     */
    public void pushTask(Task task) {
        tasks.offer(task);
    }

    /**
     * Starts the thread.
     */
    public void start() {
        if (running) {
            throw new IllegalStateException(getClass().getName() + " already running..");
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Stops the <code>Engine</code>'s thread.
     */
    public void stop() {
        if (!running) {
            throw new IllegalStateException(getClass().getName() + " already stopped..");
        }
        running = false;
        thread.interrupt();
    }

    @Override
    public void run() {
        try {
            while (running) {
                try {
                    final Task task = tasks.take();
                    submitLogic(new Runnable() {
                        @Override
                        public void run() {
                            task.execute(TaskEngine.this);
                        }
                    });
                } catch (InterruptedException e) {
                    continue;
                }
            }
        } finally {
            scheduledService.shutdown();
            taskService.shutdown();
        }
    }

    /**
     * Schedules a task to run in the scheduled service.
     *
     * @param runnable
     *            The runnable.
     * @param delay
     *            The delay.
     * @param unit
     *            The time unit.
     * @return The <code>ScheduledFuture</code> of the scheduled logic.
     */
    public ScheduledFuture<?> scheduleLogic(final Runnable runnable, long delay, TimeUnit unit) {
        return scheduledService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    handleError(t);
                }
            }
        }, delay, unit);
    }

    /**
     * Submits a task to run in the parallel task service.
     *
     * @param runnable
     *            The runnable.
     */
    public void submitTask(final Runnable runnable) {
        taskService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    handleError(t);
                }
            }
        });
    }

    /**
     * Submits a task to run in the scheduled service.
     *
     * @param runnable
     *            The runnable.
     */
    public void submitLogic(final Runnable runnable) {
        scheduledService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    handleError(t);
                }
            }
        });
    }

    /**
     * Waits for pending parallel tasks.
     *
     * @throws ExecutionException
     *             If an error occurred during a task.
     */
    public void waitForPendingParallelTasks() throws ExecutionException {
        taskService.waitForPendingTasks();
    }

    /**
     * Handles an exception in any of the pools.
     *
     * @param t
     *            The exception.
     */
    private void handleError(Throwable t) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error has occured in an executor service.", t);
        Server.getInstance().shutdown(true);
    }

}
