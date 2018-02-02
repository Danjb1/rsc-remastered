package client;

import client.model.Sector;
import client.res.Resources;
import client.scene.GameModel;
import client.scene.Scene;
import client.util.DataUtils;

public class World {

    /**
     * Number of Sectors loaded in the x-axis.
     */
    private static final int SECTORS_X = 2;

    /**
     * Number of Sectors loaded in the y-axis.
     */
    private static final int SECTORS_Y = 2;
    
    /**
     * Total number of Sectors loaded at a time.
     */
    private static final int NUM_SECTORS = SECTORS_X * SECTORS_Y;

    /**
     * The number of loaded Tiles in the x-axis.
     */
    private static final int NUM_TILES_X = SECTORS_X * Sector.WIDTH;
    
    /**
     * The number of loaded Tiles in the y-axis.
     */
    private static final int NUM_TILES_Y = SECTORS_Y * Sector.HEIGHT;
    
    /**
     * Number of faces present in the loaded terrain.
     */
    private static final int NUM_TERRAIN_FACES = NUM_SECTORS * Sector.NUM_FACES;
    
    /**
     * Number of layers in the world.
     */
    private static final int NUM_LAYERS = 4;
    
    /**
     * The Scene that should hold the loaded world models.
     */
    private Scene scene;
    
    /**
     * Position to walk to when a tile is clicked in the terrain.
     * 
     * The index is given by the faceTag of the selected face.
     */
    private int[] selectedX = new int[NUM_TERRAIN_FACES];
    private int[] selectedY = new int[NUM_TERRAIN_FACES];
    
    /**
     * Model used when loading regions.
     */
    private GameModel tmpModel = new GameModel(
            NUM_TERRAIN_FACES + 256,
            NUM_TERRAIN_FACES + 256,
            true,
            true,
            false,
            false,
            true);
    
    /**
     * Whether to dispose of the scene during garbage collection.
     */
    private boolean requiresClean;

    /**
     * Currently-loaded Sectors.
     * 
     * When we load sector (x, y) we end up with the following:
     * 
     *  sectors[0] = (x - 1, y - 1)
     *  sectors[1] = (x, y - 1)
     *  sectors[2] = (x - 1, y)
     *  sectors[3] = (x, y)
     */
    private Sector[] sectors = new Sector[NUM_SECTORS];

    private int[] groundColours = new int[256];
    
    private GameModel[] landscapeModels = new GameModel[64];
    
    private GameModel[][] wallModels = new GameModel[NUM_LAYERS][64];
    
    private GameModel[][] roofModels = new GameModel[NUM_LAYERS][64];
    
    private int[][] elevation = new int[NUM_TILES_X][NUM_TILES_Y];

    public World(Scene scene) {
        this.scene = scene;

        // Initialise ground colours
        for (int i = 0; i < 64; i++) {
            
            // Pale Grass / Snow
            groundColours[i] = DataUtils.rgbToInt(
                    255 - i * 4,
                    255 - (int) (i * 1.75),
                    255 - i * 4);
            
            // Grass
            groundColours[i + 64] = DataUtils.rgbToInt(
                    i * 3,
                    144,
                    0);
            
            // Sand
            groundColours[i + 128] = DataUtils.rgbToInt(
                    192 - (int) (i * 1.5),
                    144 - (int) (i * 1.5),
                    0);
            
            // Dark Grass / Mud
            groundColours[i + 192] = DataUtils.rgbToInt(
                    96 - (int) (i * 1.5),
                    48 + (int) (i * 1.5),
                    0);
        }
    }

    public void garbageCollect() {
        if (requiresClean) {
            scene.dispose();
        }
        for (int i = 0; i < 64; i++) {
            landscapeModels[i] = null;
            for (int k = 0; k < 4; k++) {
                roofModels[k][i] = null;
            }
        }
        System.gc();
    }

    /**
     * Loads the given region.
     * 
     * @param x
     * @param y
     * @param layer
     */
    public void loadRegion(int x, int y, int layer) {
        
        garbageCollect();
        loadRegion(x, y, layer, true);

        if (layer == 0) {
            // Load upper storeys (they should be visible from the ground floor)
            loadRegion(x, y, 1, false);
            loadRegion(x, y, 2, false);
        }
    }

    /**
     * Loads a single layer of the given region.
     * 
     * @param regionX
     * @param regionY
     * @param layer
     * @param isCurrentLayer
     */
    private void loadRegion(int regionX, int regionY, int layer, boolean isCurrentLayer) {

        // Load Sectors
        int sectorX = (regionX + 24) / 48;
        int sectorY = (regionY + 24) / 48;
        sectors[0] = Resources.loadSector(sectorX - 1, sectorY - 1, layer);
        sectors[1] = Resources.loadSector(sectorX    , sectorY - 1, layer);
        sectors[2] = Resources.loadSector(sectorX - 1, sectorY    , layer);
        sectors[3] = Resources.loadSector(sectorX    , sectorY    , layer);
        
        setGroundTexturesOverlay();
        
        tmpModel.clear();

        if (isCurrentLayer) {
            
            /*
             * Load terrain 
             */
    
            for (int x = 0; x < NUM_TILES_X; x++) {
                for (int y = 0; y < NUM_TILES_Y; y++) {
                    int i4 = -getGroundElevation(x, y);
                    if (getGroundTexturesOverlay(x, y) > 0
                            && Resources.getTileDef(getGroundTexturesOverlay(x, y) - 1).getUnknown() == 4) {
                        i4 = 0;
                    }
                    if (getGroundTexturesOverlay(x - 1, y) > 0
                            && Resources.getTileDef(getGroundTexturesOverlay(x - 1, y) - 1).getUnknown() == 4) {
                        i4 = 0;
                    }
                    if (getGroundTexturesOverlay(x, y - 1) > 0
                            && Resources.getTileDef(getGroundTexturesOverlay(x, y - 1) - 1).getUnknown() == 4) {
                        i4 = 0;
                    }
                    if (getGroundTexturesOverlay(x - 1, y - 1) > 0 && Resources
                            .getTileDef(getGroundTexturesOverlay(x - 1, y - 1) - 1).getUnknown() == 4) {
                        i4 = 0;
                    }
                    int j5 = tmpModel.getSomeIndex(x * 128, i4, y * 128);
                    int j7 = (int) (Math.random() * 10D) - 5;
                    tmpModel.setVertexAmbience(j5, j7);
                }
            }

            for (int x = 0; x < NUM_TILES_X - 1; x++) {
                for (int y = 0; y < NUM_TILES_Y - 1; y++) {
                    
                    int k5 = getGroundTexture(x, y);
                    int k7 = groundColours[k5];
                    int i10 = k7;
                    int k12 = k7;
                    int l14 = 0;
                    
                    if (layer == 1 || layer == 2) {
                        k7 = 0xbc614e;
                        i10 = 0xbc614e;
                        k12 = 0xbc614e;
                    }
                    
                    if (getGroundTexturesOverlay(x, y) > 0) {
                        int l16 = getGroundTexturesOverlay(x, y);
                        int l5 = Resources.getTileDef(l16 - 1).getUnknown();
                        int i19 = getTileDef(x, y);
                        k7 = i10 = Resources.getTileDef(l16 - 1).getColour();
                        if (l5 == 4) {
                            k7 = 1;
                            i10 = 1;
                            if (l16 == 12) {
                                k7 = 31;
                                i10 = 31;
                            }
                        }
    
                        if (l5 == 5) {
                            if (getDiagonalWalls(x, y) > 0 && getDiagonalWalls(x, y) < 24000) {
                                if (getOverlayIfRequired(x - 1, y, k12) != 0xbc614e
                                        && getOverlayIfRequired(x, y - 1, k12) != 0xbc614e) {
                                    k7 = getOverlayIfRequired(x - 1, y, k12);
                                    l14 = 0;
                                } else if (getOverlayIfRequired(x + 1, y, k12) != 0xbc614e
                                        && getOverlayIfRequired(x, y + 1, k12) != 0xbc614e) {
                                    i10 = getOverlayIfRequired(x + 1, y, k12);
                                    l14 = 0;
                                } else if (getOverlayIfRequired(x + 1, y, k12) != 0xbc614e
                                        && getOverlayIfRequired(x, y - 1, k12) != 0xbc614e) {
                                    i10 = getOverlayIfRequired(x + 1, y, k12);
                                    l14 = 1;
                                } else if (getOverlayIfRequired(x - 1, y, k12) != 0xbc614e
                                        && getOverlayIfRequired(x, y + 1, k12) != 0xbc614e) {
                                    k7 = getOverlayIfRequired(x - 1, y, k12);
                                    l14 = 1;
                                }
                            }
                        } else if (l5 != 2 || getDiagonalWalls(x, y) > 0 && getDiagonalWalls(x, y) < 24000) {
                            if (getTileDef(x - 1, y) != i19 && getTileDef(x, y - 1) != i19) {
                                k7 = k12;
                                l14 = 0;
                            } else if (getTileDef(x + 1, y) != i19 && getTileDef(x, y + 1) != i19) {
                                i10 = k12;
                                l14 = 0;
                            } else if (getTileDef(x + 1, y) != i19 && getTileDef(x, y - 1) != i19) {
                                i10 = k12;
                                l14 = 1;
                            } else if (getTileDef(x - 1, y) != i19 && getTileDef(x, y + 1) != i19) {
                                k7 = k12;
                                l14 = 1;
                            }
                        }
                    }
                    
                    int i17 = ((getGroundElevation(x + 1, y + 1) - getGroundElevation(x + 1, y))
                            + getGroundElevation(x, y + 1)) - getGroundElevation(x, y);
                    if (k7 != i10 || i17 != 0) {
                        int ai[] = new int[3];
                        int ai7[] = new int[3];
                        if (l14 == 0) {
                            if (k7 != 0xbc614e) {
                                ai[0] = y + x * 96 + 96;
                                ai[1] = y + x * 96;
                                ai[2] = y + x * 96 + 1;
                                int l21 = tmpModel.createFace(3, ai, 0xbc614e, k7);
                                selectedX[l21] = x;
                                selectedY[l21] = y;
                                tmpModel.faceTag[l21] = 0x30d40 + l21;
                            }
                            if (i10 != 0xbc614e) {
                                ai7[0] = y + x * 96 + 1;
                                ai7[1] = y + x * 96 + 96 + 1;
                                ai7[2] = y + x * 96 + 96;
                                int i22 = tmpModel.createFace(3, ai7, 0xbc614e, i10);
                                selectedX[i22] = x;
                                selectedY[i22] = y;
                                tmpModel.faceTag[i22] = 0x30d40 + i22;
                            }
                        } else {
                            if (k7 != 0xbc614e) {
                                ai[0] = y + x * 96 + 1;
                                ai[1] = y + x * 96 + 96 + 1;
                                ai[2] = y + x * 96;
                                int j22 = tmpModel.createFace(3, ai, 0xbc614e, k7);
                                selectedX[j22] = x;
                                selectedY[j22] = y;
                                tmpModel.faceTag[j22] = 0x30d40 + j22;
                            }
                            if (i10 != 0xbc614e) {
                                ai7[0] = y + x * 96 + 96;
                                ai7[1] = y + x * 96;
                                ai7[2] = y + x * 96 + 96 + 1;
                                int k22 = tmpModel.createFace(3, ai7, 0xbc614e, i10);
                                selectedX[k22] = x;
                                selectedY[k22] = y;
                                tmpModel.faceTag[k22] = 0x30d40 + k22;
                            }
                        }
                    } else if (k7 != 0xbc614e) {
                        int ai1[] = new int[4];
                        ai1[0] = y + x * 96 + 96;
                        ai1[1] = y + x * 96;
                        ai1[2] = y + x * 96 + 1;
                        ai1[3] = y + x * 96 + 96 + 1;
                        int l19 = tmpModel.createFace(4, ai1, 0xbc614e, k7);
                        selectedX[l19] = x;
                        selectedY[l19] = y;
                        tmpModel.faceTag[l19] = 0x30d40 + l19;
                    }
                }
            }

            for (int x = 1; x < NUM_TILES_X - 1; x++) {
                for (int y = 1; y < NUM_TILES_Y - 1; y++) {
                    if (getGroundTexturesOverlay(x, y) > 0
                            && Resources.getTileDef(getGroundTexturesOverlay(x, y) - 1).getUnknown() == 4) {
                        int l7 = Resources.getTileDef(getGroundTexturesOverlay(x, y) - 1).getColour();
                        int j10 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y), y * 128);
                        int l12 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y), y * 128);
                        int i15 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y + 1),
                                (y + 1) * 128);
                        int j17 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y + 1), (y + 1) * 128);
                        int ai2[] = { j10, l12, i15, j17 };
                        int i20 = tmpModel.createFace(4, ai2, l7, 0xbc614e);
                        selectedX[i20] = x;
                        selectedY[i20] = y;
                        tmpModel.faceTag[i20] = 0x30d40 + i20;
                    } else if (getGroundTexturesOverlay(x, y) == 0
                            || Resources.getTileDef(getGroundTexturesOverlay(x, y) - 1).getUnknown() != 3) {
                        if (getGroundTexturesOverlay(x, y + 1) > 0 && Resources
                                .getTileDef(getGroundTexturesOverlay(x, y + 1) - 1).getUnknown() == 4) {
                            int i8 = Resources.getTileDef(getGroundTexturesOverlay(x, y + 1) - 1).getColour();
                            int k10 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y), y * 128);
                            int i13 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y), y * 128);
                            int j15 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y + 1),
                                    (y + 1) * 128);
                            int k17 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y + 1), (y + 1) * 128);
                            int ai3[] = { k10, i13, j15, k17 };
                            int j20 = tmpModel.createFace(4, ai3, i8, 0xbc614e);
                            selectedX[j20] = x;
                            selectedY[j20] = y;
                            tmpModel.faceTag[j20] = 0x30d40 + j20;
                        }
                        if (getGroundTexturesOverlay(x, y - 1) > 0 && Resources
                                .getTileDef(getGroundTexturesOverlay(x, y - 1) - 1).getUnknown() == 4) {
                            int j8 = Resources.getTileDef(getGroundTexturesOverlay(x, y - 1) - 1).getColour();
                            int l10 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y), y * 128);
                            int j13 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y), y * 128);
                            int k15 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y + 1),
                                    (y + 1) * 128);
                            int l17 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y + 1), (y + 1) * 128);
                            int ai4[] = { l10, j13, k15, l17 };
                            int k20 = tmpModel.createFace(4, ai4, j8, 0xbc614e);
                            selectedX[k20] = x;
                            selectedY[k20] = y;
                            tmpModel.faceTag[k20] = 0x30d40 + k20;
                        }
                        if (getGroundTexturesOverlay(x + 1, y) > 0 && Resources
                                .getTileDef(getGroundTexturesOverlay(x + 1, y) - 1).getUnknown() == 4) {
                            int k8 = Resources.getTileDef(getGroundTexturesOverlay(x + 1, y) - 1).getColour();
                            int i11 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y), y * 128);
                            int k13 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y), y * 128);
                            int l15 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y + 1),
                                    (y + 1) * 128);
                            int i18 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y + 1), (y + 1) * 128);
                            int ai5[] = { i11, k13, l15, i18 };
                            int l20 = tmpModel.createFace(4, ai5, k8, 0xbc614e);
                            selectedX[l20] = x;
                            selectedY[l20] = y;
                            tmpModel.faceTag[l20] = 0x30d40 + l20;
                        }
                        if (getGroundTexturesOverlay(x - 1, y) > 0 && Resources
                                .getTileDef(getGroundTexturesOverlay(x - 1, y) - 1).getUnknown() == 4) {
                            int l8 = Resources.getTileDef(getGroundTexturesOverlay(x - 1, y) - 1).getColour();
                            int j11 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y), y * 128);
                            int l13 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y), y * 128);
                            int i16 = tmpModel.getSomeIndex((x + 1) * 128, -getGroundElevation(x + 1, y + 1),
                                    (y + 1) * 128);
                            int j18 = tmpModel.getSomeIndex(x * 128, -getGroundElevation(x, y + 1), (y + 1) * 128);
                            int ai6[] = { j11, l13, i16, j18 };
                            int i21 = tmpModel.createFace(4, ai6, l8, 0xbc614e);
                            selectedX[i21] = x;
                            selectedY[i21] = y;
                            tmpModel.faceTag[i21] = 0x30d40 + i21;
                        }
                    }
                }
            }

            tmpModel.getDistanceToSomething(true, 40, 48, -50, -10, -50);
            landscapeModels = tmpModel.createModelArray(0, 0, 1536, 1536, 8, 64, 233, false);
            for (int j6 = 0; j6 < 64; j6++) {
                scene.addModel(landscapeModels[j6]);
            }

            for (int x = 0; x < NUM_TILES_X; x++) {
                for (int y = 0; y < NUM_TILES_Y; y++) {
                    elevation[x][y] = getGroundElevation(x, y);
                }
            }
        }

        tmpModel.clear();
        
        /*
         * Load walls
         */
        
        for (int x = 0; x < NUM_TILES_X - 1; x++) {
            for (int y = 0; y < NUM_TILES_Y - 1; y++) {
                int k3 = getVerticalWall(x, y);
                if (k3 > 0 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    createWall(tmpModel, k3 - 1, x, y, x + 1, y);
                }
                k3 = getHorizontalWall(x, y);
                if (k3 > 0 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    createWall(tmpModel, k3 - 1, x, y, x, y + 1);
                }
                k3 = getDiagonalWalls(x, y);
                if (k3 > 0 && k3 < 12000 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    createWall(tmpModel, k3 - 1, x, y, x + 1, y + 1);
                }
                if (k3 > 12000 && k3 < 24000 && Resources.getDoorDef(k3 - 12001).getUnknown() == 0) {
                    createWall(tmpModel, k3 - 12001, x + 1, y, x, y + 1);
                }
            }
        }

        tmpModel.getDistanceToSomething(false, 60, 24, -50, -10, -50);
        wallModels[layer] = tmpModel.createModelArray(0, 0, 1536, 1536, 8, 64, 338, true);
        for (int l2 = 0; l2 < 64; l2++) {
            scene.addModel(wallModels[layer][l2]);
        }
        

        // Raise wall heights
        for (int x = 0; x < NUM_TILES_X - 1; x++) {
            for (int y = 0; y < NUM_TILES_Y - 1; y++) {
                int k6 = getVerticalWall(x, y);
                if (k6 > 0) {
                    setDoorElevation(k6 - 1, x, y, x + 1, y);
                }
                k6 = getHorizontalWall(x, y);
                if (k6 > 0) {
                    setDoorElevation(k6 - 1, x, y, x, y + 1);
                }
                k6 = getDiagonalWalls(x, y);
                if (k6 > 0 && k6 < 12000) {
                    setDoorElevation(k6 - 1, x, y, x + 1, y + 1);
                }
                if (k6 > 12000 && k6 < 24000) {
                    setDoorElevation(k6 - 12001, x + 1, y, x, y + 1);
                }
            }
        }

        for (int x = 1; x < NUM_TILES_X - 1; x++) {
            for (int y = 1; y < NUM_TILES_Y - 1; y++) {
                int j9 = getRoofTexture(x, y);
                if (j9 > 0) {
                    int l11 = x;
                    int i14 = y;
                    int j16 = x + 1;
                    int k18 = y;
                    int j19 = x + 1;
                    int j21 = y + 1;
                    int l22 = x;
                    int j23 = y + 1;
                    int l23 = 0;
                    int j24 = elevation[l11][i14];
                    int l24 = elevation[j16][k18];
                    int j25 = elevation[j19][j21];
                    int l25 = elevation[l22][j23];
                    if (j24 > 0x13880) {
                        j24 -= 0x13880;
                    }
                    if (l24 > 0x13880) {
                        l24 -= 0x13880;
                    }
                    if (j25 > 0x13880) {
                        j25 -= 0x13880;
                    }
                    if (l25 > 0x13880) {
                        l25 -= 0x13880;
                    }
                    if (j24 > l23) {
                        l23 = j24;
                    }
                    if (l24 > l23) {
                        l23 = l24;
                    }
                    if (j25 > l23) {
                        l23 = j25;
                    }
                    if (l25 > l23) {
                        l23 = l25;
                    }
                    if (l23 >= 0x13880) {
                        l23 -= 0x13880;
                    }
                    if (j24 < 0x13880) {
                        elevation[l11][i14] = l23;
                    } else {
                        elevation[l11][i14] -= 0x13880;
                    }
                    if (l24 < 0x13880) {
                        elevation[j16][k18] = l23;
                    } else {
                        elevation[j16][k18] -= 0x13880;
                    }
                    if (j25 < 0x13880) {
                        elevation[j19][j21] = l23;
                    } else {
                        elevation[j19][j21] -= 0x13880;
                    }
                    if (l25 < 0x13880) {
                        elevation[l22][j23] = l23;
                    } else {
                        elevation[l22][j23] -= 0x13880;
                    }
                }
            }
        }

        tmpModel.clear();
        for (int x = 1; x < NUM_TILES_X - 1; x++) {
            for (int y = 1; y < NUM_TILES_Y - 1; y++) {
                int i12 = getRoofTexture(x, y);
                if (i12 > 0) {
                    int j14 = x;
                    int k16 = y;
                    int l18 = x + 1;
                    int k19 = y;
                    int k21 = x + 1;
                    int i23 = y + 1;
                    int k23 = x;
                    int i24 = y + 1;
                    int k24 = x * 128;
                    int i25 = y * 128;
                    int k25 = k24 + 128;
                    int i26 = i25 + 128;
                    int j26 = k24;
                    int k26 = i25;
                    int l26 = k25;
                    int i27 = i26;
                    int j27 = elevation[j14][k16];
                    int k27 = elevation[l18][k19];
                    int l27 = elevation[k21][i23];
                    int i28 = elevation[k23][i24];
                    int j28 = Resources.getElevationDef(i12 - 1).getUnknown1();
                    if (isCentreRoof(j14, k16) && j27 < 0x13880) {
                        j27 += j28 + 0x13880;
                        elevation[j14][k16] = j27;
                    }
                    if (isCentreRoof(l18, k19) && k27 < 0x13880) {
                        k27 += j28 + 0x13880;
                        elevation[l18][k19] = k27;
                    }
                    if (isCentreRoof(k21, i23) && l27 < 0x13880) {
                        l27 += j28 + 0x13880;
                        elevation[k21][i23] = l27;
                    }
                    if (isCentreRoof(k23, i24) && i28 < 0x13880) {
                        i28 += j28 + 0x13880;
                        elevation[k23][i24] = i28;
                    }
                    if (j27 >= 0x13880) {
                        j27 -= 0x13880;
                    }
                    if (k27 >= 0x13880) {
                        k27 -= 0x13880;
                    }
                    if (l27 >= 0x13880) {
                        l27 -= 0x13880;
                    }
                    if (i28 >= 0x13880) {
                        i28 -= 0x13880;
                    }
                    byte byte0 = 16;
                    if (!isCornerRoof(j14 - 1, k16)) {
                        k24 -= byte0;
                    }
                    if (!isCornerRoof(j14 + 1, k16)) {
                        k24 += byte0;
                    }
                    if (!isCornerRoof(j14, k16 - 1)) {
                        i25 -= byte0;
                    }
                    if (!isCornerRoof(j14, k16 + 1)) {
                        i25 += byte0;
                    }
                    if (!isCornerRoof(l18 - 1, k19)) {
                        k25 -= byte0;
                    }
                    if (!isCornerRoof(l18 + 1, k19)) {
                        k25 += byte0;
                    }
                    if (!isCornerRoof(l18, k19 - 1)) {
                        k26 -= byte0;
                    }
                    if (!isCornerRoof(l18, k19 + 1)) {
                        k26 += byte0;
                    }
                    if (!isCornerRoof(k21 - 1, i23)) {
                        l26 -= byte0;
                    }
                    if (!isCornerRoof(k21 + 1, i23)) {
                        l26 += byte0;
                    }
                    if (!isCornerRoof(k21, i23 - 1)) {
                        i26 -= byte0;
                    }
                    if (!isCornerRoof(k21, i23 + 1)) {
                        i26 += byte0;
                    }
                    if (!isCornerRoof(k23 - 1, i24)) {
                        j26 -= byte0;
                    }
                    if (!isCornerRoof(k23 + 1, i24)) {
                        j26 += byte0;
                    }
                    if (!isCornerRoof(k23, i24 - 1)) {
                        i27 -= byte0;
                    }
                    if (!isCornerRoof(k23, i24 + 1)) {
                        i27 += byte0;
                    }
                    i12 = Resources.getElevationDef(i12 - 1).getUnknown2();
                    j27 = -j27;
                    k27 = -k27;
                    l27 = -l27;
                    i28 = -i28;
                    if (getDiagonalWalls(x, y) > 12000 && getDiagonalWalls(x, y) < 24000
                            && getRoofTexture(x - 1, y - 1) == 0) {
                        int ai8[] = new int[3];
                        ai8[0] = tmpModel.getSomeIndex(l26, l27, i26);
                        ai8[1] = tmpModel.getSomeIndex(j26, i28, i27);
                        ai8[2] = tmpModel.getSomeIndex(k25, k27, k26);
                        tmpModel.createFace(3, ai8, i12, 0xbc614e);
                    } else if (getDiagonalWalls(x, y) > 12000 && getDiagonalWalls(x, y) < 24000
                            && getRoofTexture(x + 1, y + 1) == 0) {
                        int ai9[] = new int[3];
                        ai9[0] = tmpModel.getSomeIndex(k24, j27, i25);
                        ai9[1] = tmpModel.getSomeIndex(k25, k27, k26);
                        ai9[2] = tmpModel.getSomeIndex(j26, i28, i27);
                        tmpModel.createFace(3, ai9, i12, 0xbc614e);
                    } else if (getDiagonalWalls(x, y) > 0 && getDiagonalWalls(x, y) < 12000
                            && getRoofTexture(x + 1, y - 1) == 0) {
                        int ai10[] = new int[3];
                        ai10[0] = tmpModel.getSomeIndex(j26, i28, i27);
                        ai10[1] = tmpModel.getSomeIndex(k24, j27, i25);
                        ai10[2] = tmpModel.getSomeIndex(l26, l27, i26);
                        tmpModel.createFace(3, ai10, i12, 0xbc614e);
                    } else if (getDiagonalWalls(x, y) > 0 && getDiagonalWalls(x, y) < 12000
                            && getRoofTexture(x - 1, y + 1) == 0) {
                        int ai11[] = new int[3];
                        ai11[0] = tmpModel.getSomeIndex(k25, k27, k26);
                        ai11[1] = tmpModel.getSomeIndex(l26, l27, i26);
                        ai11[2] = tmpModel.getSomeIndex(k24, j27, i25);
                        tmpModel.createFace(3, ai11, i12, 0xbc614e);
                    } else if (j27 == k27 && l27 == i28) {
                        int ai12[] = new int[4];
                        ai12[0] = tmpModel.getSomeIndex(k24, j27, i25);
                        ai12[1] = tmpModel.getSomeIndex(k25, k27, k26);
                        ai12[2] = tmpModel.getSomeIndex(l26, l27, i26);
                        ai12[3] = tmpModel.getSomeIndex(j26, i28, i27);
                        tmpModel.createFace(4, ai12, i12, 0xbc614e);
                    } else if (j27 == i28 && k27 == l27) {
                        int ai13[] = new int[4];
                        ai13[0] = tmpModel.getSomeIndex(j26, i28, i27);
                        ai13[1] = tmpModel.getSomeIndex(k24, j27, i25);
                        ai13[2] = tmpModel.getSomeIndex(k25, k27, k26);
                        ai13[3] = tmpModel.getSomeIndex(l26, l27, i26);
                        tmpModel.createFace(4, ai13, i12, 0xbc614e);
                    } else {
                        boolean flag1 = true;
                        if (getRoofTexture(x - 1, y - 1) > 0) {
                            flag1 = false;
                        }
                        if (getRoofTexture(x + 1, y + 1) > 0) {
                            flag1 = false;
                        }
                        if (!flag1) {
                            int ai14[] = new int[3];
                            ai14[0] = tmpModel.getSomeIndex(k25, k27, k26);
                            ai14[1] = tmpModel.getSomeIndex(l26, l27, i26);
                            ai14[2] = tmpModel.getSomeIndex(k24, j27, i25);
                            tmpModel.createFace(3, ai14, i12, 0xbc614e);
                            int ai16[] = new int[3];
                            ai16[0] = tmpModel.getSomeIndex(j26, i28, i27);
                            ai16[1] = tmpModel.getSomeIndex(k24, j27, i25);
                            ai16[2] = tmpModel.getSomeIndex(l26, l27, i26);
                            tmpModel.createFace(3, ai16, i12, 0xbc614e);
                        } else {
                            int ai15[] = new int[3];
                            ai15[0] = tmpModel.getSomeIndex(k24, j27, i25);
                            ai15[1] = tmpModel.getSomeIndex(k25, k27, k26);
                            ai15[2] = tmpModel.getSomeIndex(j26, i28, i27);
                            tmpModel.createFace(3, ai15, i12, 0xbc614e);
                            int ai17[] = new int[3];
                            ai17[0] = tmpModel.getSomeIndex(l26, l27, i26);
                            ai17[1] = tmpModel.getSomeIndex(j26, i28, i27);
                            ai17[2] = tmpModel.getSomeIndex(k25, k27, k26);
                            tmpModel.createFace(3, ai17, i12, 0xbc614e);
                        }
                    }
                }
            }
        }
        
        tmpModel.getDistanceToSomething(true, 50, 50, -50, -10, -50);
        roofModels[layer] = tmpModel.createModelArray(0, 0, 1536, 1536, 8, 64, 169, true);
        for (int l9 = 0; l9 < 64; l9++) {
            scene.addModel(roofModels[layer][l9]);
        }
        if (roofModels[layer][0] == null) {
            throw new RuntimeException("null roof!");
        }
        
        // Raise heights of upper storeys?
        for (int x = 0; x < NUM_TILES_X; x++) {
            for (int y = 0; y < NUM_TILES_Y; y++) {
                if (elevation[x][y] >= 0x13880) {
                    elevation[x][y] -= 0x13880;
                }
            }
        }
    }

    public int getRoofTexture(int x, int y) {
        
        if (x < 0 || x >= NUM_TILES_X || y < 0 || y >= NUM_TILES_Y) {
            return 0;
        }
        
        byte byte0 = 0;
        if (x >= 48 && y < 48) {
            byte0 = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            byte0 = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            byte0 = 3;
            x -= 48;
            y -= 48;
        }
        
        return sectors[byte0].getTile(x, y).roofTexture;
    }

    private void createWall(GameModel gameModel, int i, int x1, int y1, int x2, int y2) {
        setAmbientLighting(x1, y1, 40);
        setAmbientLighting(x2, y2, 40);
        int j1 = Resources.getDoorDef(i).getModelVar1();
        int k1 = Resources.getDoorDef(i).getModelVar2();
        int l1 = Resources.getDoorDef(i).getModelVar3();
        int i2 = x1 * 128;
        int j2 = y1 * 128;
        int k2 = x2 * 128;
        int l2 = y2 * 128;
        int i3 = gameModel.getSomeIndex(i2, -elevation[x1][y1], j2);
        int j3 = gameModel.getSomeIndex(i2, -elevation[x1][y1] - j1, j2);
        int k3 = gameModel.getSomeIndex(k2, -elevation[x2][y2] - j1, l2);
        int l3 = gameModel.getSomeIndex(k2, -elevation[x2][y2], l2);
        int i4 = gameModel.createFace(4, new int[] { i3, j3, k3, l3 }, k1, l1);
        if (Resources.getDoorDef(i).getUnknown() == 5) {
            gameModel.faceTag[i4] = 30000 + i;
        } else {
            gameModel.faceTag[i4] = 0;
        }
    }

    private void setAmbientLighting(int x, int y, int height) {
        int modelIndex1 = x / 12;
        int modelIndex2 = y / 12;
        int otherModelIndex1 = (x - 1) / 12;
        int otherModelIndex2 = (y - 1) / 12;
        setAmbientLighting(modelIndex1, modelIndex2, x, y, height);
        if (modelIndex1 != otherModelIndex1) {
            setAmbientLighting(otherModelIndex1, modelIndex2, x, y, height);
        }
        if (modelIndex2 != otherModelIndex2) {
            setAmbientLighting(modelIndex1, otherModelIndex2, x, y, height);
        }
        if (modelIndex1 != otherModelIndex1 && modelIndex2 != otherModelIndex2) {
            setAmbientLighting(otherModelIndex1, otherModelIndex2, x, y, height);
        }
    }

    private void setAmbientLighting(int modelIndex1, int modelIndex2, int x, int y, int ambience) {
        GameModel gameModel = landscapeModels[modelIndex1 + modelIndex2 * 8];
        for (int vertex = 0; vertex < gameModel.someCount; vertex++) {
            if (gameModel.vertexX[vertex] == x * 128 && gameModel.vertexY[vertex] == y * 128) {
                gameModel.setVertexAmbience(vertex, ambience);
                return;
            }
        }
    }

    private int getVerticalWall(int x, int y) {
        
        if (x < 0 || x >= NUM_TILES_X || y < 0 || y >= NUM_TILES_Y) {
            return 0;
        }
        
        byte layer = 0;
        if (x >= 48 && y < 48) {
            layer = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            layer = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            layer = 3;
            x -= 48;
            y -= 48;
        }
        
        return sectors[layer].getTile(x, y).verticalWall & 0xff;
    }

    private int getHorizontalWall(int x, int y) {
        
        if (x < 0 || x >= NUM_TILES_X || y < 0 || y >= NUM_TILES_Y) {
            return 0;
        }
        
        byte layer = 0;
        if (x >= 48 && y < 48) {
            layer = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            layer = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            layer = 3;
            x -= 48;
            y -= 48;
        }
        
        return sectors[layer].getTile(x, y).horizontalWall & 0xff;
    }

    private int getDiagonalWalls(int x, int y) {
        
        if (x < 0 || x >= NUM_TILES_X || y < 0 || y >= NUM_TILES_Y) {
            return 0;
        }
        
        byte layer = 0;
        if (x >= 48 && y < 48) {
            layer = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            layer = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            layer = 3;
            x -= 48;
            y -= 48;
        }
        
        return sectors[layer].getTile(x, y).diagonalWalls;
    }

    private int getTileDef(int x, int y) {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0) {
            return -1;
        }
        return Resources.getTileDef(texture - 1).getUnknown() != 2 ? 0 : 1;
    }

    private int getOverlayIfRequired(int x, int y, int underlay) {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0) {
            return underlay;
        }
        return Resources.getTileDef(texture - 1).getColour();
    }

    private int getGroundTexture(int x, int y) {
        
        if (x < 0 || x >= NUM_TILES_X || y < 0 || y >= NUM_TILES_Y) {
            return 0;
        }
        
        byte layer = 0;
        if (x >= 48 && y < 48) {
            layer = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            layer = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            layer = 3;
            x -= 48;
            y -= 48;
        }
        
        return sectors[layer].getTile(x, y).groundTexture & 0xFF;
    }

    public boolean isCentreRoof(int x, int y) {
        return getRoofTexture(x, y) > 0 &&
                getRoofTexture(x - 1, y) > 0 &&
                getRoofTexture(x - 1, y - 1) > 0 &&
                getRoofTexture(x, y - 1) > 0;
    }

    public boolean isCornerRoof(int x, int y) {
        return getRoofTexture(x, y) > 0 ||
                getRoofTexture(x - 1, y) > 0 ||
                getRoofTexture(x - 1, y - 1) > 0 ||
                getRoofTexture(x, y - 1) > 0;
    }

    private int getGroundElevation(int x, int y) {
        if (x < 0 || x >= 96 || y < 0 || y >= 96) {
            return 0;
        }
        byte byte0 = 0;
        if (x >= 48 && y < 48) {
            byte0 = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            byte0 = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            byte0 = 3;
            x -= 48;
            y -= 48;
        }
        return (sectors[byte0].getTile(x, y).groundElevation & 0xff) * 3;
    }

    public void setDoorElevation(int doorIndex, int x1, int y1, int x2, int y2) {
        int j1 = Resources.getDoorDef(doorIndex).getModelVar1();
        if (elevation[x1][y1] < 0x13880) {
            elevation[x1][y1] += 0x13880 + j1;
        }
        if (elevation[x2][y2] < 0x13880) {
            elevation[x2][y2] += 0x13880 + j1;
        }
    }

    private void setGroundTexturesOverlay() {
        for (int x = 0; x < NUM_TILES_X; x++) {
            for (int y = 0; y < NUM_TILES_Y; y++) {
                
                if (getGroundTexturesOverlay(x, y) != 250) {
                    continue;
                }
                
                if (x == 47 && getGroundTexturesOverlay(x + 1, y) != 250
                        && getGroundTexturesOverlay(x + 1, y) != 2) {
                    setGroundTexturesOverlay(x, y, 9);
                } else if (y == 47 && getGroundTexturesOverlay(x, y + 1) != 250
                        && getGroundTexturesOverlay(x, y + 1) != 2) {
                    setGroundTexturesOverlay(x, y, 9);
                } else {
                    setGroundTexturesOverlay(x, y, 2);
                }
            }
        }
    }

    private void setGroundTexturesOverlay(int x, int y, int groundOverlay) {
        
        if (x < 0 || x >= NUM_TILES_X || y < 0 || y >= NUM_TILES_Y) {
            return;
        }
        
        byte layer = 0;
        if (x >= 48 && y < 48) {
            layer = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            layer = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            layer = 3;
            x -= 48;
            y -= 48;
        }
        
        sectors[layer].getTile(x, y).groundOverlay = (byte) groundOverlay;
    }

    private int getGroundTexturesOverlay(int x, int y) {
        
        if (x < 0 || x >= NUM_TILES_X || y < 0 || y >= NUM_TILES_Y) {
            return 0;
        }
        
        byte layer = 0;
        if (x >= 48 && y < 48) {
            layer = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            layer = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            layer = 3;
            x -= 48;
            y -= 48;
        }
        
        return sectors[layer].getTile(x, y).groundOverlay & 0xff;
    }

    public int getAveragedElevation(int tileX, int tileY) {
        int x = tileX >> 7;
        int y = tileY >> 7;
        int i1 = tileX & 0x7f;
        int j1 = tileY & 0x7f;
        if (x < 0 || y < 0 || x >= NUM_TILES_X - 1 || y >= NUM_TILES_Y - 1) {
            return 0;
        }
        int k1;
        int l1;
        int i2;
        if (i1 <= 128 - j1) {
            k1 = getGroundElevation(x, y);
            l1 = getGroundElevation(x + 1, y) - k1;
            i2 = getGroundElevation(x, y + 1) - k1;
        } else {
            k1 = getGroundElevation(x + 1, y + 1);
            l1 = getGroundElevation(x, y + 1) - k1;
            i2 = getGroundElevation(x + 1, y) - k1;
            i1 = 128 - i1;
            j1 = 128 - j1;
        }
        return k1 + (l1 * i1) / 128 + (i2 * j1) / 128;
    }

    public GameModel getWallModel(int layer, int index) {
        return wallModels[layer][index];
    }

    public GameModel getRoofModel(int layer, int index) {
        return roofModels[layer][index];
    }

}
