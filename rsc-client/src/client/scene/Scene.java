package client.scene;

public class Scene {

    public static final int MIN_HEIGHT = 300;
    public static final int MAX_HEIGHT = 1500;
    public static final int ZOOM_INCREMENT = 25;
    public static final int DEFAULT_HEIGHT = 550;

    private int modelCount;
    private int maxModels;
    private GameModel models[];
    private int modelState[];
    private Polygon visiblePolygons[];
    private int spriteCount;
    private int spriteId[];
    private int spriteX[];
    private int spriteZ[];
    private int spriteY[];
    private int spriteWidth[];
    private int spriteHeight[];
    private int spriteTranslateX[];
    private GameModel view;
    
    public Scene(int modelLimit, int polygonLimit, int spriteLimit) {
        maxModels = modelLimit;
        models = new GameModel[maxModels];
        modelState = new int[maxModels]; // Only set, not used
        visiblePolygons = new Polygon[polygonLimit];
        for (int l = 0; l < polygonLimit; l++) {
            visiblePolygons[l] = new Polygon();
        }
        view = new GameModel(spriteLimit * 2, spriteLimit);
        spriteId = new int[spriteLimit];
        spriteWidth = new int[spriteLimit];
        spriteHeight = new int[spriteLimit];
        spriteX = new int[spriteLimit];
        spriteY = new int[spriteLimit];
        spriteZ = new int[spriteLimit];
        spriteTranslateX = new int[spriteLimit];
    }

    public void addModel(GameModel gameModel) {
        if (gameModel == null) {
            System.out.println("Warning tried to add null object!");
        }
        if (modelCount < maxModels) {
            modelState[modelCount] = 0;
            models[modelCount++] = gameModel;
        }
    }

    public void removeModel(GameModel gameModel) {
        for (int i = 0; i < modelCount; i++) {
            if (models[i] == gameModel) {
                modelCount--;
                for (int j = i; j < modelCount; j++) {
                    models[j] = models[j + 1];
                    modelState[j] = modelState[j + 1];
                }

            }
        }
    }

    public void dispose() {
        clear();
        for (int i = 0; i < modelCount; i++) {
            models[i] = null;
        }
        modelCount = 0;
    }

    public void clear() {
        spriteCount = 0;
        view.clear();
    }

    public void reduceSprites(int i) {
        spriteCount -= i;
        view.reduceCounters(i, i * 2);
        if (spriteCount < 0) {
            spriteCount = 0;
        }
    }

    public int addSprite(int i, int x, int z, int y, int width, int height, int tag) {
        spriteId[spriteCount] = i;
        spriteX[spriteCount] = x;
        spriteZ[spriteCount] = z;
        spriteY[spriteCount] = y;
        spriteWidth[spriteCount] = width;
        spriteHeight[spriteCount] = height;
        spriteTranslateX[spriteCount] = 0;
        int l1 = view.createVertex(x, z, y);
        int i2 = view.createVertex(x, z - height, y);
        int ai[] = { l1, i2 };
        view.createFace(2, ai, 0, 0);
        view.faceTag[spriteCount] = tag;
        view.isLocalPlayer[spriteCount++] = 0;
        return spriteCount - 1;
    }

    public void setLocalPlayer(int i) {
        view.isLocalPlayer[i] = 1;
    }

    public void setSpriteTranslateX(int i, int val) {
        spriteTranslateX[i] = val;
    }

}
