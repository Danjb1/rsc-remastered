package client.render;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import client.Canvas;
import client.Input;
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

    private static final int FOG_DISTANCE = 2300;
    
    private Game game;
    private Input input;
    private World world;
    private Scene scene;
    private Camera camera;

    private SceneRenderer sceneRenderer;
    private MousePicker mousePicker;

    private Canvas canvas;
    private BufferedImage image;

    public GameRenderer(Game game) {
        this.game = game;
        
        input = game.getInput();
        world = game.getWorld();
        scene = game.getScene();
        camera = scene.getCamera();
        
        int width = RsLauncher.WINDOW_WIDTH;
        int height = RsLauncher.WINDOW_HEIGHT;
        
        sceneRenderer = new SceneRenderer(scene, width, height);
        mousePicker = sceneRenderer.getMousePicker();

        canvas = new Canvas(width, height);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    public void render(Graphics g) {

        // Build the scene
        buildScene();
        updateCamera();

        // Prepare for mouse picking
        mousePicker.setMousePos(input.getMouseX(), input.getMouseY());
        
        // First render our scene to the Canvas
        canvas.clear();
        sceneRenderer.render(canvas);

        // Then copy the Canvas to an image
        image.setRGB(0, 0, image.getWidth(), image.getHeight(),
                canvas.getPixels(), 0, image.getWidth());
        
        // Finally, draw this image to the screen
        g.drawImage(image, 0, 0, null);
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
            if (world.containsTileRelativeToOrigin(door.getX(), door.getZ())) {
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
        scene.fogZDistance = FOG_DISTANCE + (game.getCameraHeight() * 2);
    }

    public MousePicker getMousePicker() {
        return mousePicker;
    }
    
}
