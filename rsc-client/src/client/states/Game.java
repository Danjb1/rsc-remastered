package client.states;

import java.awt.Graphics;

import client.Canvas;
import client.State;
import client.WorldLoader;
import client.model.Mob;
import client.render.GameRenderer;
import client.render.MousePicker;
import client.scene.Camera;
import client.scene.Model;
import client.scene.Scene;
import client.world.World;

/**
 * State responsible for running the game.
 * 
 * <p><i>Based on <code>mudclient.java</code> from other RSC sources.</i>
 * 
 * @author Dan Bryce
 */
public class Game extends State {

    /**
     * Object used to load the world.
     */
    private WorldLoader worldLoader;
    
    private World world;
    private Scene scene;
    private Mob player;

    private GameRenderer renderer;
    private LoadingScreen loadingScreen;
    private LoginScreen loginScreen;

    // 192 = West
    // 128 = North
    // 64  = East
    // 0   = South
    private int cameraRotation = 128;
    private int screenRotationX;
    private int screenRotationY;
    private int cameraHeight = Camera.DEFAULT_HEIGHT;
    
    public Game() {
        
        player = new Mob();
        player.x = 66 * World.TILE_WIDTH;
        player.z = 32 * World.TILE_DEPTH;
        
        scene = new Scene();
        
        world = new World(scene);
        worldLoader = new WorldLoader(world);
        worldLoader.loadRegion(114, 656);
        
        renderer = new GameRenderer(this);
    }
    
    @Override
    public void pollInput() {

        // Get mouse-picked models / faces from the rendered scene
        MousePicker mousePicker = renderer.getMousePicker();
        int mousePickedCount = mousePicker.getMousePickedCount();
        Model mousePickedModels[] = mousePicker.getMousePickedModels();
        int mousePickedFaces[] = mousePicker.getMousePickedFaces();
        
        int selectedGroundFaceId = -1;

        for (int i = 0; i < mousePickedCount; i++) {
            int faceId = mousePickedFaces[i];
            Model gameModel = mousePickedModels[i];

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
    }
    
    private void groundTileSelected(int tileX, int tileZ) {
        if (wasLeftClickReleased()) {
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

    public Scene getScene() {
        return scene;
    }
    
    public World getWorld() {
        return world;
    }
    
    public Mob getCurrentPlayer() {
        return player;
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
        renderer.render(canvas);
    }
    
}
