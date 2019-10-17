package client.util;

import client.game.scene.Model;
import client.game.world.Door;
import client.game.world.World;
import client.res.Resources;

public class ModelUtils {

    private static final int MIN_DOOR_ID = 10000;

    /**
     * Adds a wall to the given model.
     *
     * @param world
     * @param model
     * @param wallIndex
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     */
    public static void createWall(World world, Model model, int wallIndex, int x1, int z1, int x2, int z2) {
        setAmbientLighting(world, x1, z1, 40);
        setAmbientLighting(world, x2, z2, 40);
        int height = Resources.getDoorDef(wallIndex).getHeight();
        int frontTexture = Resources.getDoorDef(wallIndex).getFrontTexture();
        int backTexture = Resources.getDoorDef(wallIndex).getBackTexture();
        int i2 = x1 * 128;
        int j2 = z1 * 128;
        int k2 = x2 * 128;
        int l2 = z2 * 128;
        int i3 = model.addUniqueVertex(i2, -world.getElevation(x1, z1), j2);
        int j3 = model.addUniqueVertex(i2, -world.getElevation(x1, z1) - height, j2);
        int k3 = model.addUniqueVertex(k2, -world.getElevation(x2, z2) - height, l2);
        int l3 = model.addUniqueVertex(k2, -world.getElevation(x2, z2), l2);

        int i4 = model.addFace(4, new int[] { i3, j3, k3, l3 }, frontTexture, backTexture);
        if (Resources.getDoorDef(wallIndex).getUnknown() == 5) {
            model.faceTag[i4] = 30000 + wallIndex;
        } else {
            model.faceTag[i4] = 0;
        }
    }

    private static void setAmbientLighting(World world, int x, int z, int height) {
        int modelIndex1 = x / 12;
        int modelIndex2 = z / 12;
        int otherModelIndex1 = (x - 1) / 12;
        int otherModelIndex2 = (z - 1) / 12;
        setAmbientLighting(world, modelIndex1, modelIndex2, x, z, height);
        if (modelIndex1 != otherModelIndex1) {
            setAmbientLighting(world, otherModelIndex1, modelIndex2, x, z, height);
        }
        if (modelIndex2 != otherModelIndex2) {
            setAmbientLighting(world, modelIndex1, otherModelIndex2, x, z, height);
        }
        if (modelIndex1 != otherModelIndex1 && modelIndex2 != otherModelIndex2) {
            setAmbientLighting(world, otherModelIndex1, otherModelIndex2, x, z, height);
        }
    }

    private static void setAmbientLighting(World world, int modelIndex1, int modelIndex2, int x, int z, int ambience) {
        Model gameModel = world.getLandscapeModel(modelIndex1 + modelIndex2 * 8);
        for (int vertex = 0; vertex < gameModel.numVertices; vertex++) {
            if (gameModel.vertices[vertex].x == x * 128 && gameModel.vertices[vertex].z == z * 128) {
                gameModel.setVertexAmbience(vertex, ambience);
                return;
            }
        }
    }

    /**
     * Creates a Door model.
     *
     * @param door
     * @param world
     * @param entityId
     * @return
     */
    public static Model createDoor(Door door, World world, int entityId) {

        int modelX1 = door.getX();
        int modelZ1 = door.getZ();
        int modelX2 = modelX1;
        int modelZ2 = modelZ1;

        int id = door.getId();
        int frontTex = Resources.getDoorDef(id).getFrontTexture();
        int backTex = Resources.getDoorDef(id).getBackTexture();
        int height = Resources.getDoorDef(id).getHeight();

        int orientation = door.getOrientation();
        if (orientation == 0) {
            modelX2 += 1;
        }
        if (orientation == 1) {
            modelZ2 += 1;
        }
        if (orientation == 2) {
            modelX1 += 1;
            modelZ2 += 1;
        }
        if (orientation == 3) {
            modelX2 += 1;
            modelZ2 += 1;
        }

        modelX1 *= World.TILE_WIDTH;
        modelZ1 *= World.TILE_DEPTH;
        modelX2 *= World.TILE_WIDTH;
        modelZ2 *= World.TILE_DEPTH;

        Model model = new Model(4, 1);
        int vert1 = model.addUniqueVertex(modelX1, -world.getAveragedElevation(modelX1, modelZ1), modelZ1);
        int vert2 = model.addUniqueVertex(modelX1, -world.getAveragedElevation(modelX1, modelZ1) - height, modelZ1);
        int vert3 = model.addUniqueVertex(modelX2, -world.getAveragedElevation(modelX2, modelZ2) - height, modelZ2);
        int vert4 = model.addUniqueVertex(modelX2, -world.getAveragedElevation(modelX2, modelZ2), modelZ2);
        int vertices[] = { vert1, vert2, vert3, vert4 };

        model.addFace(4, vertices, frontTex, backTex);
        model.setLighting(false, 60, 24, -50, -10, -50);

        model.entityId = MIN_DOOR_ID + entityId;

        return model;
    }

}
