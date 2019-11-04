package client.game;

import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import client.net.Packet;

/**
 * State responsible for running the game.
 *
 * <p>
 * <i>Based on <code>mudclient.java</code> from other RSC sources.</i>
 *
 * @author Dan Bryce
 */
public class Game extends State {

    private final Logger logger = Logger.getLogger(getClass().getName());

    public static final int SPAWN_SECTOR_X = 50;
    public static final int SPAWN_SECTOR_Z = 51;

    private WorldLoader worldLoader;
    private World world;
    private Scene scene;
    private Mob player;

    private GameRenderer renderer;

    // 192 = West
    // 128 = North
    // 64 = East
    // 0 = South
    private int cameraRotation = 128;
    private int cameraPositionX;
    private int cameraPositionZ;
    private int cameraHeight = Camera.DEFAULT_HEIGHT;

    // Server session
    private String displayName;
    private int sessionId;
    private int privilege;

    public Game(RuneClient launcher) {
        super(launcher);
        scene = new Scene();
        world = new World(scene);
        worldLoader = new WorldLoader(world);
        renderer = new GameRenderer(this);
    }

    @Override
    public void start() {
    }

    @Override
    public StateRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void pollInput() {
        handleKeys();
        handleMouse();
    }

    private void handleKeys() {

        // Camera rotation
        if (input.isKeyDown(KeyEvent.VK_LEFT)) {
            cameraRotation++;
        } else if (input.isKeyDown(KeyEvent.VK_RIGHT)) {
            cameraRotation--;
        }

        // Change levels
        if (input.wasKeyReleased(KeyEvent.VK_PAGE_UP)) {
            worldLoader.ascend();
            loadSectors();
        } else if (input.wasKeyReleased(KeyEvent.VK_PAGE_DOWN)) {
            worldLoader.descend();
            loadSectors();
        }
    }

    private void handleMouse() {

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
            loadSectors();
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
        // Check for unexpected disconnection.
        if (player != null && !launcher.isConnected()) {
            logger.log(Level.WARNING, "Connection Lost!");
            launcher.disconnect();
            return;
        }

        // Client is connected.
        // Put any network game logic below.

        if (player == null) {
            // Not yet logged in
            return;
        }

        updateCamera();

    }

    /**
     * Executes an incoming packet.
     */
    public void executePacket(Packet packet) {
        switch (packet.getOpcode()) {
        case 3:
            // Read chatbox message
            int icon = packet.getByte();
            String message = packet.getString();
            System.out.println("[icon-" + icon + "]" + message);
            break;
        default:
            logger.log(Level.WARNING,
                    "Unhandled Packet, opcode: " + packet.getOpcode() + ", length: " + packet.getPacketLength());
            break;
        }
    }

    private void updateCamera() {

        int x = player.x + cameraPositionX;
        int z = player.z + cameraPositionZ;
        int y = -world.getAveragedElevation(x, z);

        int pitch = Camera.DEFAULT_PITCH;
        int yaw = cameraRotation * 4;
        int roll = 0;
        int height = cameraHeight * 2;

        scene.getCamera().set(x, y, z, pitch, yaw, roll, height);

        // Update fog distance based on camera height
        scene.fogZDistance = Camera.DEFAULT_FOG_DISTANCE + (cameraHeight * 2);
    }

    private void loadSectors() {

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

    /**
     * Executed when the server accepts our login request.
     */
    public void loggedIn(String displayName, int sessionId, int privilege) {
        // Get the server variables for later.
        this.displayName = displayName;
        this.sessionId = sessionId;
        this.privilege = privilege;

        // Player position is relative to the World origin
        player = new Mob();
        player.x = 66 * World.TILE_WIDTH;
        player.z = 32 * World.TILE_DEPTH;
        worldLoader.loadSector(SPAWN_SECTOR_X, SPAWN_SECTOR_Z);
    }

    @Override
    public void reset() {
        displayName = "";
        sessionId = -1;
        privilege = -1;
        player = null;
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

}
