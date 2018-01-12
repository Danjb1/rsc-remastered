package client;

import client.model.Sector;
import client.res.Resources;
import client.scene.GameModel;
import client.scene.Scene;
import client.util.DataUtils;

public class World {

    public static final int COLOUR_TRANSPARENT = 12345678;

    private Scene scene;
    
    private int[] selectedX;
    private int[] selectedY;
    private GameModel tmpModel;
    private boolean requiresClean;

    private Sector[] sectors;

    private int[] groundTextureArray;
    private GameModel[] landscapeModels;
    private GameModel[][] wallModels;
    private GameModel[][] roofModels;
    private int[][] anIntArrayArray581;

    public World(Scene scene) {
        this.scene = scene;

        selectedX = new int[18432];
        selectedY = new int[18432];

        landscapeModels = new GameModel[64];
        wallModels = new GameModel[4][64];
        roofModels = new GameModel[4][64];
        anIntArrayArray581 = new int[96][96];
        requiresClean = true;
        groundTextureArray = new int[256];
        sectors = new Sector[4];

        for (int i = 0; i < 64; i++) {
            groundTextureArray[i] = DataUtils.rgbToInt(255 - i * 4, 255 - (int) (i * 1.75D), 255 - i * 4);
        }
        for (int j = 0; j < 64; j++) {
            groundTextureArray[j + 64] = DataUtils.rgbToInt(j * 3, 144, 0);
        }
        for (int k = 0; k < 64; k++) {
            groundTextureArray[k + 128] = DataUtils.rgbToInt(192 - (int) (k * 1.5D), 144 - (int) (k * 1.5D), 0);
        }
        for (int l = 0; l < 64; l++) {
            groundTextureArray[l + 192] = DataUtils.rgbToInt(96 - (int) (l * 1.5D), 48 + (int) (l * 1.5D), 0);
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

    public void loadRegion(int x, int y, int layer) {
        
        garbageCollect();
        loadRegion(x, y, layer, true);

        if (layer == 0) {
            // Load upper storeys (they should be visible from the ground floor)
            loadRegion(x, y, 1, false);
            loadRegion(x, y, 2, false);
        }
    }

    private void loadRegion(int x, int y, int layer, boolean isCurrentLayer) {

        // Load Sectors
        int l = (x + 24) / 48;
        int i1 = (y + 24) / 48;
        sectors[0] = Resources.loadSector(l - 1, i1 - 1, layer);
        sectors[1] = Resources.loadSector(l, i1 - 1, layer);
        sectors[2] = Resources.loadSector(l - 1, i1, layer);
        sectors[3] = Resources.loadSector(l, i1, layer);
        
        setGroundTexturesOverlay();
        
        if (tmpModel == null) {
            tmpModel = new GameModel(18688, 18688, true, true, false, false, true);
        }
        
        GameModel gameModel = tmpModel;
        gameModel.clear();

        if (isCurrentLayer) {
            
            /*
             * Load terrain 
             */
    
            for (int j2 = 0; j2 < 96; j2++) {
                for (int i3 = 0; i3 < 96; i3++) {
                    int i4 = -getGroundElevation(j2, i3);
                    if (getGroundTexturesOverlay(j2, i3) > 0
                            && Resources.getTileDef(getGroundTexturesOverlay(j2, i3) - 1).getUnknown() == 4) {
                        i4 = 0;
                    }
                    if (getGroundTexturesOverlay(j2 - 1, i3) > 0
                            && Resources.getTileDef(getGroundTexturesOverlay(j2 - 1, i3) - 1).getUnknown() == 4) {
                        i4 = 0;
                    }
                    if (getGroundTexturesOverlay(j2, i3 - 1) > 0
                            && Resources.getTileDef(getGroundTexturesOverlay(j2, i3 - 1) - 1).getUnknown() == 4) {
                        i4 = 0;
                    }
                    if (getGroundTexturesOverlay(j2 - 1, i3 - 1) > 0 && Resources
                            .getTileDef(getGroundTexturesOverlay(j2 - 1, i3 - 1) - 1).getUnknown() == 4) {
                        i4 = 0;
                    }
                    int j5 = gameModel.getSomeIndex(j2 * 128, i4, i3 * 128);
                    int j7 = (int) (Math.random() * 10D) - 5;
                    gameModel.setByteAtIndexToValue(j5, j7);
                }
            }

            for (int j3 = 0; j3 < 95; j3++) {
                for (int j4 = 0; j4 < 95; j4++) {
                    
                    int k5 = getGroundTexture(j3, j4);
                    int k7 = groundTextureArray[k5];
                    int i10 = k7;
                    int k12 = k7;
                    int l14 = 0;
                    
                    if (layer == 1 || layer == 2) {
                        k7 = 0xbc614e;
                        i10 = 0xbc614e;
                        k12 = 0xbc614e;
                    }
                    
                    if (getGroundTexturesOverlay(j3, j4) > 0) {
                        int l16 = getGroundTexturesOverlay(j3, j4);
                        int l5 = Resources.getTileDef(l16 - 1).getUnknown();
                        int i19 = getTileDef(j3, j4);
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
                            if (getDiagonalWalls(j3, j4) > 0 && getDiagonalWalls(j3, j4) < 24000) {
                                if (getOverlayIfRequired(j3 - 1, j4, k12) != 0xbc614e
                                        && getOverlayIfRequired(j3, j4 - 1, k12) != 0xbc614e) {
                                    k7 = getOverlayIfRequired(j3 - 1, j4, k12);
                                    l14 = 0;
                                } else if (getOverlayIfRequired(j3 + 1, j4, k12) != 0xbc614e
                                        && getOverlayIfRequired(j3, j4 + 1, k12) != 0xbc614e) {
                                    i10 = getOverlayIfRequired(j3 + 1, j4, k12);
                                    l14 = 0;
                                } else if (getOverlayIfRequired(j3 + 1, j4, k12) != 0xbc614e
                                        && getOverlayIfRequired(j3, j4 - 1, k12) != 0xbc614e) {
                                    i10 = getOverlayIfRequired(j3 + 1, j4, k12);
                                    l14 = 1;
                                } else if (getOverlayIfRequired(j3 - 1, j4, k12) != 0xbc614e
                                        && getOverlayIfRequired(j3, j4 + 1, k12) != 0xbc614e) {
                                    k7 = getOverlayIfRequired(j3 - 1, j4, k12);
                                    l14 = 1;
                                }
                            }
                        } else if (l5 != 2 || getDiagonalWalls(j3, j4) > 0 && getDiagonalWalls(j3, j4) < 24000) {
                            if (getTileDef(j3 - 1, j4) != i19 && getTileDef(j3, j4 - 1) != i19) {
                                k7 = k12;
                                l14 = 0;
                            } else if (getTileDef(j3 + 1, j4) != i19 && getTileDef(j3, j4 + 1) != i19) {
                                i10 = k12;
                                l14 = 0;
                            } else if (getTileDef(j3 + 1, j4) != i19 && getTileDef(j3, j4 - 1) != i19) {
                                i10 = k12;
                                l14 = 1;
                            } else if (getTileDef(j3 - 1, j4) != i19 && getTileDef(j3, j4 + 1) != i19) {
                                k7 = k12;
                                l14 = 1;
                            }
                        }
                    }
                    
                    int i17 = ((getGroundElevation(j3 + 1, j4 + 1) - getGroundElevation(j3 + 1, j4))
                            + getGroundElevation(j3, j4 + 1)) - getGroundElevation(j3, j4);
                    if (k7 != i10 || i17 != 0) {
                        int ai[] = new int[3];
                        int ai7[] = new int[3];
                        if (l14 == 0) {
                            if (k7 != 0xbc614e) {
                                ai[0] = j4 + j3 * 96 + 96;
                                ai[1] = j4 + j3 * 96;
                                ai[2] = j4 + j3 * 96 + 1;
                                int l21 = gameModel.createFace(3, ai, 0xbc614e, k7);
                                selectedX[l21] = j3;
                                selectedY[l21] = j4;
                                gameModel.faceTag[l21] = 0x30d40 + l21;
                            }
                            if (i10 != 0xbc614e) {
                                ai7[0] = j4 + j3 * 96 + 1;
                                ai7[1] = j4 + j3 * 96 + 96 + 1;
                                ai7[2] = j4 + j3 * 96 + 96;
                                int i22 = gameModel.createFace(3, ai7, 0xbc614e, i10);
                                selectedX[i22] = j3;
                                selectedY[i22] = j4;
                                gameModel.faceTag[i22] = 0x30d40 + i22;
                            }
                        } else {
                            if (k7 != 0xbc614e) {
                                ai[0] = j4 + j3 * 96 + 1;
                                ai[1] = j4 + j3 * 96 + 96 + 1;
                                ai[2] = j4 + j3 * 96;
                                int j22 = gameModel.createFace(3, ai, 0xbc614e, k7);
                                selectedX[j22] = j3;
                                selectedY[j22] = j4;
                                gameModel.faceTag[j22] = 0x30d40 + j22;
                            }
                            if (i10 != 0xbc614e) {
                                ai7[0] = j4 + j3 * 96 + 96;
                                ai7[1] = j4 + j3 * 96;
                                ai7[2] = j4 + j3 * 96 + 96 + 1;
                                int k22 = gameModel.createFace(3, ai7, 0xbc614e, i10);
                                selectedX[k22] = j3;
                                selectedY[k22] = j4;
                                gameModel.faceTag[k22] = 0x30d40 + k22;
                            }
                        }
                    } else if (k7 != 0xbc614e) {
                        int ai1[] = new int[4];
                        ai1[0] = j4 + j3 * 96 + 96;
                        ai1[1] = j4 + j3 * 96;
                        ai1[2] = j4 + j3 * 96 + 1;
                        ai1[3] = j4 + j3 * 96 + 96 + 1;
                        int l19 = gameModel.createFace(4, ai1, 0xbc614e, k7);
                        selectedX[l19] = j3;
                        selectedY[l19] = j4;
                        gameModel.faceTag[l19] = 0x30d40 + l19;
                    }
                }
            }

            for (int k4 = 1; k4 < 95; k4++) {
                for (int i6 = 1; i6 < 95; i6++) {
                    if (getGroundTexturesOverlay(k4, i6) > 0
                            && Resources.getTileDef(getGroundTexturesOverlay(k4, i6) - 1).getUnknown() == 4) {
                        int l7 = Resources.getTileDef(getGroundTexturesOverlay(k4, i6) - 1).getColour();
                        int j10 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                        int l12 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                        int i15 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1),
                                (i6 + 1) * 128);
                        int j17 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                        int ai2[] = { j10, l12, i15, j17 };
                        int i20 = gameModel.createFace(4, ai2, l7, 0xbc614e);
                        selectedX[i20] = k4;
                        selectedY[i20] = i6;
                        gameModel.faceTag[i20] = 0x30d40 + i20;
                    } else if (getGroundTexturesOverlay(k4, i6) == 0
                            || Resources.getTileDef(getGroundTexturesOverlay(k4, i6) - 1).getUnknown() != 3) {
                        if (getGroundTexturesOverlay(k4, i6 + 1) > 0 && Resources
                                .getTileDef(getGroundTexturesOverlay(k4, i6 + 1) - 1).getUnknown() == 4) {
                            int i8 = Resources.getTileDef(getGroundTexturesOverlay(k4, i6 + 1) - 1).getColour();
                            int k10 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                            int i13 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                            int j15 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1),
                                    (i6 + 1) * 128);
                            int k17 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                            int ai3[] = { k10, i13, j15, k17 };
                            int j20 = gameModel.createFace(4, ai3, i8, 0xbc614e);
                            selectedX[j20] = k4;
                            selectedY[j20] = i6;
                            gameModel.faceTag[j20] = 0x30d40 + j20;
                        }
                        if (getGroundTexturesOverlay(k4, i6 - 1) > 0 && Resources
                                .getTileDef(getGroundTexturesOverlay(k4, i6 - 1) - 1).getUnknown() == 4) {
                            int j8 = Resources.getTileDef(getGroundTexturesOverlay(k4, i6 - 1) - 1).getColour();
                            int l10 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                            int j13 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                            int k15 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1),
                                    (i6 + 1) * 128);
                            int l17 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                            int ai4[] = { l10, j13, k15, l17 };
                            int k20 = gameModel.createFace(4, ai4, j8, 0xbc614e);
                            selectedX[k20] = k4;
                            selectedY[k20] = i6;
                            gameModel.faceTag[k20] = 0x30d40 + k20;
                        }
                        if (getGroundTexturesOverlay(k4 + 1, i6) > 0 && Resources
                                .getTileDef(getGroundTexturesOverlay(k4 + 1, i6) - 1).getUnknown() == 4) {
                            int k8 = Resources.getTileDef(getGroundTexturesOverlay(k4 + 1, i6) - 1).getColour();
                            int i11 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                            int k13 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                            int l15 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1),
                                    (i6 + 1) * 128);
                            int i18 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                            int ai5[] = { i11, k13, l15, i18 };
                            int l20 = gameModel.createFace(4, ai5, k8, 0xbc614e);
                            selectedX[l20] = k4;
                            selectedY[l20] = i6;
                            gameModel.faceTag[l20] = 0x30d40 + l20;
                        }
                        if (getGroundTexturesOverlay(k4 - 1, i6) > 0 && Resources
                                .getTileDef(getGroundTexturesOverlay(k4 - 1, i6) - 1).getUnknown() == 4) {
                            int l8 = Resources.getTileDef(getGroundTexturesOverlay(k4 - 1, i6) - 1).getColour();
                            int j11 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6), i6 * 128);
                            int l13 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6), i6 * 128);
                            int i16 = gameModel.getSomeIndex((k4 + 1) * 128, -getGroundElevation(k4 + 1, i6 + 1),
                                    (i6 + 1) * 128);
                            int j18 = gameModel.getSomeIndex(k4 * 128, -getGroundElevation(k4, i6 + 1), (i6 + 1) * 128);
                            int ai6[] = { j11, l13, i16, j18 };
                            int i21 = gameModel.createFace(4, ai6, l8, 0xbc614e);
                            selectedX[i21] = k4;
                            selectedY[i21] = i6;
                            gameModel.faceTag[i21] = 0x30d40 + i21;
                        }
                    }
                }
            }

            gameModel.getDistanceToSomething(true, 40, 48, -50, -10, -50);
            landscapeModels = tmpModel.createModelArray(0, 0, 1536, 1536, 8, 64, 233, false);
            for (int j6 = 0; j6 < 64; j6++) {
                scene.addModel(landscapeModels[j6]);
            }

            for (int i9 = 0; i9 < 96; i9++) {
                for (int k11 = 0; k11 < 96; k11++) {
                    anIntArrayArray581[i9][k11] = getGroundElevation(i9, k11);
                }
            }
        }

        tmpModel.clear();
        
        /*
         * Load walls
         */
        
        for (int i = 0; i < 95; i++) {
            for (int k2 = 0; k2 < 95; k2++) {
                int k3 = getVerticalWall(i, k2);
                if (k3 > 0 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    createWall(tmpModel, k3 - 1, i, k2, i + 1, k2);
                }
                k3 = getHorizontalWall(i, k2);
                if (k3 > 0 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    createWall(tmpModel, k3 - 1, i, k2, i, k2 + 1);
                }
                k3 = getDiagonalWalls(i, k2);
                if (k3 > 0 && k3 < 12000 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    createWall(tmpModel, k3 - 1, i, k2, i + 1, k2 + 1);
                }
                if (k3 > 12000 && k3 < 24000 && Resources.getDoorDef(k3 - 12001).getUnknown() == 0) {
                    createWall(tmpModel, k3 - 12001, i + 1, k2, i, k2 + 1);
                }
            }
        }

        tmpModel.getDistanceToSomething(false, 60, 24, -50, -10, -50);
        wallModels[layer] = tmpModel.createModelArray(0, 0, 1536, 1536, 8, 64, 338, true);
        for (int l2 = 0; l2 < 64; l2++) {
            scene.addModel(wallModels[layer][l2]);
        }
        

        // Raise wall heights
        for (int l3 = 0; l3 < 95; l3++) {
            for (int l4 = 0; l4 < 95; l4++) {
                int k6 = getVerticalWall(l3, l4);
                if (k6 > 0) {
                    method403(k6 - 1, l3, l4, l3 + 1, l4);
                }
                k6 = getHorizontalWall(l3, l4);
                if (k6 > 0) {
                    method403(k6 - 1, l3, l4, l3, l4 + 1);
                }
                k6 = getDiagonalWalls(l3, l4);
                if (k6 > 0 && k6 < 12000) {
                    method403(k6 - 1, l3, l4, l3 + 1, l4 + 1);
                }
                if (k6 > 12000 && k6 < 24000) {
                    method403(k6 - 12001, l3 + 1, l4, l3, l4 + 1);
                }
            }
        }

        for (int i5 = 1; i5 < 95; i5++) {
            for (int l6 = 1; l6 < 95; l6++) {
                int j9 = getRoofTexture(i5, l6);
                if (j9 > 0) {
                    int l11 = i5;
                    int i14 = l6;
                    int j16 = i5 + 1;
                    int k18 = l6;
                    int j19 = i5 + 1;
                    int j21 = l6 + 1;
                    int l22 = i5;
                    int j23 = l6 + 1;
                    int l23 = 0;
                    int j24 = anIntArrayArray581[l11][i14];
                    int l24 = anIntArrayArray581[j16][k18];
                    int j25 = anIntArrayArray581[j19][j21];
                    int l25 = anIntArrayArray581[l22][j23];
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
                        anIntArrayArray581[l11][i14] = l23;
                    } else {
                        anIntArrayArray581[l11][i14] -= 0x13880;
                    }
                    if (l24 < 0x13880) {
                        anIntArrayArray581[j16][k18] = l23;
                    } else {
                        anIntArrayArray581[j16][k18] -= 0x13880;
                    }
                    if (j25 < 0x13880) {
                        anIntArrayArray581[j19][j21] = l23;
                    } else {
                        anIntArrayArray581[j19][j21] -= 0x13880;
                    }
                    if (l25 < 0x13880) {
                        anIntArrayArray581[l22][j23] = l23;
                    } else {
                        anIntArrayArray581[l22][j23] -= 0x13880;
                    }
                }
            }
        }

        tmpModel.clear();
        for (int i7 = 1; i7 < 95; i7++) {
            for (int k9 = 1; k9 < 95; k9++) {
                int i12 = getRoofTexture(i7, k9);
                if (i12 > 0) {
                    int j14 = i7;
                    int k16 = k9;
                    int l18 = i7 + 1;
                    int k19 = k9;
                    int k21 = i7 + 1;
                    int i23 = k9 + 1;
                    int k23 = i7;
                    int i24 = k9 + 1;
                    int k24 = i7 * 128;
                    int i25 = k9 * 128;
                    int k25 = k24 + 128;
                    int i26 = i25 + 128;
                    int j26 = k24;
                    int k26 = i25;
                    int l26 = k25;
                    int i27 = i26;
                    int j27 = anIntArrayArray581[j14][k16];
                    int k27 = anIntArrayArray581[l18][k19];
                    int l27 = anIntArrayArray581[k21][i23];
                    int i28 = anIntArrayArray581[k23][i24];
                    int j28 = Resources.getElevationDef(i12 - 1).getUnknown1();
                    if (isCentreRoof(j14, k16) && j27 < 0x13880) {
                        j27 += j28 + 0x13880;
                        anIntArrayArray581[j14][k16] = j27;
                    }
                    if (isCentreRoof(l18, k19) && k27 < 0x13880) {
                        k27 += j28 + 0x13880;
                        anIntArrayArray581[l18][k19] = k27;
                    }
                    if (isCentreRoof(k21, i23) && l27 < 0x13880) {
                        l27 += j28 + 0x13880;
                        anIntArrayArray581[k21][i23] = l27;
                    }
                    if (isCentreRoof(k23, i24) && i28 < 0x13880) {
                        i28 += j28 + 0x13880;
                        anIntArrayArray581[k23][i24] = i28;
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
                    if (getDiagonalWalls(i7, k9) > 12000 && getDiagonalWalls(i7, k9) < 24000
                            && getRoofTexture(i7 - 1, k9 - 1) == 0) {
                        int ai8[] = new int[3];
                        ai8[0] = tmpModel.getSomeIndex(l26, l27, i26);
                        ai8[1] = tmpModel.getSomeIndex(j26, i28, i27);
                        ai8[2] = tmpModel.getSomeIndex(k25, k27, k26);
                        tmpModel.createFace(3, ai8, i12, 0xbc614e);
                    } else if (getDiagonalWalls(i7, k9) > 12000 && getDiagonalWalls(i7, k9) < 24000
                            && getRoofTexture(i7 + 1, k9 + 1) == 0) {
                        int ai9[] = new int[3];
                        ai9[0] = tmpModel.getSomeIndex(k24, j27, i25);
                        ai9[1] = tmpModel.getSomeIndex(k25, k27, k26);
                        ai9[2] = tmpModel.getSomeIndex(j26, i28, i27);
                        tmpModel.createFace(3, ai9, i12, 0xbc614e);
                    } else if (getDiagonalWalls(i7, k9) > 0 && getDiagonalWalls(i7, k9) < 12000
                            && getRoofTexture(i7 + 1, k9 - 1) == 0) {
                        int ai10[] = new int[3];
                        ai10[0] = tmpModel.getSomeIndex(j26, i28, i27);
                        ai10[1] = tmpModel.getSomeIndex(k24, j27, i25);
                        ai10[2] = tmpModel.getSomeIndex(l26, l27, i26);
                        tmpModel.createFace(3, ai10, i12, 0xbc614e);
                    } else if (getDiagonalWalls(i7, k9) > 0 && getDiagonalWalls(i7, k9) < 12000
                            && getRoofTexture(i7 - 1, k9 + 1) == 0) {
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
                        if (getRoofTexture(i7 - 1, k9 - 1) > 0) {
                            flag1 = false;
                        }
                        if (getRoofTexture(i7 + 1, k9 + 1) > 0) {
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
        
        // Raise roof heights
        for (int j12 = 0; j12 < 96; j12++) {
            for (int k14 = 0; k14 < 96; k14++) {
                if (anIntArrayArray581[j12][k14] >= 0x13880) {
                    anIntArrayArray581[j12][k14] -= 0x13880;
                }
            }
        }
    }

    public int getRoofTexture(int i, int j) {
        if (i < 0 || i >= 96 || j < 0 || j >= 96) {
            return 0;
        }
        byte byte0 = 0;
        if (i >= 48 && j < 48) {
            byte0 = 1;
            i -= 48;
        } else if (i < 48 && j >= 48) {
            byte0 = 2;
            j -= 48;
        } else if (i >= 48 && j >= 48) {
            byte0 = 3;
            i -= 48;
            j -= 48;
        }
        return sectors[byte0].getTile(i, j).roofTexture;
    }

    private void createWall(GameModel gameModel, int i, int j, int k, int l, int i1) {
        method419(j, k, 40);
        method419(l, i1, 40);
        int j1 = Resources.getDoorDef(i).getModelVar1();
        int k1 = Resources.getDoorDef(i).getModelVar2();
        int l1 = Resources.getDoorDef(i).getModelVar3();
        int i2 = j * 128;
        int j2 = k * 128;
        int k2 = l * 128;
        int l2 = i1 * 128;
        int i3 = gameModel.getSomeIndex(i2, -anIntArrayArray581[j][k], j2);
        int j3 = gameModel.getSomeIndex(i2, -anIntArrayArray581[j][k] - j1, j2);
        int k3 = gameModel.getSomeIndex(k2, -anIntArrayArray581[l][i1] - j1, l2);
        int l3 = gameModel.getSomeIndex(k2, -anIntArrayArray581[l][i1], l2);
        int i4 = gameModel.createFace(4, new int[] { i3, j3, k3, l3 }, k1, l1);
        if (Resources.getDoorDef(i).getUnknown() == 5) {
            gameModel.faceTag[i4] = 30000 + i;
        } else {
            gameModel.faceTag[i4] = 0;
        }
    }

    private void method419(int i, int j, int k) {
        int l = i / 12;
        int i1 = j / 12;
        int j1 = (i - 1) / 12;
        int k1 = (j - 1) / 12;
        method402(l, i1, i, j, k);
        if (l != j1) {
            method402(j1, i1, i, j, k);
        }
        if (i1 != k1) {
            method402(l, k1, i, j, k);
        }
        if (l != j1 && i1 != k1) {
            method402(j1, k1, i, j, k);
        }
    }

    private void method402(int i, int j, int k, int l, int i1) {
        GameModel gameModel = landscapeModels[i + j * 8];
        for (int j1 = 0; j1 < gameModel.someCount; j1++) {
            if (gameModel.vertexX[j1] == k * 128 && gameModel.vertexY[j1] == l * 128) {
                gameModel.setByteAtIndexToValue(j1, i1);
                return;
            }
        }
    }

    private int getVerticalWall(int i, int j) {
        if (i < 0 || i >= 96 || j < 0 || j >= 96) {
            return 0;
        }
        byte byte0 = 0;
        if (i >= 48 && j < 48) {
            byte0 = 1;
            i -= 48;
        } else if (i < 48 && j >= 48) {
            byte0 = 2;
            j -= 48;
        } else if (i >= 48 && j >= 48) {
            byte0 = 3;
            i -= 48;
            j -= 48;
        }
        return sectors[byte0].getTile(i, j).verticalWall & 0xff;
    }

    private int getHorizontalWall(int i, int j) {
        if (i < 0 || i >= 96 || j < 0 || j >= 96) {
            return 0;
        }
        byte byte0 = 0;
        if (i >= 48 && j < 48) {
            byte0 = 1;
            i -= 48;
        } else if (i < 48 && j >= 48) {
            byte0 = 2;
            j -= 48;
        } else if (i >= 48 && j >= 48) {
            byte0 = 3;
            i -= 48;
            j -= 48;
        }
        return sectors[byte0].getTile(i, j).horizontalWall & 0xff;
    }

    private int getDiagonalWalls(int i, int j) {
        if (i < 0 || i >= 96 || j < 0 || j >= 96) {
            return 0;
        }
        byte byte0 = 0;
        if (i >= 48 && j < 48) {
            byte0 = 1;
            i -= 48;
        } else if (i < 48 && j >= 48) {
            byte0 = 2;
            j -= 48;
        } else if (i >= 48 && j >= 48) {
            byte0 = 3;
            i -= 48;
            j -= 48;
        }
        return sectors[byte0].getTile(i, j).diagonalWalls;
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

    private int getGroundTexture(int i, int j) {
        if (i < 0 || i >= 96 || j < 0 || j >= 96) {
            return 0;
        }
        byte byte0 = 0;
        if (i >= 48 && j < 48) {
            byte0 = 1;
            i -= 48;
        } else if (i < 48 && j >= 48) {
            byte0 = 2;
            j -= 48;
        } else if (i >= 48 && j >= 48) {
            byte0 = 3;
            i -= 48;
            j -= 48;
        }
        return sectors[byte0].getTile(i, j).groundTexture & 0xFF;
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

    private int getGroundElevation(int i, int j) {
        if (i < 0 || i >= 96 || j < 0 || j >= 96) {
            return 0;
        }
        byte byte0 = 0;
        if (i >= 48 && j < 48) {
            byte0 = 1;
            i -= 48;
        } else if (i < 48 && j >= 48) {
            byte0 = 2;
            j -= 48;
        } else if (i >= 48 && j >= 48) {
            byte0 = 3;
            i -= 48;
            j -= 48;
        }
        return (sectors[byte0].getTile(i, j).groundElevation & 0xff) * 3;
    }

    public void method403(int i, int j, int k, int l, int i1) {
        int j1 = Resources.getDoorDef(i).getModelVar1();
        if (anIntArrayArray581[j][k] < 0x13880) {
            anIntArrayArray581[j][k] += 0x13880 + j1;
        }
        if (anIntArrayArray581[l][i1] < 0x13880) {
            anIntArrayArray581[l][i1] += 0x13880 + j1;
        }
    }

    private void setGroundTexturesOverlay() {
        for (int i = 0; i < 96; i++) {
            for (int j = 0; j < 96; j++) {
                if (getGroundTexturesOverlay(i, j) == 250) {
                    if (i == 47 && getGroundTexturesOverlay(i + 1, j) != 250
                            && getGroundTexturesOverlay(i + 1, j) != 2) {
                        setGroundTexturesOverlay(i, j, 9);
                    } else if (j == 47 && getGroundTexturesOverlay(i, j + 1) != 250
                            && getGroundTexturesOverlay(i, j + 1) != 2) {
                        setGroundTexturesOverlay(i, j, 9);
                    } else {
                        setGroundTexturesOverlay(i, j, 2);
                    }
                }
            }
        }
    }

    private void setGroundTexturesOverlay(int i, int j, int k) {
        if (i < 0 || i >= 96 || j < 0 || j >= 96) {
            return;
        }
        byte byte0 = 0;
        if (i >= 48 && j < 48) {
            byte0 = 1;
            i -= 48;
        } else if (i < 48 && j >= 48) {
            byte0 = 2;
            j -= 48;
        } else if (i >= 48 && j >= 48) {
            byte0 = 3;
            i -= 48;
            j -= 48;
        }
        sectors[byte0].getTile(i, j).groundOverlay = (byte) k;
    }

    private int getGroundTexturesOverlay(int i, int j) {
        if (i < 0 || i >= 96 || j < 0 || j >= 96) {
            return 0;
        }
        byte byte0 = 0;
        if (i >= 48 && j < 48) {
            byte0 = 1;
            i -= 48;
        } else if (i < 48 && j >= 48) {
            byte0 = 2;
            j -= 48;
        } else if (i >= 48 && j >= 48) {
            byte0 = 3;
            i -= 48;
            j -= 48;
        }
        return sectors[byte0].getTile(i, j).groundOverlay & 0xff;
    }

    public int getAveragedElevation(int i, int j) {
        int k = i >> 7;
        int l = j >> 7;
        int i1 = i & 0x7f;
        int j1 = j & 0x7f;
        if (k < 0 || l < 0 || k >= 95 || l >= 95) {
            return 0;
        }
        int k1;
        int l1;
        int i2;
        if (i1 <= 128 - j1) {
            k1 = getGroundElevation(k, l);
            l1 = getGroundElevation(k + 1, l) - k1;
            i2 = getGroundElevation(k, l + 1) - k1;
        } else {
            k1 = getGroundElevation(k + 1, l + 1);
            l1 = getGroundElevation(k, l + 1) - k1;
            i2 = getGroundElevation(k + 1, l) - k1;
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
