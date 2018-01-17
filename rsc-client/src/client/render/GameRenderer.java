package client.render;

import client.Canvas;
import client.World;
import client.scene.Camera;
import client.scene.Scene;
import client.states.Game;

public class GameRenderer {

    public static void render(Game game, Canvas canvas) {
        
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

        /*
        // Draw players
        Mob[] npcArray = game.getNpcArray();
        Mob[] mobArray = game.getMobArray();
        Mob[] npcRecordArray = game.getNpcRecordArray();
        for (int j1 = 0; j1 < playerCount; j1++) {
            Mob player = playerArray[j1];
            if (player.projectileRange > 0) {
                Mob attackingNpc = null;
                if (player.attackingNpcIndex != -1) {
                    attackingNpc = npcRecordArray[player.attackingNpcIndex];
                } else if (player.attackingMobIndex != -1) {
                    attackingNpc = mobArray[player.attackingMobIndex];
                }
                if (attackingNpc != null) {
                    int px = player.currentX;
                    int py = player.currentY;
                    int pi = -world.getAveragedElevation(px, py) - 110;
                    int nx = attackingNpc.currentX;
                    int ny = attackingNpc.currentY;
                    int ni = -world.getAveragedElevation(nx, ny)
                            - EntityHandler.getNpcDef(attackingNpc.type).getCamera2() / 2;
                    int i10 = (px * player.projectileRange + nx * (GameClient.PROJECTILE_MAX_RANGE - player.projectileRange))
                            / GameClient.PROJECTILE_MAX_RANGE;
                    int j10 = (pi * player.projectileRange + ni * (GameClient.PROJECTILE_MAX_RANGE - player.projectileRange))
                            / GameClient.PROJECTILE_MAX_RANGE;
                    int k10 = (py * player.projectileRange + ny * (GameClient.PROJECTILE_MAX_RANGE - player.projectileRange))
                            / GameClient.PROJECTILE_MAX_RANGE;
                    scene.addSprite(GameClient.SPRITE_PROJECTILE_START + player.attackingCameraInt, i10, j10, k10, 32,
                            32, 0);
                    objectsProcessed++;
                }
            }
        }
        */

        /*
        // Draw NPCs
        int npcCount = game.getNpcCount();
        for (int l1 = 0; l1 < npcCount; l1++) {
            Mob npc = npcArray[l1];
            int mobx = npc.currentX;
            int moby = npc.currentY;
            int i7 = -world.getAveragedElevation(mobx, moby);
            int i9 = scene.addSprite(20000 + l1, mobx, i7, moby, EntityHandler.getNpcDef(npc.type).getCamera1(),
                    EntityHandler.getNpcDef(npc.type).getCamera2(), l1 + 30000);
            objectsProcessed++;
            if (npc.currentSprite == 8) {
                scene.setSpriteTranslateX(i9, -30);
            }
            if (npc.currentSprite == 9) {
                scene.setSpriteTranslateX(i9, 30);
            }
        }
        */

        /*
        // Draw ground items
        int magicLoc = game.getMagicLoc();
        for (int itemIndex = 0; itemIndex < world.getNumGroundItems(); itemIndex++) {
            GroundItem item = world.getGroundItem(itemIndex);
            int itemX = item.getX() * magicLoc + 64;
            int itemY = item.getY() * magicLoc + 64;
            int itemZ = -world.getAveragedElevation(itemX, itemY) - item.getZ();
            scene.addSprite(40000 + item.getType(), itemX, itemZ, itemY, 96, 64, itemIndex + 20000);
            objectsProcessed++;
        }
        */

        /*
        // Draw something
        int countOfSomething = game.getCountOfSomething();
        int[] somethingX = game.getSomethingX();
        int[] somethingY = game.getSomethingY();
        int[] somethingVar = game.getSomethingVar();
        for (int k3 = 0; k3 < countOfSomething; k3++) {
            int l4 = somethingX[k3] * magicLoc + 64;
            int j7 = somethingY[k3] * magicLoc + 64;
            int j9 = somethingVar[k3];
            if (j9 == 0) {
                scene.addSprite(50000 + k3, l4, -world.getAveragedElevation(l4, j7), j7, 128, 256, k3 + 50000);
                objectsProcessed++;
            }
            if (j9 == 1) {
                scene.addSprite(50000 + k3, l4, -world.getAveragedElevation(l4, j7), j7, 128, 64, k3 + 50000);
                objectsProcessed++;
            }
        }
        game.setObjectsProcessed(objectsProcessed);
        */

        // Change camera angle
        Camera camera = scene.getCamera();
        int cameraHeight = game.getCameraHeight();
        scene.fogZFalloff = 1;
        scene.fogZDistance = 2300 + (cameraHeight * 2);

        int l5 = game.getLastAutoCameraRotatePlayerX() + game.getScreenRotationX();
        int i8 = game.getLastAutoCameraRotatePlayerY() + game.getScreenRotationY();
        camera.setCamera(l5, -world.getAveragedElevation(l5, i8), i8, 912, game.getCameraRotation() * 4, 0,
                cameraHeight * 2);

        game.getSceneRenderer().render(canvas);
    }

}
