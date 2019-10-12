package client.game;

import java.awt.event.KeyEvent;

import client.RuneClient;
import client.State;
import client.StateRenderer;
import client.game.model.Mob;
import client.game.model.Sector;
import client.game.render.GameRenderer;
import client.game.render.MousePicker;
import client.game.scene.Camera;
import client.game.scene.Model;
import client.game.scene.Scene;
import client.game.world.World;
import client.game.world.WorldLoader;
import client.loading.LoadingScreen;
import client.login.LoginScreen;

/**
 * State responsible for running the game.
 *
 * <p><i>Based on <code>mudclient.java</code> from other RSC sources.</i>
 *
 * @author Dan Bryce
 */
public class Game extends State {

    public static final int SPAWN_SECTOR_X = 50;
    public static final int SPAWN_SECTOR_Z = 51;

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

    public Game(RuneClient launcher) {
        super(launcher);

        // Player position is relative to the World origin
        player = new Mob();
        player.x = 66 * World.TILE_WIDTH;
        player.z = 32 * World.TILE_DEPTH;

        scene = new Scene();

        world = new World(scene);
        worldLoader = new WorldLoader(world);
        worldLoader.loadSector(SPAWN_SECTOR_X, SPAWN_SECTOR_Z);

        renderer = new GameRenderer(this);
    }

    @Override
    public StateRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void pollInput() {

        // Key handling
        if (input.wasKeyReleased(KeyEvent.VK_PAGE_UP)) {
            worldLoader.ascend();
        } else if (input.wasKeyReleased(KeyEvent.VK_PAGE_DOWN)) {
            worldLoader.descend();
        }

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
        if (input.wasLeftClickReleased()) {
            player.x = tileX * World.TILE_WIDTH;
            player.z = tileZ * World.TILE_DEPTH;
        }
    }

    @Override
    public void tick() {

        /*
         * Load next Sectors
         */

        if (player.x < 16 * World.TILE_WIDTH) {
            worldLoader.loadSector(world.getSectorX() - 1, world.getSectorZ());
            player.x += Sector.WIDTH * World.TILE_WIDTH;

        } else if (player.x > 80 * World.TILE_WIDTH) {
            worldLoader.loadSector(world.getSectorX() + 1, world.getSectorZ());
            player.x -= Sector.WIDTH * World.TILE_WIDTH;
        }

        if (player.z < 16 * World.TILE_DEPTH) {
            worldLoader.loadSector(world.getSectorX(), world.getSectorZ() - 1);
            player.z += Sector.DEPTH * World.TILE_DEPTH;

        } else if (player.z > 80 * World.TILE_DEPTH) {
            worldLoader.loadSector(world.getSectorX(), world.getSectorZ() + 1);
            player.z -= Sector.DEPTH * World.TILE_DEPTH;
        }
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

}
