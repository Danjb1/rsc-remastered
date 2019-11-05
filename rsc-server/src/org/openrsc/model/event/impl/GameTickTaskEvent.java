package org.openrsc.model.event.impl;

import java.util.Set;

import org.openrsc.model.Npc;
import org.openrsc.model.NpcManager;
import org.openrsc.model.PlayerManager;
import org.openrsc.model.event.Event;
import org.openrsc.model.player.Player;
import org.openrsc.task.Task;
import org.openrsc.task.TaskEngine;

/**
 * The game tick event task.
 */
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
            @Override
            public void run() {
                // Get mob lists.
                Set<Player> cachedPlayerList = PlayerManager.getInstance().getList();
                Set<Npc> cachedNpcList = NpcManager.getInstance().getList();
                
                // Execute the game tick.
                PlayerManager.getInstance().tick(cachedPlayerList, cachedNpcList);
                NpcManager.getInstance().tick(cachedPlayerList, cachedNpcList);
            }
        });
    }

}