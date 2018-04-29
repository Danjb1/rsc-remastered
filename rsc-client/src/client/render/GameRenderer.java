package client.render;

import client.Canvas;
import client.RsLauncher;
import client.scene.Camera;
import client.scene.Scene;
import client.states.Game;
import client.world.Door;
import client.world.World;

/**
 * Class responsible for rendering the game.
 * 
 * <p>The general idea is:
 * 
 * <ul>
 *  <li>Build the scene</li>
 *  <li>Tell the SceneRenderer to render the scene</li>
 *  <li>Draw the UI on top</li>
 * </ul>
 * 
 * @author Dan Bryce
 */
public class GameRenderer {

    private Game game;
    private World world;
    private Scene scene;
    private Camera camera;

    private SceneRenderer sceneRenderer;
    private MousePicker mousePicker;
    
    public GameRenderer(Game game) {
        this.game = game;
        
        world = game.getWorld();
        scene = game.getScene();
        camera = scene.getCamera();
        
        sceneRenderer = new SceneRenderer(
                scene,
                RsLauncher.WINDOW_WIDTH,
                RsLauncher.WINDOW_HEIGHT);
        mousePicker = sceneRenderer.getMousePicker();
    }
    
    public void render(Canvas canvas) {
        buildScene();
        updateCamera();

        mousePicker.setMousePos(game.getMouseX(), game.getMouseY());
        sceneRenderer.render(canvas);
    }

    private void buildScene() {

        int layer = world.getCurrentLayer();

        /*
         * Buildings
         */
        for (int i = 0; i < 64; i++) {
            
            scene.removeModel(world.getRoofModel(layer, i));
            if (layer == 0) {
                // Remove roofs from upper storeys
                scene.removeModel(world.getWallModel(1, i));
                scene.removeModel(world.getRoofModel(1, i));
                scene.removeModel(world.getWallModel(2, i));
                scene.removeModel(world.getRoofModel(2, i));

                // Add roof of current layer
                // TODO: Don't add roofs or upper storeys if player is indoors!
                scene.addModel(world.getRoofModel(layer, i));
                
                // Add upper storeys
                scene.addModel(world.getWallModel(1, i));
                scene.addModel(world.getRoofModel(1, i));
                scene.addModel(world.getWallModel(2, i));
                scene.addModel(world.getRoofModel(2, i));
            }
        }

        /*
         * Doors
         */
        for (int i = 0; i < world.getNumDoors(); i++) {
            Door door = world.getDoor(i);
            if (world.containsTile(door.getX(), door.getZ())) {
                scene.addModel(door.getModel());
            }
        }
    }

    private void updateCamera() {

        int x = game.getCurrentPlayer().x + game.getScreenRotationX();
        int z = game.getCurrentPlayer().z + game.getScreenRotationZ();
        int y = -world.getAveragedElevation(x, z);
        
        int pitch = 912;
        int yaw = game.getCameraRotation() * 4;
        int roll = 0;
        int height = game.getCameraHeight() * 2;
        
        camera.setCamera(x, y, z, pitch, yaw, roll, height);

        // Update fog distance based on camera height
        scene.fogZDistance = 2300 + (game.getCameraHeight() * 2);
    }

    public MousePicker getMousePicker() {
        return mousePicker;
    }
    
}
