package org.openrsc.model.data.definitions;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The definition wrapper for spells
 */
public class SpellDef extends EntityDef {

	/**
	 * The level required to use the spell
	 */
	public int reqLevel;
	/**
	 * The type of the spell
	 */
	public int type;
	/**
	 * The number of different runes needed for the spell
	 */
	public int runeCount;
	/**
	 * The number of each type of rune (item id) required
	 */
	public HashMap<Integer, Integer> requiredRunes;
	/**
	 * The amount of experience given by this spell
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
