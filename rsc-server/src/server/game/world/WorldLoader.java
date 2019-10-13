package server.game.world;

import java.util.List;
import java.util.zip.ZipFile;

import server.entityhandling.locs.GameObjectLoc;
import server.entityhandling.locs.ItemLoc;
import server.entityhandling.locs.NpcLoc;
import server.res.ResourceLoader;

public class WorldLoader {

    @SuppressWarnings("unchecked")
    public void loadWorld(World world) {

        ZipFile tileArchive = ResourceLoader.loadZipData("Landscape.tar.gz");

        for (int lvl = 0; lvl < 4; lvl++) {
            int wildX = 2304;
            int wildY = 1776 - (lvl * 944);
            for (int sx = 0; sx < 1000; sx += 48) {
                for (int sy = 0; sy < 1000; sy += 48) {
                    int x = (sx + wildX) / 48;
                    int y = (sy + (lvl * 944) + wildY) / 48;
                    loadSection(x, y, lvl, world, sx, sy + (944 * lvl));
                }
            }
        }

        List<GameObjectLoc> gameObjectLocs = (List<GameObjectLoc>)
                ResourceLoader.loadGzipData("locs/GameObjectLoc.xml.gz");
        List<ItemLoc> itemLocs = (List<ItemLoc>)
                ResourceLoader.loadGzipData("locs/ItemLoc.xml.gz");
        List<NpcLoc> npcLocs = (List<NpcLoc>)
                ResourceLoader.loadGzipData("locs/NpcLoc.xml.gz");

        for (GameObjectLoc gameObject : gameObjectLocs) {
//            world.registerGameObject(new GameObject(gameObject));
        }
        for (ItemLoc item : itemLocs) {
//            world.registerItem(new Item(item));
        }
        for (NpcLoc npc : npcLocs) {
//            world.registerNpc(new Npc(npc));
        }
    }

    private void loadSection(int sectionX, int sectionY, int height,
            World world, int bigX, int bigY) {
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

}
