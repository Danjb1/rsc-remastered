package client.game.world;

import client.game.scene.Model;
import client.util.ModelUtils;

public class Door {

    private World world;

    private int x;
    private int z;
    private int orientation;
    private int id;
    private Model model;

    public Door(World world, int x, int z, int orientation, int id, int entityId) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.orientation = orientation;
        this.id = id;

        model = ModelUtils.createDoor(this, world, entityId);
    }

    public void move(int dx, int dz) {
        x += dx;
        z += dz;
        model = ModelUtils.createDoor(this, world, model.entityId);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getId() {
        return id;
    }

    public Model getModel() {
        return model;
    }

}
