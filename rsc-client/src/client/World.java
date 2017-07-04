package client;

import client.model.Sector;
import client.render.SceneRenderer;
import client.res.Resources;
import client.scene.GameModel;
import client.scene.Scene;
import client.util.DataUtils;

public class World {

    public static final int COLOUR_TRANSPARENT = 12345678;

    private Scene scene;
    private SceneRenderer sceneRenderer;
    private GamePanel gamePanel;
    
    private int[] selectedX;
    private int[] selectedY;
    private GameModel aModel;
    private boolean requiresClean;

    private Sector[] sectors;

    private int[][] anIntArrayArray581;
    private GameModel[] aModelArray596;
    private int[] groundTextureArray;
    private GameModel[][] aModelArrayArray598;

    public World(Scene scene, SceneRenderer sceneRenderer, GamePanel gamePanel) {
        this.scene = scene;
        this.sceneRenderer = sceneRenderer;
        this.gamePanel = gamePanel;

        selectedX = new int[18432];
        selectedY = new int[18432];

        aModelArrayArray598 = new GameModel[4][64];
        anIntArrayArray581 = new int[96][96];
        requiresClean = true;
        aModelArray596 = new GameModel[64];
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

    public void loadRegion(int i, int j) {
        garbageCollect();
        loadRegion(i, j, true);
    }

    private void loadRegion(int i, int j, boolean flag) {
        
        // Load Sectors
        int l = (i + 24) / 48;
        int i1 = (j + 24) / 48;
        sectors[0] = Resources.loadSector(l - 1, i1 - 1, 0);
        sectors[1] = Resources.loadSector(l, i1 - 1, 0);
        sectors[2] = Resources.loadSector(l - 1, i1, 0);
        sectors[3] = Resources.loadSector(l, i1, 0);
        
        setGroundTexturesOverlay();
        
        if (aModel == null) {
            aModel = new GameModel(18688, 18688, true, true, false, false, true);
        }
        
        if (!flag) {
            aModel.clear();
            return;
        }
        
        GameModel gameModel = aModel;
        gameModel.clear();
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
                method413(j3, j4, l14, k7, i10);
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
                    method413(k4, i6, 0, l7, l7);
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
                        method413(k4, i6, 0, i8, i8);
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
                        method413(k4, i6, 0, j8, j8);
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
                        method413(k4, i6, 0, k8, k8);
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
                        method413(k4, i6, 0, l8, l8);
                    }
                }
            }
        }

        gameModel.getDistanceToSomething(true, 40, 48, -50, -10, -50);
        aModelArray596 = aModel.createModelArray(0, 0, 1536, 1536, 8, 64, 233, false);
        for (int j6 = 0; j6 < 64; j6++) {
            scene.addModel(aModelArray596[j6]);
        }
        for (int i9 = 0; i9 < 96; i9++) {
            for (int k11 = 0; k11 < 96; k11++) {
                anIntArrayArray581[i9][k11] = getGroundElevation(i9, k11);
            }
        }
        aModel.clear();
    }

    public void method413(int i, int j, int k, int l, int i1) {
        int j1 = i * 3;
        int k1 = j * 3;
        int l1 = sceneRenderer.method302(l);
        int i2 = sceneRenderer.method302(i1);
        l1 = l1 >> 1 & 0x7f7f7f;
        i2 = i2 >> 1 & 0x7f7f7f;
        if (k == 0) {
            gamePanel.drawLineX(j1, k1, 3, l1);
            gamePanel.drawLineX(j1, k1 + 1, 2, l1);
            gamePanel.drawLineX(j1, k1 + 2, 1, l1);
            gamePanel.drawLineX(j1 + 2, k1 + 1, 1, i2);
            gamePanel.drawLineX(j1 + 1, k1 + 2, 2, i2);
        } else if (k == 1) {
            gamePanel.drawLineX(j1, k1, 3, i2);
            gamePanel.drawLineX(j1 + 1, k1 + 1, 2, i2);
            gamePanel.drawLineX(j1 + 2, k1 + 2, 1, i2);
            gamePanel.drawLineX(j1, k1 + 1, 1, l1);
            gamePanel.drawLineX(j1, k1 + 2, 2, l1);
        }
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
