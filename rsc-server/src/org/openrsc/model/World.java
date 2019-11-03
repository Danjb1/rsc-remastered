package org.openrsc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import org.openrsc.model.data.ResourceLoader;
import org.openrsc.model.data.Resources;
import org.openrsc.model.data.definitions.DoorDef;
import org.openrsc.model.data.definitions.GameObjectDef;
import org.openrsc.model.data.definitions.ItemDef;
import org.openrsc.model.data.definitions.NpcDef;
import org.openrsc.model.data.definitions.PrayerDef;
import org.openrsc.model.data.definitions.SpellDef;
import org.openrsc.model.data.definitions.TileDef;
import org.openrsc.model.data.locations.GameObjectLoc;
import org.openrsc.model.data.locations.ItemLoc;
import org.openrsc.model.data.locations.NpcLoc;

/**
 * Loads game data at startup to create a persistent game world.
 */
public class World {

	private static final World INSTANCE = new World();

	private static final int SECTORS_X = 1000;
	private static final int SECTORS_Z = 1000;

	@SuppressWarnings("unused")
	private Sector[][] sectors = new Sector[SECTORS_X][SECTORS_Z];

	// TODO Use the data for tile collision
	@SuppressWarnings("unused")
	private List<GameObjectLoc> gameObjectLocs = new ArrayList<>();

	public World() {
		Logger.getLogger(getClass().getName()).info("Loading...");
		loadDefinitions();
		loadWorld();
	}

	private void loadDefinitions() {
		// Load resources
		Resources.doors       = (DoorDef[])       ResourceLoader.loadGzipData("defs/DoorDef.xml.gz");
		Resources.gameObjects = (GameObjectDef[]) ResourceLoader.loadGzipData("defs/GameObjectDef.xml.gz");
		Resources.items       = (ItemDef[])       ResourceLoader.loadGzipData("defs/ItemDef.xml.gz");
		Resources.npcs        = (NpcDef[])        ResourceLoader.loadGzipData("defs/NPCDef.xml.gz");
		Resources.prayers     = (PrayerDef[])     ResourceLoader.loadGzipData("defs/PrayerDef.xml.gz");
		Resources.spells      = (SpellDef[])      ResourceLoader.loadGzipData("defs/SpellDef.xml.gz");
		Resources.tiles       = (TileDef[])       ResourceLoader.loadGzipData("defs/TileDef.xml.gz");
	}

	@SuppressWarnings({ "unused", "unchecked" } )
	public void loadWorld() {

		ZipFile tileArchive = ResourceLoader.loadZipData("Landscape.tar.gz");

		for (int lvl = 0; lvl < 4; lvl++) {
			int wildX = 2304;
			int wildY = 1776 - (lvl * 944);
			for (int sx = 0; sx < 1000; sx += 48) {
				for (int sy = 0; sy < 1000; sy += 48) {
					int x = (sx + wildX) / 48;
					int y = (sy + (lvl * 944) + wildY) / 48;
					loadSection(x, y, lvl, getInstance(), sx, sy + (944 * lvl));
				}
			}
		}

		// Read game object spawns.
		this.gameObjectLocs = (List<GameObjectLoc>) ResourceLoader.loadGzipData("locs/GameObjectLoc.xml.gz");

		// TODO These arent implemented yet
		// Read ground item spawns.
		List<ItemLoc> itemLocs = (List<ItemLoc>) ResourceLoader.loadGzipData("locs/ItemLoc.xml.gz");
		for (ItemLoc item : itemLocs) {
			// world.registerItem(new Item(item));
		}
		itemLocs.clear(); // Unload

		// Read npc spawns.
		List<NpcLoc> npcLocs = (List<NpcLoc>) ResourceLoader.loadGzipData("locs/NpcLoc.xml.gz");
		for (NpcLoc npc : npcLocs) {
			Npc n = NpcManager.getInstance().create(npc.getId(), npc.startX(), npc.startY());
			int movementRadius = new Location(npc.startX(), npc.startY()).getDistance(new Location(npc.maxX(), npc.maxY()));
			n.setMovementRadius(movementRadius);
		}
		npcLocs.clear(); // Unload

	}

	private void loadSection(int sectionX, int sectionY, int height, World world, int bigX, int bigY) {
		//        Sector s = null;
		//        try {
		//            String filename = "h" + height + "x" + sectionX + "y" + sectionY;
		//            ZipEntry e = tileArchive.getEntry(filename);
		//            if (e == null) {
		//                throw new Exception("Missing tile: " + filename);
		//            }
		//            ByteBuffer data = DataConversions.streamToBuffer(
		//                    new BufferedInputStream(tileArchive.getInputStream(e)));
		//            s = Sector.unpack(data);
		//            // s = modifyAndSave(filename, s, bigX, bigY);
		//        } catch (Exception e) {
		//            e.printStackTrace();
		//        }
		//
		//        for (int y = 0; y < Sector.DEPTH; y++) {
		//            for (int x = 0; x < Sector.WIDTH; x++) {
		//                int bx = bigX + x;
		//                int by = bigY + y;
		//                if (!world.withinWorld(bx, by)) {
		//                    continue;
		//                }
		//                if ((s.getTile(x, y).groundOverlay & 0xff) == 250) {
		//                    s.getTile(x, y).groundOverlay = (byte) 2;
		//                }
		//                int groundOverlay = s.getTile(x, y).groundOverlay & 0xFF;
		//                if (groundOverlay > 0 && Resources
		//                        .getTileDef(groundOverlay - 1).getObjectType() != 0) {
		//                    world.getTileValue(bx, by).mapValue |= 0x40; // 64
		//                }
		//
		//                int verticalWall = s.getTile(x, y).verticalWall & 0xFF;
		//                if (verticalWall > 0
		//                        && Resources.getDoorDef(verticalWall - 1)
		//                                .getUnknown() == 0
		//                        && Resources.getDoorDef(verticalWall - 1)
		//                                .getDoorType() != 0) {
		//                    world.getTileValue(bx, by).mapValue |= 1; // 1
		//                    world.getTileValue(bx, by - 1).mapValue |= 4; // 4
		//                }
		//
		//                int horizontalWall = s.getTile(x, y).horizontalWall & 0xFF;
		//                if (horizontalWall > 0
		//                        && Resources.getDoorDef(horizontalWall - 1)
		//                                .getUnknown() == 0
		//                        && Resources.getDoorDef(horizontalWall - 1)
		//                                .getDoorType() != 0) {
		//                    world.getTileValue(bx, by).mapValue |= 2; // 2
		//                    world.getTileValue(bx - 1, by).mapValue |= 8; // 8
		//                }
		//
		//                int diagonalWalls = s.getTile(x, y).diagonalWalls;
		//                if (diagonalWalls > 0 && diagonalWalls < 12000
		//                        && Resources.getDoorDef(diagonalWalls - 1)
		//                                .getUnknown() == 0
		//                        && Resources.getDoorDef(diagonalWalls - 1)
		//                                .getDoorType() != 0) {
		//                    world.getTileValue(bx, by).mapValue |= 0x20; // 32
		//                }
		//                if (diagonalWalls > 12000 && diagonalWalls < 24000
		//                        && Resources.getDoorDef(diagonalWalls - 12001)
		//                                .getUnknown() == 0
		//                        && Resources.getDoorDef(diagonalWalls - 12001)
		//                                .getDoorType() != 0) {
		//                    world.getTileValue(bx, by).mapValue |= 0x10; // 16
		//                }
		//            }
		//        }
	}

	/**
	 * Executed when the server shuts down.
	 */
	public void onShutdown() {
		Logger.getLogger(getClass().getName()).info("Shutting down..");
		PlayerManager.getInstance().onShutdown();
		NpcManager.getInstance().onShutdown();
	}

	public static World getInstance() {
		return INSTANCE;
	}

}
