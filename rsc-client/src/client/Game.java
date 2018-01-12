package client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import client.model.Mob;
import client.render.SceneRenderer;
import client.res.Resources;
import client.scene.Camera;
import client.scene.GameModel;
import client.scene.Scene;

public class Game implements KeyListener, MouseListener, MouseMotionListener {

    private static final int FPS = 50;
    private static final int MS_PER_FRAME = 1000 / FPS;

    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final String WINDOW_TITLE = "Runescape";

    private static final int MIN_DOOR_ID = 10000;
    
    private GamePanel gamePanel;
    private JFrame frame;

    private World world;
    private Scene scene;
    private Mob player;

    /**
     * Flag used to tell the game to exit.
     *
     * The original RSC used an exit timer instead, to give the game time to
     * finish any outstanding operations before exiting.
     */
    private boolean exiting;

    private LoadingScreen loadingScreen;
    private LoginScreen loginScreen;
    private SceneRenderer sceneRenderer;

    private int cameraRotation = 128;
    private int screenRotationX;
    private int screenRotationY;
    private int cameraHeight = Camera.DEFAULT_HEIGHT;
    private int lastAutoCameraRotatePlayerX;
    private int lastAutoCameraRotatePlayerY;
    
    private int regionX;
    private int regionY;
    private int layer = 0;
    
    private int planeWidth = 2304;
    private int planeHeight = 1776;
    private int planeIndex = 0;
    
    private int mapBoundaryX1;
    private int mapBoundaryY1;
    private int mapBoundaryX2;
    private int mapBoundaryY2;

    private int objectCount;
    private int objectX[] = new int[1500];
    private int objectY[] = new int[1500];
    private int objectTypes[] = new int[1500];
    private int objectID[] = new int[1500];
    private GameModel objectModels[] = new GameModel[1500];
    private int magicLoc = 128;

    private int wallObjectCount;
    private int wallObjectX[] = new int[500];
    private int wallObjectY[] = new int[500];
    private int wallObjectDirection[] = new int[500];
    private int wallObjectId[] = new int[500];
    private GameModel wallObjectModels[] = new GameModel[500];
    
    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }

    public Game() {

        loadingScreen = new LoadingScreen(this);

        createFrame(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
    }

    private void createFrame(int width, int height, String title) {
        gamePanel = new GamePanel(this, width, height);
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        // Pseudo-fullscreen if window fills the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setUndecorated(width == screenSize.width &&
                height == screenSize.height);
        frame.setContentPane(gamePanel);
        frame.addKeyListener(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();
    }

    public void run() {

        loadGame();

        // Initialise our circular buffer of frame times
        int frameIndex = 0;
        long[] frameTimes = new long[10];
        for (int i = 0; i < 10; i++) {
            frameTimes[i] = System.currentTimeMillis();
        }

        /*
         * The game loop.
         */
        while (!exiting) {

            long now = System.currentTimeMillis();
            int sleepTime = 1;

            // Calculate time elapsed since last frame
            int timeSinceLastFrame;
            if (now > frameTimes[frameIndex]) {
                timeSinceLastFrame = (int)
                        (2560 * MS_PER_FRAME / (now - frameTimes[frameIndex]));
            } else {
                timeSinceLastFrame = 300;
            }

            // Keep timePassed between 25 and 256
            if (timeSinceLastFrame < 25) {
                timeSinceLastFrame = 25;
            } else if (timeSinceLastFrame > 256) {
                timeSinceLastFrame = 256;

                // Calculate time until next frame is due
                sleepTime = (int)
                        (MS_PER_FRAME - (now - frameTimes[frameIndex]) / 10L);

                // sleepTime must be at least 1
                if (sleepTime < 1) {
                    sleepTime = 1;
                }
            }

            // Sleep until the next frame is due
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            // Record the time of this frame
            frameTimes[frameIndex] = now;

            // Advance the frame index
            frameIndex = (frameIndex + 1) % 10;

            if (sleepTime > 1) {
                // Recalculate frame times based on sleepTime
                for (int i = 0; i < 10; i++) {
                    frameTimes[i] += sleepTime;
                }
            }

            // Process the game continually until we are due to render
            int timeSinceLastRender = 0;
            while (timeSinceLastRender < 256) {
                tick();
                timeSinceLastRender += timeSinceLastFrame;
            }

            // Render
            gamePanel.repaint();
        }
    }

    private void tick() {

        // Rotate camera
        if (lastAutoCameraRotatePlayerX - player.currentX < -500
                || lastAutoCameraRotatePlayerX - player.currentX > 500
                || lastAutoCameraRotatePlayerY - player.currentY < -500
                || lastAutoCameraRotatePlayerY - player.currentY > 500) {
            lastAutoCameraRotatePlayerX = player.currentX;
            lastAutoCameraRotatePlayerY = player.currentY;
        }
        if (lastAutoCameraRotatePlayerX != player.currentX) {
            lastAutoCameraRotatePlayerX += (player.currentX - lastAutoCameraRotatePlayerX)
                    / (16 + (cameraHeight - 500) / 15);
        }
        if (lastAutoCameraRotatePlayerY != player.currentY) {
            lastAutoCameraRotatePlayerY += (player.currentY - lastAutoCameraRotatePlayerY)
                    / (16 + (cameraHeight - 500) / 15);
        }
    }

    private void loadGame() {
        while (loadingScreen != null && !loadingScreen.isLoaded()){
            loadingScreen.continueLoading();
            gamePanel.repaint();
            try {
                // Precise framerate is not important here, just so long as we
                // don't hog the thread.
                Thread.sleep(16); // Roughly 60fps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        
        // Explore the world with the keyboard
        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            player.currentX += 200;
            break;
        case KeyEvent.VK_RIGHT:
            player.currentX -= 200;
            break;
        case KeyEvent.VK_UP:
            player.currentY -= 200;
            break;
        case KeyEvent.VK_DOWN:
            player.currentY += 200;
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public JFrame getFrame() {
        return frame;
    }

    public LoadingScreen getLoadingScreen() {
        return loadingScreen;
    }

    public LoginScreen getLoginScreen() {
        return loginScreen;
    }

    public void finishedLoading() {
        loadingScreen = null;
        loginScreen = new LoginScreen();
        
        // For now, just pretend we've logged in straight away
        login();
    }
    
    private void login() {
        loginScreen = null;
        player = new Mob();
        player.currentX = 8512;
        player.currentY = 4160;
        scene = new Scene();
        sceneRenderer = new SceneRenderer(gamePanel, scene);
        world = new World(scene);
        loadNextRegion(114, 656);
    }

    private boolean loadNextRegion(int x, int y) {
        
        x += planeWidth;
        y += planeHeight;
        
        if (layer == planeIndex &&
                x > mapBoundaryX1 && x < mapBoundaryX2 &&
                y > mapBoundaryY1 && y < mapBoundaryY2) {
            // No need to load region if already loaded
            return false;
        }
        
        int k = regionX;
        int l = regionY;
        int i1 = (x + 24) / 48;
        int j1 = (y + 24) / 48;
        layer = planeIndex;
        regionX = i1 * 48 - 48;
        regionY = j1 * 48 - 48;
        
        // Set map boundary around the loaded region
        mapBoundaryX1 = i1 * 48 - 32;
        mapBoundaryY1 = j1 * 48 - 32;
        mapBoundaryX2 = i1 * 48 + 32;
        mapBoundaryY2 = j1 * 48 + 32;
        
        world.loadRegion(x, y, layer);
        
        regionX -= planeWidth;
        regionY -= planeHeight;
        int k1 = regionX - k;
        int l1 = regionY - l;
        
        // Add object models to scene
        for (int i2 = 0; i2 < objectCount; i2++) {
            objectX[i2] -= k1;
            objectY[i2] -= l1;
            int j2 = objectX[i2];
            int l2 = objectY[i2];
            int k3 = objectTypes[i2];
            GameModel gameModel = objectModels[i2];
            try {
                int l4 = objectID[i2];
                int k5;
                int i6;
                if (l4 == 0 || l4 == 4) {
                    k5 = Resources.getObjectDef(k3).getWidth();
                    i6 = Resources.getObjectDef(k3).getHeight();
                } else {
                    i6 = Resources.getObjectDef(k3).getWidth();
                    k5 = Resources.getObjectDef(k3).getHeight();
                }
                int j6 = ((j2 + j2 + k5) * magicLoc) / 2;
                int k6 = ((l2 + l2 + i6) * magicLoc) / 2;
                if (j2 >= 0 && l2 >= 0 && j2 < 96 && l2 < 96) {
                    scene.addModel(gameModel);
                    gameModel.setVars(j6, -world.getAveragedElevation(j6, k6), k6);
                    if (k3 == 74) {
                        gameModel.modVars(0, -480, 0);
                    }
                }
            } catch (RuntimeException runtimeexception) {
                System.out.println("Loc Error: " + runtimeexception.getMessage());
                System.out.println("i:" + i2 + " obj:" + gameModel);
                runtimeexception.printStackTrace();
            }
        }

        // Create wall models
        for (int k2 = 0; k2 < wallObjectCount; k2++) {
            wallObjectX[k2] -= k1;
            wallObjectY[k2] -= l1;
            int i3 = wallObjectX[k2];
            int l3 = wallObjectY[k2];
            int j4 = wallObjectId[k2];
            int i5 = wallObjectDirection[k2];
            try {
                GameModel model_1 = createModel(i3, l3, i5, j4, k2);
                wallObjectModels[k2] = model_1;
            } catch (RuntimeException runtimeexception1) {
                System.out.println("Bound Error: " + runtimeexception1.getMessage());
                runtimeexception1.printStackTrace();
            }
        }

        return true;
    }

    public Scene getScene() {
        return scene;
    }
    
    public SceneRenderer getSceneRenderer() {
        return sceneRenderer;
    }

    public World getWorld() {
        return world;
    }
    
    public int getLayer() {
        return layer;
    }

    public Mob getCurrentPlayer() {
        return player;
    }

    private GameModel createModel(int x, int y, int k, int l, int i1) {
        int modelX = x;
        int modelY = y;
        int modelX1 = x;
        int modelX2 = y;
        int j2 = Resources.getDoorDef(l).getModelVar2();
        int k2 = Resources.getDoorDef(l).getModelVar3();
        int l2 = Resources.getDoorDef(l).getModelVar1();
        GameModel gameModel = new GameModel(4, 1);
        if (k == 0) {
            modelX1 = x + 1;
        }
        if (k == 1) {
            modelX2 = y + 1;
        }
        if (k == 2) {
            modelX = x + 1;
            modelX2 = y + 1;
        }
        if (k == 3) {
            modelX1 = x + 1;
            modelX2 = y + 1;
        }
        modelX *= magicLoc;
        modelY *= magicLoc;
        modelX1 *= magicLoc;
        modelX2 *= magicLoc;
        int i3 = gameModel.getSomeIndex(modelX, -world.getAveragedElevation(modelX, modelY), modelY);
        int j3 = gameModel.getSomeIndex(modelX, -world.getAveragedElevation(modelX, modelY) - l2, modelY);
        int k3 = gameModel.getSomeIndex(modelX1, -world.getAveragedElevation(modelX1, modelX2) - l2, modelX2);
        int l3 = gameModel.getSomeIndex(modelX1, -world.getAveragedElevation(modelX1, modelX2), modelX2);
        int ai[] = { i3, j3, k3, l3 };
        gameModel.createFace(4, ai, j2, k2);
        gameModel.getDistanceToSomething(false, 60, 24, -50, -10, -50);
        if (x >= 0 && y >= 0 && x < 96 && y < 96) {
            scene.addModel(gameModel);
        }
        gameModel.entityId = i1 + MIN_DOOR_ID;
        return gameModel;
    }

    public int getCameraHeight() {
        return cameraHeight;
    }
    
    public int getCameraRotation() {
        return cameraRotation;
    }
    
    public int getLastAutoCameraRotatePlayerX() {
        return lastAutoCameraRotatePlayerX;
    }
    
    public int getLastAutoCameraRotatePlayerY() {
        return lastAutoCameraRotatePlayerY;
    }
    
    public int getScreenRotationX() {
        return screenRotationX;
    }
    
    public int getScreenRotationY() {
        return screenRotationY;
    }

    public void setCameraRotation(int cameraRotation) {
        this.cameraRotation = cameraRotation;
    }
    
    public void setLastAutoCameraRotatePlayerX(
            int lastAutoCameraRotatePlayerX) {
        this.lastAutoCameraRotatePlayerX = lastAutoCameraRotatePlayerX;
    }
    
    public void setLastAutoCameraRotatePlayerY(
            int lastAutoCameraRotatePlayerY) {
        this.lastAutoCameraRotatePlayerY = lastAutoCameraRotatePlayerY;
    }
    
}
