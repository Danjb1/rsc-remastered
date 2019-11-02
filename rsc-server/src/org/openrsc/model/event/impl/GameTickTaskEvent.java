package org.openrsc.model.event.impl;

import java.util.concurrent.TimeUnit;

import org.openrsc.model.NpcManager;
import org.openrsc.model.PlayerManager;
import org.openrsc.model.event.Event;
import org.openrsc.task.Task;
import org.openrsc.task.TaskEngine;

public class GameTickTaskEvent extends Event {

	public GameTickTaskEvent() {
		super(600);
	}

	@Override
	public void execute(TaskEngine context) {
		context.pushTask(new UpdateTask());
	}

}

class UpdateTask implements Task {

	@Override
	public void execute(TaskEngine context) {
		context.submitTask(new Runnable() {
			public void run() {
				long currentTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
				PlayerManager.getInstance().tick(currentTime);
				NpcManager.getInstance().tick(currentTime);
			}
		});
	}

}