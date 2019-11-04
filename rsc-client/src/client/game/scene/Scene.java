package client.game.scene;

/**
 * A collection of models and sprites along with lighting settings, ready for
 * rendering.
 *
 * @author Dan Bryce
 */
public class Scene {

    private static final int MAX_MODELS = 15000;
    private static final int MAX_SPRITES = 1000;

    private Model sprites;
    private Camera camera;
    private int numModels;
    private Model models[] = new Model[MAX_MODELS];
    private int numSprites;
    private SpriteEntity spriteEntities[] = new SpriteEntity[MAX_SPRITES];

    /**
     * Fog "density".
     */
    public int fogZFalloff = 1;

    public int fogZDistance = 2100 + (Camera.DEFAULT_HEIGHT * 2);

    public Scene() {
        for (int l = 0; l < spriteEntities.length; l++) {
            spriteEntities[l] = new SpriteEntity();
        }
        sprites = new Model(MAX_SPRITES * 2, MAX_SPRITES);
        camera = new Camera();

        camera.set(0, 0, 0, 912, 0, 0, 2000);

        setLight(-50, -10, -50);
    }

    public void addModel(Model gameModel) {
        if (gameModel == null) {
            System.out.println("WARNING: Tried to add null object");
            return;
        }
        if (numModels < MAX_MODELS) {
            models[numModels] = gameModel;
            numModels++;
        }
    }

    public void removeModel(Model gameModel) {
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
        sprites.clear();
    }

    public void reduceSprites(int i) {
        numSprites -= i;
        sprites.removeGeometry(i, i * 2);
        if (numSprites < 0) {
            numSprites = 0;
        }
    }

    public int addSpriteEntity(SpriteEntity spriteEntity, int tag) {
        spriteEntities[numSprites] = spriteEntity;
        int v1 = sprites.addVertex(
                spriteEntity.getX(),
                spriteEntity.getY(),
                spriteEntity.getZ());
        int v2 = sprites.addVertex(
                spriteEntity.getX(),
                spriteEntity.getZ() - spriteEntity.getHeight(),
                spriteEntity.getY());
        int vertices[] = { v1, v2 };
        sprites.addFace(2, vertices, 0, 0);
        sprites.faceTag[numSprites] = tag;
        numSprites++;
        return numSprites - 1;
    }

    public void setLight(int distX, int distY, int distZ) {
        if (distX == 0 && distY == 0 && distZ == 0) {
            distX = 32;
        }
        for (int l = 0; l < numModels; l++) {
            models[l].setLighting(distX, distY, distZ);
        }

    }

    public void setLight(int i, int j, int distX, int distY, int distZ) {
        if (distX == 0 && distY == 0 && distZ == 0) {
            distX = 32;
        }
        for (int j1 = 0; j1 < numModels; j1++) {
            models[j1].setLighting(i, j, distX, distY, distZ);
        }

    }

    public Model getSprites() {
        return sprites;
    }

    public Camera getCamera() {
        return camera;
    }

    public int getNumModels() {
        return numModels;
    }

    public Model[] getModels() {
        return models;
    }

    public SpriteEntity[] getSpriteEntities() {
        return spriteEntities;
    }

}
