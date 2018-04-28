package client;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class State implements 
        KeyListener,
        MouseListener {
    
    protected int mouseX;
    protected int mouseY;
    
    private MouseEvent lastMouseEvent;

    public void pollInput() {}

    public void discardUnusedInput() {
        lastMouseEvent = null;
    }

    public void tick() {}

    public abstract void render(Canvas canvas, Graphics g);

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        lastMouseEvent = e;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        lastMouseEvent = e;
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
    public void keyReleased(KeyEvent e) {}

    public void setMousePos(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    protected boolean wasLeftClickReleased() {
        return lastMouseEvent != null &&
                lastMouseEvent.getButton() == MouseEvent.BUTTON1 &&
                lastMouseEvent.getID() == MouseEvent.MOUSE_RELEASED;
    }

}
