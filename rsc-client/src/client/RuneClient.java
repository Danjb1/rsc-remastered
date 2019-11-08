package client;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import client.loading.LoadingScreen;

/**
 * Class responsible for setting up and running the game.
 *
 * <p><i>Based on <code>GameShell.java</code> from other RSC sources.</i>
 *
 * @author Dan Bryce
 */
public class RuneClient {

    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final String WINDOW_TITLE = "RSC Remastered";

    private static final int MS_PER_FRAME = 16; // 60fps

    /**
     * Flag used to tell the game to exit.
     *
     * The original RSC used an exit timer instead, to give the game time to
     * finish any outstanding operations before exiting.
     */
    private boolean exiting;

    private JFrame frame;
    private JPanel gamePanel;
    private Canvas canvas;
    private BufferedImage screenBuffer;

    private State state;

    public RuneClient() {
        createFrame(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);

        screenBuffer = new BufferedImage(
                WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        canvas = new Canvas(screenBuffer);
    }

    private void createFrame(int width, int height, String title) {

        // Create the content pane
        gamePanel = new JPanel();
        gamePanel.setPreferredSize(new Dimension(width, height));

        // Create the frame itself
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        // Pseudo-fullscreen if window fills the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setUndecorated(width == screenSize.width &&
                height == screenSize.height);
        frame.setContentPane(gamePanel);
        frame.pack();

        // Load the frame icon image.
        try {
            Image icon = ImageIO.read(
                    ClassLoader.getSystemResource("res/data/icon.png"));
            if (icon != null) {
                frame.setIconImage(icon);
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        // Make the frame visible.
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();
    }

    public void load() {

        LoadingScreen loadingScreen = new LoadingScreen(this);
        changeState(loadingScreen);

        while (!loadingScreen.isLoaded()){
            loadingScreen.continueLoading();
            render();
            try {
                // Don't hog the thread
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        while (!exiting) {
            long before = System.currentTimeMillis();

            pollInput();
            tick();
            render();

            int elapsed = (int) (System.currentTimeMillis() - before);
            int sleepTime = MS_PER_FRAME - elapsed;

            if (sleepTime < 1) {
                sleepTime = 1;
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void pollInput() {
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLoc, gamePanel);
        Input input = state.getInput();
        input.setMousePos(mouseLoc.x, mouseLoc.y);

        synchronized (input) {
            state.pollInput();
            input.consume();
        }
    }

    private void tick() {
        state.tick();
    }

    private void render() {

        // Clear the Canvas
        canvas.clear();

        // Render the state onto our Canvas
        state.getRenderer().render(canvas);

        // Render this Canvas to the screen
        gamePanel.getGraphics().drawImage(canvas.getImage(), 0, 0, null);
    }

    public void changeState(State newState) {

        if (state != null) {

            // Remove listeners from previous state
            Input input = state.getInput();
            gamePanel.removeMouseListener(input);
            frame.removeKeyListener(input);

            // Reset previous state
            state.destroy();
        }

        // Set new state
        state = newState;
        state.start();

        // Add listeners to new state
        Input input = state.getInput();
        gamePanel.addMouseListener(input);
        frame.addKeyListener(input);
    }

    public State getState() {
        return state;
    }

    public int getWidth() {
        return WINDOW_WIDTH;
    }

    public int getHeight() {
        return WINDOW_HEIGHT;
    }

}
