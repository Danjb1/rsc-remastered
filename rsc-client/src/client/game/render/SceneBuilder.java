package client.game.render;

import client.game.scene.Scene;
import client.game.world.Door;
import client.game.world.World;

public class SceneBuilder {

    private static final int MAX_BUILDINGS = 64;

    private Scene scene;

    private World world;

    public SceneBuilder(Scene scene, World world) {
        this.scene = scene;
        this.world = world;
    }

    public void build() {
        addBuildings();
        addDoors();
    }

    private void addBuildings() {

        int layer = world.getCurrentLayer();

        for (int i = 0; i < MAX_BUILDINGS; i++) {

            // Remove all roofs from the current layer
            scene.removeModel(world.getRoofModel(layer, i));

            if (layer == World.LAYER_GROUND) {

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
    }

    private void addDoors() {
        for (int i = 0; i < world.getNumDoors(); i++) {
            Door door = world.getDoor(i);
            if (world.containsTileRelativeToOrigin(door.getX(), door.getZ())) {
                scene.addModel(door.getModel());
            }
        }
    }

}
