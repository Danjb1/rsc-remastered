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
    private GameModel aModel;
    private boolean requiresClean;

    private Sector[] sectors;

    private int[] groundTextureArray;
    private GameModel[] aModelArray596;
    private GameModel[][] aModelArrayArray580;
    private GameModel[][] aModelArrayArray598;
    private int[][] anIntArrayArray581;

    public World(Scene scene) {
        this.scene = scene;

        selectedX = new int[18432];
        selectedY = new int[18432];

        aModelArray596 = new GameModel[64];
        aModelArrayArray580 = new GameModel[4][64];
        aModelArrayArray598 = new GameModel[4][64];
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
            aModelArray596[i] = null;
            for (int k = 0; k < 4; k++) {
                aModelArrayArray598[k][i] = null;
            }
        }
        System.gc();
    }

    public void loadRegion(int x, int y) {
        garbageCollect();
        loadRegion(x, y, 0);
    }

    public void loadRegion(int x, int y, int height) {

        // Load Sectors
        sectors[0] = Resources.loadSector(x - 1, y - 1, 0);
        sectors[1] = Resources.loadSector(x, y - 1, 0);
        sectors[2] = Resources.loadSector(x - 1, y, 0);
        sectors[3] = Resources.loadSector(x, y, 0);
        
        setGroundTexturesOverlay();
        
        if (aModel == null) {
            aModel = new GameModel(18688, 18688, true, true, false, false, true);
        }
        
        GameModel gameModel = aModel;
        gameModel.clear();
        
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
                int l14 = 0;
                if (height == 1 || height == 2) {
                    k7 = 0xbc614e;
                    i10 = 0xbc614e;
                }
                if (getGroundTexturesOverlay(j3, j4) > 0) {
                    int l16 = getGroundTexturesOverlay(j3, j4);
                    int l5 = Resources.getTileDef(l16 - 1).getUnknown();
                    k7 = i10 = Resources.getTileDef(l16 - 1).getColour();
                    if (l5 == 4) {
                        k7 = 1;
                        i10 = 1;
                        if (l16 == 12) {
                            k7 = 31;
                            i10 = 31;
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

        gameModel.getDistanceToSomething(true, 40, 48, -50, -10, -50);
        aModelArray596 = aModel.createModelArray(0, 0, 1536, 1536, 8, 64, 233, false);
        for (int j6 = 0; j6 < 64; j6++) {
            scene.addModel(aModelArray596[j6]);
        }

        aModel.clear();
        
        /*
         * Load walls
         */
        
        for (int i = 0; i < 95; i++) {
            for (int k2 = 0; k2 < 95; k2++) {
                int k3 = getVerticalWall(i, k2);
                if (k3 > 0 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, i, k2, i + 1, k2);
                }
                k3 = getHorizontalWall(i, k2);
                if (k3 > 0 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, i, k2, i, k2 + 1);
                }
                k3 = getDiagonalWalls(i, k2);
                if (k3 > 0 && k3 < 12000 && Resources.getDoorDef(k3 - 1).getUnknown() == 0) {
                    method421(aModel, k3 - 1, i, k2, i + 1, k2 + 1);
                }
                if (k3 > 12000 && k3 < 24000 && Resources.getDoorDef(k3 - 12001).getUnknown() == 0) {
                    method421(aModel, k3 - 12001, i + 1, k2, i, k2 + 1);
                }
            }
        }

        aModel.getDistanceToSomething(false, 60, 24, -50, -10, -50);
        aModelArrayArray580[height] = aModel.createModelArray(0, 0, 1536, 1536, 8, 64, 338, true);
        for (int l2 = 0; l2 < 64; l2++) {
            scene.addModel(aModelArrayArray580[height][l2]);
        }
        
        aModel.clear();
        aModel.getDistanceToSomething(true, 50, 50, -50, -10, -50);
        aModelArrayArray598[height] = aModel.createModelArray(0, 0, 1536, 1536, 8, 64, 169, true);
    }

    public void method421(GameModel gameModel, int i, int j, int k, int l, int i1) {
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

    public void method419(int i, int j, int k) {
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

    public void method402(int i, int j, int k, int l, int i1) {
        GameModel gameModel = aModelArray596[i + j * 8];
        for (int j1 = 0; j1 < gameModel.someCount; j1++) {
            if (gameModel.vertexX[j1] == k * 128 && gameModel.vertexY[j1] == l * 128) {
                gameModel.setByteAtIndexToValue(j1, i1);
                return;
            }
        }
    }

    public int getVerticalWall(int i, int j) {
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

    public int getHorizontalWall(int i, int j) {
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

    public int getDiagonalWalls(int i, int j) {
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

    public int getTileDef(int x, int y) {
        int texture = getGroundTexturesOverlay(x, y);
        if (texture == 0) {
            return -1;
        }
        return Resources.getTileDef(texture - 1).getUnknown() != 2 ? 0 : 1;
    }

    public int getGroundTexture(int i, int j) {
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

    public int getGroundElevation(int i, int j) {
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

    public void setGroundTexturesOverlay() {
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

    public void setGroundTexturesOverlay(int i, int j, int k) {
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

    public int getGroundTexturesOverlay(int i, int j) {
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

}
