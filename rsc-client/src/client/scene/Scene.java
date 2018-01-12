package client.scene;

/**
 * A collection of models and sprites along with lighting settings, ready for
 * rendering.
 * 
 * @author Dan Bryce
 */
public class Scene {

    private static final int MAX_MODELS = 15000;
    private static final int MAX_SPRITES = 1000;

    private GameModel view;
    private Camera camera;
    private int numModels;
    private GameModel models[] = new GameModel[MAX_MODELS];
    private int numSprites;
    private SpriteEntity spriteEntities[] = new SpriteEntity[MAX_SPRITES];

    public int clipX;
    public int clipY;
    public int clipNear = 5;
    
    /**
     * View distance for 3d models.
     */
    public int clipFar3d = 2200 + (Camera.DEFAULT_HEIGHT * 2);
    
    /**
     * View distance for sprites.
     */
    public int clipFar2d = 2200 + (Camera.DEFAULT_HEIGHT * 2);
    
    /**
     * Fog "density".
     */
    public int fogZFalloff = 1;
    
    public int fogZDistance = 2100 + (Camera.DEFAULT_HEIGHT * 2);
    
    public Scene() {
        for (int l = 0; l < spriteEntities.length; l++) {
            spriteEntities[l] = new SpriteEntity();
        }
        view = new GameModel(MAX_SPRITES * 2, MAX_SPRITES);
        camera = new Camera();
        
        camera.setCamera(0, 0, 0, 912, 0, 0, 2000);
        
        setLight(-50, -10, -50);
    }

    public void addModel(GameModel gameModel) {
        if (gameModel == null) {
            System.out.println("Warning tried to add null object!");
        }
        if (numModels < MAX_MODELS) {
            models[numModels] = gameModel;
            numModels++;
        }
    }

    public void removeModel(GameModel gameModel) {
        for (int i = 0; i < numModels; i++) {
            if (models[i] == gameModel) {
                numModels--;
                for (int j = i; j < numModels; j++) {
                    models[j] = models[j + 1];
                }

            }
        }
    }

    public void dispose() {
        clear();
        for (int i = 0; i < numModels; i++) {
            models[i] = null;
        }
        numModels = 0;
    }

    public void clear() {
        numSprites = 0;
        view.clear();
    }

    public void reduceSprites(int i) {
        numSprites -= i;
        view.reduceCounters(i, i * 2);
        if (numSprites < 0) {
            numSprites = 0;
        }
    }

    public int addSpriteEntity(SpriteEntity spriteEntity, int tag) {
        spriteEntities[numSprites] = spriteEntity;
        int v1 = view.createVertex(
                spriteEntity.getX(),
                spriteEntity.getY(),
                spriteEntity.getZ());
        int v2 = view.createVertex(
                spriteEntity.getX(),
                spriteEntity.getZ() - spriteEntity.getHeight(),
                spriteEntity.getY());
        int vertices[] = { v1, v2 };
        view.createFace(2, vertices, 0, 0);
        view.faceTag[numSprites] = tag;
        numSprites++;
        return numSprites - 1;
    }

    public void setLight(int distX, int distY, int distZ) {
        if (distX == 0 && distY == 0 && distZ == 0) {
            distX = 32;
        }
        for (int l = 0; l < numModels; l++) {
            models[l].setLight(distX, distY, distZ);
        }

    }

    public void setLight(int i, int j, int distX, int distY, int distZ) {
        if (distX == 0 && distY == 0 && distZ == 0) {
            distX = 32;
        }
        for (int j1 = 0; j1 < numModels; j1++) {
            models[j1].setLight(i, j, distX, distY, distZ);
        }

    }

    public GameModel getView() {
        return view;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public int getNumModels() {
        return numModels;
    }
    
    public GameModel[] getModels() {
        return models;
    }
    
    public SpriteEntity[] getSpriteEntities() {
        return spriteEntities;
    }

}
