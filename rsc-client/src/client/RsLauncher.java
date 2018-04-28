package client;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import client.states.Game;
import client.states.LoadingScreen;
import client.states.LoginScreen;

/**
 * Class responsible for setting up and running the game.
 * 
 * <p><i>Based on <code>GameShell.java</code> from other RSC sources.</i>
 * 
 * @author Dan Bryce
 */
public class RsLauncher {

    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    
    private static final String WINDOW_TITLE = "OpenRSC";

    private static final int FPS = 50;
    private static final int MS_PER_FRAME = 1000 / FPS;

    /**
     * Flag used to tell the game to exit.
     *
     * The original RSC used an exit timer instead, to give the game time to
     * finish any outstanding operations before exiting.
     */
    private boolean exiting;

    private GamePanel gamePanel;
    
    private JFrame frame;

    private State state;

    public static void main(String[] args) {
        RsLauncher rs = new RsLauncher();
        rs.run();
    }

    public RsLauncher() {
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
                pollInput();
                tick();
                timeSinceLastRender += timeSinceLastFrame;
            }

            render();
        }
    }

    private void loadGame() {
        
        LoadingScreen loadingScreen = new LoadingScreen(this);
        state = loadingScreen;
        
        while (!loadingScreen.isLoaded()){
            loadingScreen.continueLoading();
            render();
            try {
                // Precise framerate is not important here, just so long as we
                // don't hog the thread.
                Thread.sleep(16); // Roughly 60fps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finishedLoading() {
        changeState(new LoginScreen());
        
        // Skip the login screen for now
        changeState(new Game());
    }
    
    private void pollInput() {
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLoc, gamePanel);
        state.setMousePos(mouseLoc.x, mouseLoc.y);
        
        synchronized (state) {
            state.pollInput();
            state.clearInput();
        }
    }

    private void tick() {
        state.tick();
    }

    private void render() {
        gamePanel.repaint();
    }

    public void changeState(State newState) {
        
        // Remove listeners from previous state
        gamePanel.removeMouseListener(state);
        frame.removeKeyListener(state);
        
        state = newState;

        // Add listeners to new state
        gamePanel.addMouseListener(state);
        frame.addKeyListener(state);
    }
    
    public State getState() {
        return state;
    }
    
}
