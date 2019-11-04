package org.openrsc.model.data;

import org.openrsc.model.data.definitions.DoorDef;
import org.openrsc.model.data.definitions.GameObjectDef;
import org.openrsc.model.data.definitions.ItemDef;
import org.openrsc.model.data.definitions.NpcDef;
import org.openrsc.model.data.definitions.PrayerDef;
import org.openrsc.model.data.definitions.SpellDef;
import org.openrsc.model.data.definitions.TileDef;

/**
 * Class responsible for holding the game's resources.
 *
 * <p>
 * <i>Based on <code>EntityHandler.java</code> from other RSC sources.</i>
 *
 * @author Dan Bryce
 */
public class Resources {

    public static DoorDef[] doors;
    public static GameObjectDef[] gameObjects;
    public static ItemDef[] items;
    public static NpcDef[] npcs;
    public static PrayerDef[] prayers;
    public static SpellDef[] spells;
    public static TileDef[] tiles;

    public static DoorDef getDoorDef(int id) {
        if (id < 0 || id >= doors.length) {
            return null;
        }
        return doors[id];
    }

    public static GameObjectDef getGameObjectDef(int id) {
        if (id < 0 || id >= gameObjects.length) {
            return null;
        }
        return gameObjects[id];
    }

    public static ItemDef getItemDef(int id) {
        if (id < 0 || id >= items.length) {
            return null;
        }
        return items[id];
    }

    public static TileDef getTileDef(int id) {
        if (id < 0 || id >= tiles.length) {
            return null;
        }
        return tiles[id];
    }

    public static NpcDef getNpcDef(int id) {
        if (id < 0 || id >= npcs.length) {
            return null;
        }
        return npcs[id];
    }

    public static PrayerDef getPrayerDef(int id) {
        if (id < 0 || id >= prayers.length) {
            return null;
        }
        return prayers[id];
    }

    public static SpellDef getSpellDef(int id) {
        if (id < 0 || id >= spells.length) {
            return null;
        }
        return spells[id];
    }

}
