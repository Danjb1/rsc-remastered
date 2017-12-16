package client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import client.render.SceneRenderer;
import client.scene.Scene;

public class Game implements KeyListener, MouseListener, MouseMotionListener {

    private static final int FPS = 50;
    private static final int MS_PER_FRAME = 1000 / FPS;

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String WINDOW_TITLE = "Runescape";

    private GamePanel gamePanel;
    private JFrame frame;

    private World world;
    private Scene scene;

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
    
    private int regionX = 57;
    private int regionY = 55;

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
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        // Explore the world with the keyboard
        switch (e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            regionX--;
            break;
        case KeyEvent.VK_RIGHT:
            regionX++;
            break;
        case KeyEvent.VK_UP:
            regionY--;
            break;
        case KeyEvent.VK_DOWN:
            regionY++;
            break;
        }

        System.out.println("Loading region: " + regionX + ", " + regionY);
        world.loadRegion(regionX, regionY);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

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
        scene = new Scene();
        sceneRenderer = new SceneRenderer(gamePanel, scene);
        world = new World(scene);
        world.loadRegion(regionX, regionY);
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

}
