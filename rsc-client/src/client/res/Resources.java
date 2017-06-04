package client.res;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.thoughtworks.xstream.XStream;

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

    static {
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

}
