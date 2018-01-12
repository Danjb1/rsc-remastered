package client.render;

import client.Game;
import client.GamePanel;
import client.World;
import client.scene.Camera;
import client.scene.Scene;

public class GameRenderer {

    public static void render(GamePanel gamePanel, Game game) {
        
        World world = game.getWorld();
        Scene scene = game.getScene();
        int layer = game.getLayer();

        // Draw buildings
        for (int i = 0; i < 64; i++) {
            
            scene.removeModel(world.getRoofModel(layer, i));
            if (layer == 0) {
                // Remove roofs from upper storeys
                scene.removeModel(world.getWallModel(1, i));
                scene.removeModel(world.getRoofModel(1, i));
                scene.removeModel(world.getWallModel(2, i));
                scene.removeModel(world.getRoofModel(2, i));
            }
            
            scene.addModel(world.getRoofModel(layer, i));
            if (layer == 0) {
                // Add roofs to upper storeys
                scene.addModel(world.getWallModel(1, i));
                scene.addModel(world.getRoofModel(1, i));
                scene.addModel(world.getWallModel(2, i));
                scene.addModel(world.getRoofModel(2, i));
            }
        }

        // Change camera angle
        Camera camera = scene.getCamera();
        int cameraHeight = game.getCameraHeight();
        scene.clipFar3d = 2400 + (cameraHeight * 2);
        scene.clipFar2d = 2400 + (cameraHeight * 2);
        scene.fogZFalloff = 1;
        scene.fogZDistance = 2300 + (cameraHeight * 2);

        int l5 = game.getLastAutoCameraRotatePlayerX() + game.getScreenRotationX();
        int i8 = game.getLastAutoCameraRotatePlayerY() + game.getScreenRotationY();
        camera.setCamera(l5, -world.getAveragedElevation(l5, i8), i8, 912, game.getCameraRotation() * 4, 0,
                cameraHeight * 2);

        game.getSceneRenderer().render();
    }

}
