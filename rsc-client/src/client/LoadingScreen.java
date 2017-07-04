package client;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
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
import client.res.Resources;
import client.scene.Sprite;
import client.util.DataUtils;

public class LoadingScreen {

    private static final String SPRITES_FILENAME = "Sprites.rscd";

    public static final int SPRITE_MEDIA_START = 2000;
    public static final int SPRITE_UTIL_START = 2100;
    public static final int SPRITE_ITEM_START = 2150;
    public static final int SPRITE_LOGO_START = 2010;
    public static final int SPRITE_PROJECTILE_START = 3160;
    public static final int SPRITE_TEXTURE_START = 3220;

    private Game game;

    private String message = "Loading...";
    private int progress = -1;

    private int[] experienceTable = new int[99];

    public Sprite[] sprites = new Sprite[4000];
    private ZipFile spriteArchive;

    private NpcDef[] npcs;
    private ItemDef[] items;
    private TextureDef[] textures;
    private AnimationDef[] animations;
    private SpellDef[] spells;
    private PrayerDef[] prayers;
    private TileDef[] tiles;
    private DoorDef[] doors;
    private ElevationDef[] elevation;
    private GameObjectDef[] objects;

    private int numInvImages;

    private List<String> models = new ArrayList<>();

    public LoadingScreen(Game game) {
        this.game = game;

        try {
            /*
             * Ideally we would use getResource() here but we need a concrete
             * File, not an input stream! This means we can't ship the data
             * files inside the JAR unless we seriously re-work this code.
             */
            spriteArchive = new ZipFile(new File(Resources.DATA_DIR + SPRITES_FILENAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the game and all required resources.
     */
    public void continueLoading() {

        if (progress == -1) {
            // Don't do anything the first time, to ensure the loading screen
            // is rendered before we start loading stuff
            progress = 0;
            return;
        }

        if (progress == 0) {
            generateExperienceTable();
            loadGameData();
            updateProgress(15, "Unpacking configuration");
            return;

        } else if (progress == 15) {
            //            gameGraphics = new SurfaceSprite(this, windowWidth, windowHeight, 4000);
            //            gameGraphics.setDimensions(0, 0, windowWidth, windowHeight);
            //            Menu.aBoolean220 = false;
            //            spellMenu = new Menu(gameGraphics, 5);
            //            int l = ((Surface) (gameGraphics)).width2 - 199;
            //            byte byte0 = 36;
            //            spellMenuHandle = spellMenu.method162(l, byte0 + 24, 196, 90, 1, 500, true);
            //            friendsMenu = new Menu(gameGraphics, 5);
            //            friendsMenuHandle = friendsMenu.method162(l, byte0 + 40, 196, 126, 1, 500, true);
            loadMedia();
            updateProgress(30, "Unpacking media");
            return;

        } else if (progress == 30) {
            //            loadEntity(); // 45%
            updateProgress(45, "Unpacking entities");
            return;

        } else if (progress == 45) {
            //            scene = new Scene(gameGraphics, 15000, 15000, 1000);
            //            scene.setBounds(windowWidth / 2, windowHeight / 2, windowWidth / 2, windowHeight / 2, windowWidth,
            //                    viewDistance);
            //            scene.clipFar3d = 2400;
            //            scene.clipFar2d = 2400;
            //            scene.fogZFalloff = 1;
            //            scene.fogZDistance = 2300;
            //            scene.setLight(-50, -10, -50);
            //            world = new World(scene, gameGraphics);
            //            loadTextures(); // 60%
            updateProgress(60, "Unpacking textures");
            return;

        } else if (progress == 60) {
            //            loadModels(); // 75%
            updateProgress(75, "Loading 3d models");
            return;

        } else if (progress == 75) {
            //            loadSounds(); // 90%
            updateProgress(90, "Unpacking sound effects");
            return;

        } else if (progress == 90) {
            updateProgress(100, "Starting game...");
            //            drawGameMenu();
            //            makeLoginMenus();
            //            characterCreation = new CharacterCreation(gameGraphics);
            //            resetLoginVars();
        }

        game.finishedLoading();

        //        Fonts.loadFonts(frame);
    }

    private void generateExperienceTable() {
        int totalExperience = 0;
        for (int level = 0; level < 99; level++) {
            int nextLevel = level + 1;
            int experienceForNextLevel = (int) (nextLevel + 300D * Math.pow(2, nextLevel / 7D));
            totalExperience += experienceForNextLevel;
            experienceTable[level] = (totalExperience & 0xffffffc) / 4;
        }
    }

    private void loadGameData() {
        npcs       = (NpcDef[])        Resources.loadData("NPCs.rscd");
        items      = (ItemDef[])       Resources.loadData("Items.rscd");
        textures   = (TextureDef[])    Resources.loadData("Textures.rscd");
        animations = (AnimationDef[])  Resources.loadData("Animations.rscd");
        spells     = (SpellDef[])      Resources.loadData("Spells.rscd");
        prayers    = (PrayerDef[])     Resources.loadData("Prayers.rscd");
        tiles      = (TileDef[])       Resources.loadData("Tiles.rscd");
        doors      = (DoorDef[])       Resources.loadData("Doors.rscd");
        elevation  = (ElevationDef[])  Resources.loadData("Elevation.rscd");
        objects    = (GameObjectDef[]) Resources.loadData("Objects.rscd");

        for (int id = 0; id < items.length; id++) {
            if (items[id].getSprite() + 1 > numInvImages) {
                numInvImages = items[id].getSprite() + 1;
            }
        }

        for (int id = 0; id < objects.length; id++) {
            objects[id].modelID = getModelIndex(objects[id].getObjectModel());
        }
    }

    private int getModelIndex(String name) {
        if (name.equalsIgnoreCase("na")) {
            return 0;
        }
        if (models.contains(name)) {
            models.add(name);
            return models.size() - 1;
        }
        return -1;
    }

    //    private void loadModels() {
    //
    //        String[] modelNames = { "torcha2", "torcha3", "torcha4", "skulltorcha2", "skulltorcha3", "skulltorcha4",
    //                "firea2", "firea3", "fireplacea2", "fireplacea3", "firespell2", "firespell3", "lightning2",
    //                "lightning3", "clawspell2", "clawspell3", "clawspell4", "clawspell5", "spellcharge2", "spellcharge3" };
    //        for (String name : modelNames) {
    //            EntityHandler.getModelIndex(name);
    //        }
    //
    //        byte[] models = load("models36.jag");
    //        if (models == null) {
    //            lastLoadedNull = true;
    //            return;
    //        }
    //        for (int j = 0; j < EntityHandler.getModelCount(); j++) {
    //            int k = DataOperations.method358(EntityHandler.getModelName(j) + ".ob3", models);
    //            if (k == 0) {
    //                gameDataModels[j] = new GameModel(1, 1);
    //            } else {
    //                gameDataModels[j] = new GameModel(models, k, true);
    //            }
    //            gameDataModels[j].transparent = EntityHandler.getModelName(j).equals("giantcrystal");
    //        }
    //    }

    //    private void loadSprite(int id, String packageName, int amount) {
    //        for (int i = id; i < id + amount; i++) {
    //            if (!gameGraphics.loadSprite(i, packageName)) {
    //                lastLoadedNull = true;
    //                return;
    //            }
    //        }
    //    }

    private void loadMedia() {
        loadSprite(SPRITE_MEDIA_START, "media", 1);
        loadSprite(SPRITE_MEDIA_START + 1, "media", 6);
        loadSprite(SPRITE_MEDIA_START + 9, "media", 1);
        loadSprite(SPRITE_MEDIA_START + 10, "media", 1);
        loadSprite(SPRITE_MEDIA_START + 11, "media", 3);
        loadSprite(SPRITE_MEDIA_START + 14, "media", 8);
        loadSprite(SPRITE_MEDIA_START + 22, "media", 1);
        loadSprite(SPRITE_MEDIA_START + 23, "media", 1);
        loadSprite(SPRITE_MEDIA_START + 24, "media", 1);
        loadSprite(SPRITE_MEDIA_START + 25, "media", 2);
        loadSprite(SPRITE_UTIL_START, "media", 2);
        loadSprite(SPRITE_UTIL_START + 2, "media", 4);
        loadSprite(SPRITE_UTIL_START + 6, "media", 2);
        loadSprite(SPRITE_PROJECTILE_START, "media", 7);
        loadSprite(SPRITE_LOGO_START, "media", 1);

        int i = numInvImages;
        for (int j = 1; i > 0; j++) {
            int k = i;
            i -= 30;
            if (k > 30) {
                k = 30;
            }
            loadSprite(SPRITE_ITEM_START + (j - 1) * 30, "media.object", k);
        }
    }

    // packageName currently unused!
    private void loadSprite(int id, String packageName, int amount) {
        for (int i = id; i < id + amount; i++) {
            boolean success = loadSprite(i, packageName);
            if (!success) {
                throw new NullPointerException("Sprite " + packageName + "[" + i + "] failed to load");
            }
        }
    }

    public boolean loadSprite(int id, String packageName) {
        try {
            ZipEntry e = spriteArchive.getEntry(String.valueOf(id));
            if (e == null) {
                System.err.println("Missing sprite: " + id);
                return false;
            }
            ByteBuffer data = DataUtils.streamToBuffer(new BufferedInputStream(spriteArchive.getInputStream(e)));
            sprites[id] = Sprite.deserialise(data);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //    private void loadEntity() {
    //        int animationNumber = 0;
    //        label0: for (int animationIndex = 0; animationIndex < EntityHandler.animationCount(); animationIndex++) {
    //            String s = EntityHandler.getAnimationDef(animationIndex).getName();
    //            for (int nextAnimationIndex = 0; nextAnimationIndex < animationIndex; nextAnimationIndex++) {
    //                if (!EntityHandler.getAnimationDef(nextAnimationIndex).getName().equalsIgnoreCase(s)) {
    //                    continue;
    //                }
    //                EntityHandler.getAnimationDef(animationIndex).number = EntityHandler.getAnimationDef(nextAnimationIndex)
    //                        .getNumber();
    //                continue label0;
    //            }
    //
    //            loadSprite(animationNumber, "entity", 15);
    //            if (EntityHandler.getAnimationDef(animationIndex).hasA()) {
    //                loadSprite(animationNumber + 15, "entity", 3);
    //            }
    //
    //            if (EntityHandler.getAnimationDef(animationIndex).hasF()) {
    //                loadSprite(animationNumber + 18, "entity", 9);
    //            }
    //            EntityHandler.getAnimationDef(animationIndex).number = animationNumber;
    //            animationNumber += 27;
    //        }
    //    }

    //    private void loadTextures() {
    //        scene.initialiseArrays(EntityHandler.textureCount(), 7, 11);
    //        for (int i = 0; i < EntityHandler.textureCount(); i++) {
    //            loadSprite(SPRITE_TEXTURE_START + i, "texture", 1);
    //            Sprite sprite = ((Surface) (gameGraphics)).sprites[SPRITE_TEXTURE_START + i];
    //
    //            int length = sprite.getWidth() * sprite.getHeight();
    //            int[] pixels = sprite.getPixels();
    //            int ai1[] = new int[32768];
    //            for (int k = 0; k < length; k++) {
    //                ai1[((pixels[k] & 0xf80000) >> 9) + ((pixels[k] & 0xf800) >> 6) + ((pixels[k] & 0xf8) >> 3)]++;
    //            }
    //            int[] dictionary = new int[256];
    //            dictionary[0] = 0xff00ff;
    //            int[] temp = new int[256];
    //            for (int i1 = 0; i1 < ai1.length; i1++) {
    //                int j1 = ai1[i1];
    //                if (j1 > temp[255]) {
    //                    for (int k1 = 1; k1 < 256; k1++) {
    //                        if (j1 <= temp[k1]) {
    //                            continue;
    //                        }
    //                        for (int i2 = 255; i2 > k1; i2--) {
    //                            dictionary[i2] = dictionary[i2 - 1];
    //                            temp[i2] = temp[i2 - 1];
    //                        }
    //                        dictionary[k1] = ((i1 & 0x7c00) << 9) + ((i1 & 0x3e0) << 6) + ((i1 & 0x1f) << 3) + 0x40404;
    //                        temp[k1] = j1;
    //                        break;
    //                    }
    //                }
    //                ai1[i1] = -1;
    //            }
    //            byte[] indices = new byte[length];
    //            for (int l1 = 0; l1 < length; l1++) {
    //                int j2 = pixels[l1];
    //                int k2 = ((j2 & 0xf80000) >> 9) + ((j2 & 0xf800) >> 6) + ((j2 & 0xf8) >> 3);
    //                int l2 = ai1[k2];
    //                if (l2 == -1) {
    //                    int i3 = 0x3b9ac9ff;
    //                    int j3 = j2 >> 16 & 0xff;
    //                    int k3 = j2 >> 8 & 0xff;
    //                    int l3 = j2 & 0xff;
    //                    for (int i4 = 0; i4 < 256; i4++) {
    //                        int j4 = dictionary[i4];
    //                        int k4 = j4 >> 16 & 0xff;
    //                        int l4 = j4 >> 8 & 0xff;
    //                        int i5 = j4 & 0xff;
    //                        int j5 = (j3 - k4) * (j3 - k4) + (k3 - l4) * (k3 - l4) + (l3 - i5) * (l3 - i5);
    //                        if (j5 < i3) {
    //                            i3 = j5;
    //                            l2 = i4;
    //                        }
    //                    }
    //
    //                    ai1[k2] = l2;
    //                }
    //                indices[l1] = (byte) l2;
    //            }
    //            scene.defineTexture(i, indices, dictionary, sprite.getSomething1() / 64 - 1);
    //        }
    //    }

    //    private void loadSounds() {
    //        try {
    //            sounds = load("sounds1.mem");
    //            streamAudioPlayer = new StreamAudioPlayer();
    //            return;
    //        } catch (Throwable throwable) {
    //            System.out.println("Unable to init sounds:" + throwable);
    //        }
    //    }

    protected final void updateProgress(int progress, String message) {
        this.progress = progress;
        this.message = message;
    }

    public boolean isLoaded() {
        return progress >= 100;
    }

    public String getMessage() {
        return message;
    }

    public int getProgress() {
        return progress;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

}
