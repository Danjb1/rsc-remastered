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
import client.scene.Sprite;
import client.util.DataUtils;

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
    public static TextureDef[] textures;
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
    public static long texturesLoaded;
    public static int textureCount;
    public static byte textureColoursUsed[][];
    public static int textureColourList[][];
    public static int textureDimension[];
    public static long textureLoadedNumber[];
    public static int texturePixels[][];
    public static boolean textureBackTransparent[];
    public static int textureColours64[][];
    public static int textureColours128[][];
    
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

    public static Object loadData(String filename) {
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

    public static Sector loadSector(int sectionX, int sectionY, int height) {
        Sector s = null;
        try {
            String filename = "h" + height + "x" + sectionX + "y" + sectionY;
            ZipEntry e = tileArchive.getEntry(filename);
            if (e == null) {
                s = new Sector();
                if (height == 0 || height == 3) {
                    for (int i = 0; i < 2304; i++) {
                        s.getTile(i).groundOverlay = (byte) (height == 0 ? -6 : 8);
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
        Resources.textureCount = textureCount;
        textureColoursUsed = new byte[textureCount][];
        textureColourList = new int[textureCount][];
        textureDimension = new int[textureCount];
        textureLoadedNumber = new long[textureCount];
        textureBackTransparent = new boolean[textureCount];
        texturePixels = new int[textureCount][];
        texturesLoaded = 0L;
        textureColours64 = new int[numTextureColours64][]; // 64x64 rgba
        textureColours128 = new int[numTextureColours128][]; // 128x128 rgba
    }

    public static void defineTexture(int id, byte[] usedColours, int[] colours, int wide128) {
        textureColoursUsed[id] = usedColours;
        textureColourList[id] = colours;
        textureDimension[id] = wide128; // is 1 if the texture is 128+ pixels wide, 0 if <128
        textureLoadedNumber[id] = 0L;
        textureBackTransparent[id] = false;
        texturePixels[id] = null;
        prepareTexture(id);
    }

    public static void prepareTexture(int i) {
        if (i < 0) {
            return;
        }
        textureLoadedNumber[i] = texturesLoaded++;
        if (texturePixels[i] != null) {
            return;
        }
        if (textureDimension[i] == 0) {
            for (int j = 0; j < textureColours64.length; j++) {
                if (textureColours64[j] == null) {
                    textureColours64[j] = new int[16384];
                    texturePixels[i] = textureColours64[j];
                    setTexturePixels(i);
                    return;
                }
            }

            long l = 1L << 30;
            int i1 = 0;
            for (int k1 = 0; k1 < textureCount; k1++) {
                if (k1 != i && textureDimension[k1] == 0 && texturePixels[k1] != null && textureLoadedNumber[k1] < l) {
                    l = textureLoadedNumber[k1];
                    i1 = k1;
                }
            }

            texturePixels[i] = texturePixels[i1];
            texturePixels[i1] = null;
            setTexturePixels(i);
            return;
        }
        for (int k = 0; k < textureColours128.length; k++) {
            if (textureColours128[k] == null) {
                textureColours128[k] = new int[0x10000];
                texturePixels[i] = textureColours128[k];
                setTexturePixels(i);
                return;
            }
        }

        long l1 = 1L << 30;
        int j1 = 0;
        for (int i2 = 0; i2 < textureCount; i2++) {
            if (i2 != i && textureDimension[i2] == 1 && texturePixels[i2] != null && textureLoadedNumber[i2] < l1) {
                l1 = textureLoadedNumber[i2];
                j1 = i2;
            }
        }

        texturePixels[i] = texturePixels[j1];
        texturePixels[j1] = null;
        setTexturePixels(i);
    }

    private static void setTexturePixels(int i) {
        int textureWidth = textureDimension[i] == 0 ? 64 : 128;
        int colours[] = texturePixels[i];
        int colourCount = 0;
        for (int k = 0; k < textureWidth; k++) {
            for (int l = 0; l < textureWidth; l++) {
                int index = textureColoursUsed[i][l + k * textureWidth] & 0xff;
                int j1 = textureColourList[i][index];
                j1 &= 0xf8f8ff;
                if (j1 == 0) {
                    j1 = 1;
                } else if (j1 == 0xf800ff) {
                    j1 = 0;
                    textureBackTransparent[i] = true;
                }
                colours[colourCount++] = j1;
            }

        }

        for (int i1 = 0; i1 < colourCount; i1++) {
            int colour = colours[i1];
            colours[colourCount + i1] = colour - (colour >>> 3) & 0xf8f8ff;
            colours[colourCount * 2 + i1] = colour - (colour >>> 2) & 0xf8f8ff;
            colours[colourCount * 3 + i1] = colour - (colour >>> 2) - (colour >>> 3) & 0xf8f8ff;
        }

    }

}
