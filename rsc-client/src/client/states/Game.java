package client.states;

import java.awt.Graphics;

import client.Canvas;
import client.RsLauncher;
import client.State;
import client.World;
import client.model.Mob;
import client.render.GameRenderer;
import client.render.SceneRenderer;
import client.res.Resources;
import client.scene.Camera;
import client.scene.GameModel;
import client.scene.Scene;

/**
 * State responsible for running the game.
 * 
 * <p><i>Based on <code>mudclient.java</code> from other RSC sources.</i>
 * 
 * @author Dan Bryce
 */
public class Game extends State {

    private static final int MIN_DOOR_ID = 10000;
    
    private World world;
    private Scene scene;
    private Mob player;

    private LoadingScreen loadingScreen;
    private LoginScreen loginScreen;
    private SceneRenderer sceneRenderer;

    private int cameraRotation = 128;
    private int screenRotationX;
    private int screenRotationY;
    private int cameraHeight = Camera.DEFAULT_HEIGHT;
    
    private int regionX;
    private int regionZ;
    private int layer = 0;
    
    private int planeWidth = 2304;
    private int planeDepth = 1776;
    private int planeIndex = 0;
    
    private int mapBoundaryX1;
    private int mapBoundaryZ1;
    private int mapBoundaryX2;
    private int mapBoundaryZ2;

    private int objectCount;
    private int objectX[] = new int[1500];
    private int objectZ[] = new int[1500];
    private int objectTypes[] = new int[1500];
    private int objectID[] = new int[1500];
    private GameModel objectModels[] = new GameModel[1500];
    private int magicLoc = 128;

    private int wallObjectCount;
    private int wallObjectX[] = new int[500];
    private int wallObjectZ[] = new int[500];
    private int wallObjectDirection[] = new int[500];
    private int wallObjectId[] = new int[500];
    private GameModel wallObjectModels[] = new GameModel[500];

    public Game() {
        player = new Mob();
        player.x = 8512;
        player.z = 4160;
        scene = new Scene();
        sceneRenderer = new SceneRenderer(
                scene,
                RsLauncher.WINDOW_WIDTH,
                RsLauncher.WINDOW_HEIGHT);
        world = new World(scene);
        loadNextRegion(114, 656);
    }
    
    @Override
    public void pollInput() {

        // Get mouse-picked models / faces from the rendered scene
        int mousePickedCount = sceneRenderer.getMousePickedCount();
        GameModel mousePickedModels[] = sceneRenderer.getMousePickedModels();
        int mousePickedFaces[] = sceneRenderer.getMousePickedFaces();
        
        int selectedGroundFaceId = -1;

        for (int i = 0; i < mousePickedCount; i++) {
            int faceId = mousePickedFaces[i];
            GameModel gameModel = mousePickedModels[i];

            if (faceId >= 0) {
                faceId = gameModel.faceTag[faceId] - 200000;
            }
            
            if (faceId >= 0) {
                selectedGroundFaceId = faceId;
            }
        }

        if (selectedGroundFaceId != -1) {
            int tileX = world.getTileXForFace(selectedGroundFaceId);
            int tileZ = world.getTileZForFace(selectedGroundFaceId);
            groundTileSelected(tileX, tileZ);
        }

        // Update mouse picking position for the next frame
        sceneRenderer.setMousePos(mouseX, mouseY);
    }
    
    private void groundTileSelected(int tileX, int tileZ) {
        if (wasLeftClickReleased()) {
            System.out.println("clicked on tile: " + tileX + ", " + tileZ);
            player.x = tileX * World.TILE_WIDTH;
            player.z = tileZ * World.TILE_DEPTH;
        }
    }

    @Override
    public void tick() {
    }

    public LoadingScreen getLoadingScreen() {
        return loadingScreen;
    }

    public LoginScreen getLoginScreen() {
        return loginScreen;
    }

    private boolean loadNextRegion(int x, int z) {
        
        x += planeWidth;
        z += planeDepth;
        
        if (layer == planeIndex &&
                x > mapBoundaryX1 && x < mapBoundaryX2 &&
                z > mapBoundaryZ1 && z < mapBoundaryZ2) {
            // No need to load region if already loaded
            return false;
        }
        
        int k = regionX;
        int l = regionZ;
        int i1 = (x + 24) / 48;
        int j1 = (z + 24) / 48;
        layer = planeIndex;
        regionX = i1 * 48 - 48;
        regionZ = j1 * 48 - 48;
        
        // Set map boundary around the loaded region
        mapBoundaryX1 = i1 * 48 - 32;
        mapBoundaryZ1 = j1 * 48 - 32;
        mapBoundaryX2 = i1 * 48 + 32;
        mapBoundaryZ2 = j1 * 48 + 32;
        
        world.loadRegion(x, z, layer);
        
        regionX -= planeWidth;
        regionZ -= planeDepth;
        int k1 = regionX - k;
        int l1 = regionZ - l;
        
        // Add object models to scene
        for (int i2 = 0; i2 < objectCount; i2++) {
            objectX[i2] -= k1;
            objectZ[i2] -= l1;
            int j2 = objectX[i2];
            int l2 = objectZ[i2];
            int k3 = objectTypes[i2];
            GameModel gameModel = objectModels[i2];
            try {
                int l4 = objectID[i2];
                int k5;
                int i6;
                if (l4 == 0 || l4 == 4) {
                    k5 = Resources.getObjectDef(k3).getWidth();
                    i6 = Resources.getObjectDef(k3).getHeight();
                } else {
                    i6 = Resources.getObjectDef(k3).getWidth();
                    k5 = Resources.getObjectDef(k3).getHeight();
                }
                int j6 = ((j2 + j2 + k5) * magicLoc) / 2;
                int k6 = ((l2 + l2 + i6) * magicLoc) / 2;
                if (j2 >= 0 && l2 >= 0 && j2 < 96 && l2 < 96) {
                    scene.addModel(gameModel);
                    gameModel.setVars(j6, -world.getAveragedElevation(j6, k6), k6);
                    if (k3 == 74) {
                        gameModel.modVars(0, -480, 0);
                    }
                }
            } catch (RuntimeException runtimeexception) {
                System.out.println("Loc Error: " + runtimeexception.getMessage());
                System.out.println("i:" + i2 + " obj:" + gameModel);
                runtimeexception.printStackTrace();
            }
        }

        // Create wall models
        for (int k2 = 0; k2 < wallObjectCount; k2++) {
            wallObjectX[k2] -= k1;
            wallObjectZ[k2] -= l1;
            int i3 = wallObjectX[k2];
            int l3 = wallObjectZ[k2];
            int j4 = wallObjectId[k2];
            int i5 = wallObjectDirection[k2];
            try {
                GameModel model_1 = createModel(i3, l3, i5, j4, k2);
                wallObjectModels[k2] = model_1;
            } catch (RuntimeException runtimeexception1) {
                System.out.println("Bound Error: " + runtimeexception1.getMessage());
                runtimeexception1.printStackTrace();
            }
        }

        return true;
    }

    public Scene getScene() {
        return scene;
    }
    
    public SceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }

    public World getWorld() {
        return world;
    }
    
    public int getLayer() {
        return layer;
    }

    public Mob getCurrentPlayer() {
        return player;
    }

    private GameModel createModel(int x, int z, int k, int l, int i1) {
        int modelX1 = x;
        int modelZ1 = z;
        int modelX2 = x;
        int modelZ2 = z;
        int j2 = Resources.getDoorDef(l).getModelVar2();
        int k2 = Resources.getDoorDef(l).getModelVar3();
        int l2 = Resources.getDoorDef(l).getModelVar1();
        GameModel gameModel = new GameModel(4, 1);
        if (k == 0) {
            modelX2 = x + 1;
        }
        if (k == 1) {
            modelZ2 = z + 1;
        }
        if (k == 2) {
            modelX1 = x + 1;
            modelZ2 = z + 1;
        }
        if (k == 3) {
            modelX2 = x + 1;
            modelZ2 = z + 1;
        }
        modelX1 *= magicLoc;
        modelZ1 *= magicLoc;
        modelX2 *= magicLoc;
        modelZ2 *= magicLoc;
        int i3 = gameModel.createVertexWithoutDuplication(modelX1, -world.getAveragedElevation(modelX1, modelZ1), modelZ1);
        int j3 = gameModel.createVertexWithoutDuplication(modelX1, -world.getAveragedElevation(modelX1, modelZ1) - l2, modelZ1);
        int k3 = gameModel.createVertexWithoutDuplication(modelX2, -world.getAveragedElevation(modelX2, modelZ2) - l2, modelZ2);
        int l3 = gameModel.createVertexWithoutDuplication(modelX2, -world.getAveragedElevation(modelX2, modelZ2), modelZ2);
        int vertices[] = { i3, j3, k3, l3 };
        gameModel.createFace(4, vertices, j2, k2);
        gameModel.recalculateLighting(false, 60, 24, -50, -10, -50);
        if (x >= 0 && z >= 0 && x < 96 && z < 96) {
            scene.addModel(gameModel);
        }
        gameModel.entityId = i1 + MIN_DOOR_ID;
        return gameModel;
    }

    public int getCameraHeight() {
        return cameraHeight;
    }
    
    public int getCameraRotation() {
        return cameraRotation;
    }
    
    public int getScreenRotationX() {
        return screenRotationX;
    }
    
    public int getScreenRotationZ() {
        return screenRotationY;
    }

    public void setCameraRotation(int cameraRotation) {
        this.cameraRotation = cameraRotation;
    }
    
    @Override
    public void render(Canvas canvas, Graphics g) {
        GameRenderer.render(this, canvas);
    }
    
}
