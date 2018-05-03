package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Listens for input in the UI thread and makes it available to the main thread.
 * 
 * @author Dan Bryce
 */
public class Input implements KeyListener, MouseListener {

    private int mouseX;
    private int mouseY;

    private boolean leftClickReleased;
    private List<Integer> keysReleased = new ArrayList<>();

    /**
     * Clears all input.
     * 
     * This should be called every frame after input processing. This should be
     * called in sychronized block because new input could be added at any time!
     */
    public void clear() {
        leftClickReleased = false;
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

    public boolean wasKeyReleased(int keyId) {
        return keysReleased.contains(keyId);
    }

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

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        synchronized (this) {
            keysReleased.add(e.getKeyCode());
        }
    }
    
}
