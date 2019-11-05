package org.openrsc.model;

import org.openrsc.model.data.Resources;
import org.openrsc.model.player.Player;

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

    public static int getAttackStyleBonus(Mob mob) {
        // TODO switch (mob.getAttackStyle()) {
        // Shared       +1 each
        // Accurate     +3 atk
        // Aggressive   +3 str
        // Defensive    +3 def
        return 0;
    }

    /**
     * Calculate the max hit possible when using melee.
     */
    public static int getMaximumHitMelee(int strengthLevel, int weaponPower, boolean burstOfStrength, boolean superHumanStrength,
            boolean ultimateStrength, int bonus) {

        // Get the prayer modifier value.
        double prayerModifier = 1.0D;
        if (ultimateStrength) {
            prayerModifier = 1.25D;
        } else if (superHumanStrength) {
            prayerModifier = 1.1D;
        } else if (burstOfStrength) {
            prayerModifier = 1.50D;
        }

        // The max hit, before implementing weapon power.
        double baseMaxHit = (double) ((strengthLevel * prayerModifier) + bonus);

        // Calculate the weapon modifier.
        double weaponModifier = (weaponPower * 0.00175D) + 0.1D;

        // Calculate the final max hit value.
        int maxHit = (int) ((baseMaxHit * weaponModifier) + 1.05D);

        return maxHit;
    }


    /**
     * Calculate the max hit possible when using range.
     */
    public static int getMaximumHitRange() {
        // TODO
        return 0;
    }

    /**
     * Calculate the max hit possible when using range.
     */
    public static int getMaximumHitMagic() {
        // TODO
        return 0;
    }

    /**
     * Calculate how much experience is rewarded for a kill.
     */
    public static int getCombatExperience(Mob mob) {
        double exp = ((mob.getCombatLevel() * 4.5) + 9) * 97.5D;
        return (int) (mob instanceof Player ? (exp / 4D) : exp);
    }

    /**
     * Calculate a mobs combat level.
     * 1 level in Attack, Strength, Hits or Defense is equal to 1/4 of a combat level.
     * 1 level in Magic or Prayer is equivalent to 1/8 of a combat level.
     * If ranged multiplied by 1.5 is equal to Attack + Strength combined, 1 ranged
     * level is worth 0.375 combat levels, and attack and strength aren't counted.
     */
    public static int getCombatLevel(int att, int def, int str, int hits, int magic, int pray, int range) {
        double attack = att + str;
        double defense = def + hits;
        double mage = pray + magic;
        mage /= 8D;

        if (attack < ((double) range * 1.5D)) {
            return (int) ((defense / 4D) + ((double) range * 0.375D) + mage);
        } else {
            return (int) ((attack / 4D) + (defense / 4D) + mage);
        }
    }

}
