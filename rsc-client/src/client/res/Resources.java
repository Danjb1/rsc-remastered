package client.res;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.thoughtworks.xstream.XStream;

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
import client.model.Sector;
import client.util.DataUtils;

/**
 * Class responsible for reading and storing resources required by the game.
 * 
 * <p><i>Based on <code>EntityHandler.java</code> from other RSC sources.</i>
 * 
 * @author Dan Bryce
 */
public class Resources {

    /*
     * Directories
     */
    public static final String RESOURCES_DIR = "res/";
    public static final String DATA_DIR = RESOURCES_DIR + "data/";

    /**
     * Package containing entity definitions.
     */
    private static final String ENTITY_DEF_PACKAGE_NAME = 
            "client.entityhandling.defs";
    
    /**
     * XStream used to read from / write to XML.
     */
    private static XStream xStream = new XStream();

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
    public static NpcDef[] npcs;
    public static ItemDef[] items;
    public static TextureDef[] textureDefs;
    public static AnimationDef[] animations;
    public static SpellDef[] spells;
    public static PrayerDef[] prayers;
    public static TileDef[] tiles;
    public static DoorDef[] doors;
    public static ElevationDef[] elevation;
    public static GameObjectDef[] objects;

    /*
     * Texture data
     */
    public static Texture[] textures;
    
    static {
        // XStream aliases
        addAlias("NPCDef",        ENTITY_DEF_PACKAGE_NAME + ".NpcDef");
        addAlias("ItemDef",       ENTITY_DEF_PACKAGE_NAME + ".ItemDef");
        addAlias("TextureDef",    ENTITY_DEF_PACKAGE_NAME + ".TextureDef");
        addAlias("AnimationDef",  ENTITY_DEF_PACKAGE_NAME + ".AnimationDef");
        addAlias("ItemDropDef",   ENTITY_DEF_PACKAGE_NAME + ".ItemDropDef");
        addAlias("SpellDef",      ENTITY_DEF_PACKAGE_NAME + ".SpellDef");
        addAlias("PrayerDef",     ENTITY_DEF_PACKAGE_NAME + ".PrayerDef");
        addAlias("TileDef",       ENTITY_DEF_PACKAGE_NAME + ".TileDef");
        addAlias("DoorDef",       ENTITY_DEF_PACKAGE_NAME + ".DoorDef");
        addAlias("ElevationDef",  ENTITY_DEF_PACKAGE_NAME + ".ElevationDef");
        addAlias("GameObjectDef", ENTITY_DEF_PACKAGE_NAME + ".GameObjectDef");
    }

    public static InputStream getResourceAsStream(String filename) {
        return Resources.class.getClassLoader().getResourceAsStream(filename);
    }

    private static void addAlias(String name, String className) {
        try {
            xStream.alias(name, Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ZipFile loadZipData(String filename) {
        try {
            return new ZipFile(new File(DATA_DIR + filename));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object loadGzipData(String filename) {
        try {
            InputStream is = new GZIPInputStream(
                    getResourceAsStream(DATA_DIR + filename));
            return xStream.fromXML(is);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void writeData(File file, Object o) {
        try {
            OutputStream os = new GZIPOutputStream(new FileOutputStream(file));
            xStream.toXML(o, os);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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
