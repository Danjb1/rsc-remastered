package client.game.world;

import client.game.model.Sector;
import client.game.scene.Model;
import client.game.scene.Scene;

/**
 * Class representing the game world.
 *
 * The x- and z-axes are on the horizontal plane, and the y-axis is the
 * elevation.
 *
 * @author Dan Bryce
 */
public class World {

    /**
     * Offset from which tile co-ordinates are calculated.
     */
    public static final int START_X = 2304;

    /**
     * Offset from which tile co-ordinates are calculated.
     */
    public static final int START_Z = 1776;

    /**
     * Width of 1 Tile, in world units.
     */
    public static final int TILE_WIDTH = 128;

    /**
     * Depth of 1 Tile, in world units.
     */
    public static final int TILE_DEPTH = 128;

    /**
     * Number of Sectors loaded in the x-axis.
     */
    private static final int SECTORS_X = 2;

    /**
     * Number of Sectors loaded in the z-axis.
     */
    private static final int SECTORS_Z = 2;

    /**
     * Total number of Sectors loaded at a time.
     */
    private static final int NUM_SECTORS = SECTORS_X * SECTORS_Z;

    /**
     * The number of loaded Tiles in the x-axis.
     */
    public static final int NUM_TILES_X = SECTORS_X * Sector.WIDTH;

    /**
     * The number of loaded Tiles in the y-axis.
     */
    public static final int NUM_TILES_Z = SECTORS_Z * Sector.DEPTH;

    /**
     * Number of faces present in the loaded terrain.
     */
    public static final int NUM_TERRAIN_FACES = NUM_SECTORS * Sector.NUM_FACES;

    /**
     * Number of layers in the world.
     */
    private static final int NUM_LAYERS = 4;

    /**
     * Index of the layer at ground level.
     */
    public static final int LAYER_GROUND = 0;

    /**
     * The Scene that should hold the loaded world models.
     */
    private Scene scene;

    /**
     * Tile position corresponding to a given face of the terrain model.
     *
     * <p>The index is given by the faceTag of the selected face.
     */
    private int[] tileXForFace = new int[NUM_TERRAIN_FACES];
    private int[] tileZForFace = new int[NUM_TERRAIN_FACES];

    /**
     * Currently-loaded Sectors.
     *
     * <p>When we load sector (x, z) we end up with the following:
     *
     * <pre>
     *  sectors[0] = (x - 1, z - 1)
     *  sectors[1] = (x, z - 1)
     *  sectors[2] = (x - 1, z)
     *  sectors[3] = (x, z)
     * </pre>
     */
    private Sector[] sectors = new Sector[NUM_SECTORS];

    private Model[] landscapeModels = new Model[64];

    private Model[][] wallModels = new Model[NUM_LAYERS][64];

    private Model[][] roofModels = new Model[NUM_LAYERS][64];

    private int[][] elevation = new int[NUM_TILES_X][NUM_TILES_Z];

    /**
     * The first tile in the World that is currently loaded, in the x-axis.
     */
    private int originX;

    /**
     * The first tile in the World that is currently loaded, in the z-axis.
     */
    private int originZ;

    private int currentLayer = 0;

    private int sectorX;

    private int sectorZ;

    private int mapBoundaryX1;
    private int mapBoundaryZ1;
    private int mapBoundaryX2;
    private int mapBoundaryZ2;

    private int numGameObjects;
    private GameObject[] gameObjects = new GameObject[1500];

    private int numDoors;
    private Door[] doors = new Door[500];

    public World(Scene scene) {
        this.scene = scene;
    }

    public void clear() {
        scene.dispose();
        for (int i = 0; i < 64; i++) {
            landscapeModels[i] = null;
            for (int k = 0; k < 4; k++) {
                roofModels[k][i] = null;
            }
        }
        System.gc();
    }

    public void setTilePosForFace(int faceId, int x, int z) {
        this.tileXForFace[faceId] = x;
        this.tileZForFace[faceId] = z;
    }

    public int getTileXForFace(int faceId) {
        return tileXForFace[faceId];
    }

    public int getTileZForFace(int faceId) {
        return tileZForFace[faceId];
    }

    public void setLandscapeModels(Model[] landscapeModels) {
        this.landscapeModels = landscapeModels;

        for (int i = 0; i < 64; i++) {
            scene.addModel(landscapeModels[i]);
        }
    }

    public void setElevation(int x, int z, int newElevation) {
        elevation[x][z] = newElevation;
    }

    public int getElevation(int x, int z) {
        return elevation[x][z];
    }

    public int getGroundElevation(int x, int z) {

        if (!containsTileRelativeToOrigin(x, z)) {
            return 0;
        }

        byte layer = 0;
        if (x >= Sector.WIDTH && z < Sector.DEPTH) {
            layer = 1;
            x -= Sector.WIDTH;
        } else if (x < Sector.WIDTH && z >= Sector.DEPTH) {
            layer = 2;
            z -= Sector.DEPTH;
        } else if (x >= Sector.WIDTH && z >= Sector.DEPTH) {
            layer = 3;
            x -= Sector.WIDTH;
            z -= Sector.DEPTH;
        }

        return (sectors[layer].getTile(x, z).groundElevation & 0xff) * 3;
    }

    public int getAveragedElevation(int tileX, int tileZ) {

        int x = tileX >> 7;
        int z = tileZ >> 7;
        int i1 = tileX & 0x7f;
        int j1 = tileZ & 0x7f;

        if (x < 0 || z < 0 ||
                x >= World.NUM_TILES_X - 1 || z >= World.NUM_TILES_Z - 1) {
            return 0;
        }

        int k1;
        int l1;
        int i2;

        if (i1 <= 128 - j1) {
            k1 = getGroundElevation(x, z);
            l1 = getGroundElevation(x + 1, z) - k1;
            i2 = getGroundElevation(x, z + 1) - k1;
        } else {
            k1 = getGroundElevation(x + 1, z + 1);
            l1 = getGroundElevation(x, z + 1) - k1;
            i2 = getGroundElevation(x + 1, z) - k1;
            i1 = 128 - i1;
            j1 = 128 - j1;
        }

        return k1 + (l1 * i1) / 128 + (i2 * j1) / 128;
    }

    public void setWallModels(int layer, Model[] newWallModels) {
        wallModels[layer] = newWallModels;

        for (int i = 0; i < 64; i++) {
            scene.addModel(wallModels[layer][i]);
        }
    }

    public void setRoofModels(int layer, Model[] newRoofModels) {
        roofModels[layer] = newRoofModels;
        for (int l9 = 0; l9 < 64; l9++) {
            scene.addModel(roofModels[layer][l9]);
        }
    }

    public Model getLandscapeModel(int i) {
        return landscapeModels[i];
    }

    public Model getWallModel(int layer, int index) {
        return wallModels[layer][index];
    }

    public Model getRoofModel(int layer, int index) {
        return roofModels[layer][index];
    }

    public int getGroundTexture(int x, int z) {

        if (x < 0 || x >= NUM_TILES_X || z < 0 || z >= NUM_TILES_Z) {
            return 0;
        }

        byte layer = 0;
        if (x >= Sector.WIDTH && z < Sector.DEPTH) {
            layer = 1;
            x -= Sector.WIDTH;
        } else if (x < Sector.WIDTH && z >= Sector.DEPTH) {
            layer = 2;
            z -= Sector.DEPTH;
        } else if (x >= Sector.WIDTH && z >= Sector.DEPTH) {
            layer = 3;
            x -= Sector.WIDTH;
            z -= Sector.DEPTH;
        }

        return sectors[layer].getTile(x, z).texture & 0xFF;
    }

    public void setGroundTextureOverlay(int x, int z, int groundOverlay) {

        if (x < 0 || x >= NUM_TILES_X || z < 0 || z >= NUM_TILES_Z) {
            return;
        }

        byte layer = 0;
        if (x >= Sector.WIDTH && z < Sector.DEPTH) {
            layer = 1;
            x -= Sector.WIDTH;
        } else if (x < Sector.WIDTH && z >= Sector.DEPTH) {
            layer = 2;
            z -= Sector.DEPTH;
        } else if (x >= Sector.WIDTH && z >= Sector.DEPTH) {
            layer = 3;
            x -= Sector.WIDTH;
            z -= Sector.DEPTH;
        }

        sectors[layer].getTile(x, z).groundOverlay = (byte) groundOverlay;
    }

    public int getGroundTextureOverlay(int x, int z) {

        if (x < 0 || x >= NUM_TILES_X || z < 0 || z >= NUM_TILES_Z) {
            return 0;
        }

        byte layer = 0;
        if (x >= Sector.WIDTH && z < Sector.DEPTH) {
            layer = 1;
            x -= Sector.WIDTH;
        } else if (x < Sector.WIDTH && z >= Sector.DEPTH) {
            layer = 2;
            z -= Sector.DEPTH;
        } else if (x >= Sector.WIDTH && z >= Sector.DEPTH) {
            layer = 3;
            x -= Sector.WIDTH;
            z -= Sector.DEPTH;
        }

        return sectors[layer].getTile(x, z).groundOverlay & 0xff;
    }

    public void setSector(int i, Sector sector) {
        sectors[i] = sector;
    }

    public Sector getSector(int i) {
        return sectors[i];
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginZ() {
        return originZ;
    }

    public boolean containsTile(int tileX, int tileZ) {
        return tileX > mapBoundaryX1 && tileX < mapBoundaryX2 &&
                tileZ > mapBoundaryZ1 && tileZ < mapBoundaryZ2;
    }

    public boolean containsTileRelativeToOrigin(int tileX, int tileZ) {
        return tileX >= 0 && tileZ >= 0 &&
                tileX < NUM_TILES_X && tileZ < NUM_TILES_Z;
    }

    public int getNumDoors() {
        return numDoors;
    }

    public Door getDoor(int i) {
        return doors[i];
    }

    public int getNumGameObjects() {
        return numGameObjects;
    }

    public GameObject getGameObject(int i) {
        return gameObjects[i];
    }

    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
    }

    public int getSectorX() {
        return sectorX;
    }

    public int getSectorZ() {
        return sectorZ;
    }

    public void setCurrentSector(int sectorX, int sectorZ) {
        this.sectorX = sectorX;
        this.sectorZ = sectorZ;

        // Set map boundary around the loaded sectors
        mapBoundaryX1 = sectorX * Sector.WIDTH - 32;
        mapBoundaryZ1 = sectorZ * Sector.DEPTH - 32;
        mapBoundaryX2 = sectorX * Sector.WIDTH + 32;
        mapBoundaryZ2 = sectorZ * Sector.DEPTH + 32;

        // Set the current origin
        originX = (sectorX * Sector.WIDTH - Sector.WIDTH) - START_X;
        originZ = (sectorZ * Sector.DEPTH - Sector.DEPTH) - START_Z;
    }

    public boolean isLoaded() {
        return sectors[0] != null;
    }

}
