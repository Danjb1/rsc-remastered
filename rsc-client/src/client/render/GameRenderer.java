package client.render;

import client.Game;
import client.GamePanel;
import client.scene.Camera;
import client.scene.Scene;

public class GameRenderer {

    public static void render(GamePanel gamePanel, Game game) {
        Scene scene = game.getScene();
        Camera camera = scene.getCamera();
        int cameraHeight = 550;
        camera.setCamera(0, -384, 0, 912, 0 * 4, 0, cameraHeight * 2);
        game.getSceneRenderer().render();
    }

}
