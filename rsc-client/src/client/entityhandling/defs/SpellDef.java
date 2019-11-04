package client.entityhandling.defs;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Data relating to a spell.
 */
public class SpellDef extends EntityDef {

    /**
     * The minimum Magic level required to use the spell.
     */
    public int reqLevel;

    public int type;

    /**
     * The number of different runes needed for the spell.
     */
    public int runeCount;

    /**
     * The number of each type of rune required, keyed by item ID.
     */
    public HashMap<Integer, Integer> requiredRunes;

    /**
     * The amount of experience awarded by casting this spell.
     */
    public int exp;

    public int getReqLevel() {
        return reqLevel;
    }

    public int getSpellType() {
        return type;
    }

    public int getRuneCount() {
        return runeCount;
    }

    public Set<Entry<Integer, Integer>> getRunesRequired() {
        return requiredRunes.entrySet();
    }

    public int getExp() {
        return exp;
    }
}
