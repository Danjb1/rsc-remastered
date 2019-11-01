package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Listens for input in the UI thread and makes it available to the main thread.
 *
 * @author Dan Bryce
 */
public class Input implements KeyListener, MouseListener {

    private int mouseX;
    private int mouseY;

    private boolean leftClickReleased;
    private List<Integer> keysPressed = new ArrayList<>();
    private List<Integer> keysReleased = new ArrayList<>();
    private Set<Integer> keysDown = new HashSet<>();

    /**
     * Consumes all input for the current frame.
     *
     * <p>This should be called every frame after input processing.
     *
     * <p>Synchronized because new input could be added at any time!
     */
    public synchronized void consume() {
        leftClickReleased = false;
        keysPressed.clear();
        keysReleased.clear();
    }

    public void setMousePos(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public boolean wasLeftClickReleased() {
        return leftClickReleased;
    }

    public boolean wasKeyPressed(int keyId) {
        return keysPressed.contains(keyId);
    }

    public boolean wasKeyReleased(int keyId) {
        return keysReleased.contains(keyId);
    }

    public boolean isKeyDown(int key) {
        return keysDown.contains(key);
    }

    ////////////////////////////////////////////////////////////////////////////
    // MouseListener methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (this) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                leftClickReleased = true;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    ////////////////////////////////////////////////////////////////////////////
    // KeyListener methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (this) {
            keysPressed.add(e.getKeyCode());
            keysDown.add(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        synchronized (this) {
            keysReleased.add(e.getKeyCode());
            keysDown.remove(e.getKeyCode());
        }
    }

}
