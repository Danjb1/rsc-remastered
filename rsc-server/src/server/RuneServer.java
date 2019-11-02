package server;

import java.io.IOException;

import server.entityhandling.defs.DoorDef;
import server.entityhandling.defs.GameObjectDef;
import server.entityhandling.defs.ItemDef;
import server.entityhandling.defs.NpcDef;
import server.entityhandling.defs.PrayerDef;
import server.entityhandling.defs.SpellDef;
import server.entityhandling.defs.TileDef;
import server.game.world.World;
import server.game.world.WorldLoader;
import server.res.ResourceLoader;
import server.res.Resources;

public class RuneServer {

    public RuneServer() throws IOException {
    }

    public void load() {
        // Load resources
        Resources.doors       = (DoorDef[])       ResourceLoader.loadGzipData("defs/DoorDef.xml.gz");
        Resources.gameObjects = (GameObjectDef[]) ResourceLoader.loadGzipData("defs/GameObjectDef.xml.gz");
        Resources.items       = (ItemDef[])       ResourceLoader.loadGzipData("defs/ItemDef.xml.gz");
        Resources.npcs        = (NpcDef[])        ResourceLoader.loadGzipData("defs/NPCDef.xml.gz");
        Resources.prayers     = (PrayerDef[])     ResourceLoader.loadGzipData("defs/PrayerDef.xml.gz");
        Resources.spells      = (SpellDef[])      ResourceLoader.loadGzipData("defs/SpellDef.xml.gz");
        Resources.tiles       = (TileDef[])       ResourceLoader.loadGzipData("defs/TileDef.xml.gz");

        // Load world
        World world = new World();
        WorldLoader worldLoader = new WorldLoader();
        worldLoader.loadWorld(world);
    }

}
