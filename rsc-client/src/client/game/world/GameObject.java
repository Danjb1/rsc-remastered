package client.game.world;

import client.game.scene.Model;
import client.res.Resources;

public class GameObject {

    private World world;

    private int x;
    private int z;
    private int type;
    private int id;
    private Model model;

    public GameObject(World world, int x, int z, int type, int id) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.type = type;
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }

    public void move(int dx, int dz) {

        x += dx;
        z += dz;

        int width;
        int height;

        if (id == 0 || id == 4) {
            // Special case objects have width and height swapped?
            width = Resources.getObjectDef(type).getWidth();
            height = Resources.getObjectDef(type).getHeight();
        } else {
            height = Resources.getObjectDef(type).getWidth();
            width = Resources.getObjectDef(type).getHeight();
        }

        int tileX = ((x + x + width) * World.TILE_WIDTH) / 2;
        int tileZ = ((z + z + height) * World.TILE_DEPTH) / 2;

        // Add new models
        if (world.containsTileRelativeToOrigin(x, z)) {
            model.setTranslate(tileX, -world.getAveragedElevation(tileX, tileZ), tileZ);

            if (type == 74) {
                // Special case object needs to be higher?
                model.translate(0, -480, 0);
            }
        }
    }

}
