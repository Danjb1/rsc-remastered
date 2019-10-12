package client.res;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import client.entityhandling.defs.AnimationDef;
import client.entityhandling.defs.DoorDef;
import client.entityhandling.defs.ElevationDef;
import client.entityhandling.defs.GameObjectDef;
import client.entityhandling.defs.ItemDef;
import client.entityhandling.defs.NpcDef;
import client.entityhandling.defs.PrayerDef;
import client.entityhandling.defs.SpellDef;
import client.entityhandling.defs.TextureDef;
import client.entityhandling.defs.TileDef;
import client.game.model.Sector;
import client.util.DataUtils;

/**
 * Class responsible for holding the game's resources.
 *
 * <p><i>Based on <code>EntityHandler.java</code> from other RSC sources.</i>
 *
 * @author Dan Bryce
 */
public class Resources {

    /*
     * ZIP archive file handles
     */
    public static ZipFile spriteArchive;
    public static ZipFile tileArchive;

    /**
     * Loaded Sprites
     */
    public static Sprite[] sprites = new Sprite[4000];

    /*
     * Loaded entity definitions
     */
    public static AnimationDef[] animations;
    public static DoorDef[] doors;
    public static ElevationDef[] elevation;
    public static GameObjectDef[] objects;
    public static ItemDef[] items;
    public static NpcDef[] npcs;
    public static PrayerDef[] prayers;
    public static SpellDef[] spells;
    public static TextureDef[] textureDefs;
    public static TileDef[] tiles;

    /*
     * Texture data
     */
    public static Texture[] textures;

    public static Sprite getSprite(int id) {
        return sprites[id];
    }

    public static TileDef getTileDef(int id) {
        if (id < 0 || id >= tiles.length) {
            return null;
        }
        return tiles[id];
    }

    public static DoorDef getDoorDef(int id) {
        if (id < 0 || id >= doors.length) {
            return null;
        }
        return doors[id];
    }

    public static ElevationDef getElevationDef(int id) {
        if (id < 0 || id >= elevation.length) {
            return null;
        }
        return elevation[id];
    }

    public static GameObjectDef getObjectDef(int id) {
        if (id < 0 || id >= objects.length) {
            return null;
        }
        return objects[id];
    }

    public static Sector loadSector(int sectionX, int sectionY, int layer) {
        Sector s = null;
        try {
            String filename = "h" + layer + "x" + sectionX + "y" + sectionY;
            ZipEntry e = tileArchive.getEntry(filename);
            if (e == null) {
                s = new Sector();
                if (layer == 0 || layer == 3) {
                    for (int i = 0; i < 2304; i++) {
                        s.getTile(i).groundOverlay = (byte) (layer == 0 ? -6 : 8);
                    }
                }
            } else {
                ByteBuffer data = DataUtils.streamToBuffer(
                        new BufferedInputStream(tileArchive.getInputStream(e)));
                s = Sector.unpack(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return s;
    }

    public static void initialiseArrays(int textureCount, int numTextureColours64, int numTextureColours128) {
        textures = new Texture[textureCount];
    }

    public static void prepareTexture(int id) {

        if (id < 0) {
            return;
        }

        Texture tex = textures[id];

        if (tex.pixels != null) {
            // Texture already loaded
            return;
        }

        int numPixels = tex.isLarge() ? 65536 : 16384;
        tex.pixels = new int[numPixels];
        int textureSize = !tex.isLarge() ? 64 : 128;
        int pixelIndex = 0;

        // Produce texture by looking up colours in the palette
        for (int y = 0; y < textureSize; y++) {
            for (int x = 0; x < textureSize; x++) {
                int colourIndex = tex.colourData[x + y * textureSize] & 0xff;
                int texColour = tex.palette[colourIndex];
                texColour &= 0xf8f8ff;
                if (texColour == 0) {
                    texColour = 1;
                } else if (texColour == 0xf800ff) {
                    texColour = 0;
                    tex.setHasTransparency(true);
                }
                tex.pixels[pixelIndex++] = texColour;
            }
        }

        /*
         * Produce 3 additional versions of the texture.
         *
         * These seem to be darker versions, which seem to be drawn over the
         * normal texture during rendering.
         */
        for (int i = 0; i < pixelIndex; i++) {
            int colour = tex.pixels[i];
            tex.pixels[pixelIndex + i] = colour - (colour >>> 3) & 0xf8f8ff;
            tex.pixels[pixelIndex * 2 + i] = colour - (colour >>> 2) & 0xf8f8ff;
            tex.pixels[pixelIndex * 3 + i] = colour - (colour >>> 2) - (colour >>> 3) & 0xf8f8ff;
        }

        // No longer needed
        tex.palette = null;
        tex.colourData = null;
    }

}
