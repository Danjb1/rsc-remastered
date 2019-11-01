package client.game.render;

import client.Canvas;
import client.StateRenderer;
import client.game.Game;
import client.game.scene.Scene;
import client.game.world.World;

/**
 * Class responsible for rendering the game.
 *
 * <p>The general idea is:
 *
 * <ul>
 *  <li>Build the scene</li>
 *  <li>Tell the SoftwareRenderer to render the scene</li>
 *  <li>Draw the UI on top</li>
 * </ul>
 *
 * @author Dan Bryce
 */
public class GameRenderer extends StateRenderer {

    private static final int SPRITE_ID_MENUS = 2000;
    private static final int SPRITE_ID_INVENTORY = 2001;

    private static final int MENUS_OFFSET_X = 200;
    private static final int MENUS_OFFSET_Y = 3;
    private static final int INVENTORY_MENU_OFFSET_X = 248;

    private Game game;
    private World world;
    private SceneBuilder sceneBuilder;
    private Scene scene;

    private SoftwareRenderer softwareRenderer;
    private MousePicker mousePicker;

    public GameRenderer(Game game) {
        this.game = game;

        world = game.getWorld();
        scene = game.getScene();

        sceneBuilder = new SceneBuilder(scene, world);
        softwareRenderer = new SoftwareRenderer(scene,
                game.getLauncher().getWidth(),
                game.getLauncher().getHeight());
        mousePicker = softwareRenderer.getMousePicker();
    }

    @Override
    public void render(Canvas canvas) {
        renderWorld(canvas);
        renderUi(canvas);
    }

    private void renderWorld(Canvas canvas) {

        // Don't render until the world is loaded
        if (!world.isLoaded()) {
            return;
        }

        // Render the scene
        sceneBuilder.build();
        softwareRenderer.render(canvas);
    }

    public MousePicker getMousePicker() {
        return mousePicker;
    }

    private void renderUi(Canvas canvas) {
        renderMenus(canvas);
        renderInventoryMenu(canvas);
    }

    private void renderMenus(Canvas canvas) {
        canvas.drawSprite(
                game.getLauncher().getWidth() - MENUS_OFFSET_X,
                MENUS_OFFSET_Y,
                SPRITE_ID_MENUS);
    }

    private void renderInventoryMenu(Canvas canvas) {
        canvas.drawSprite(
                game.getLauncher().getWidth() - INVENTORY_MENU_OFFSET_X,
                MENUS_OFFSET_Y,
                SPRITE_ID_INVENTORY);
    }

}
