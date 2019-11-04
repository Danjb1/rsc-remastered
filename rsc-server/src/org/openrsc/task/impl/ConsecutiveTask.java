package org.openrsc.task.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openrsc.task.Task;
import org.openrsc.task.TaskEngine;

/**
 * A task which executes a group of tasks in a guaranteed sequence.
 * 
 * @author Graham Edgecombe
 */
public class ConsecutiveTask implements Task {

    /**
     * The tasks.
     */
    private Collection<Task> tasks;

    /**
     * Creates the consecutive task.
     * 
     * @param tasks
     *            The child tasks to execute.
     */
    public ConsecutiveTask(Task... tasks) {
        List<Task> taskList = new ArrayList<Task>();
        for (Task task : tasks) {
            taskList.add(task);
        }
        this.tasks = Collections.unmodifiableCollection(taskList);
    }

    @Override
    public void execute(TaskEngine context) {
        for (Task task : tasks) {
            task.execute(context);
        }
    }

}
