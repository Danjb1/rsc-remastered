package org.openrsc.model;

import org.openrsc.model.data.Resources;

/**
 * A generic combat system that uses RuneScape Classic rules and calculations.
 */
public class Combat {
	
	public static void execute(Mob attacker) {
		Mob opponent = attacker.getOpponent();
		
		if (opponent instanceof Npc) {
			Npc npc = (Npc) opponent;
			
			// Npc is not attackable.
			if (!Resources.npcs[npc.getType()].attackable) {
				return;
			}
			
		}
		
	}

}
