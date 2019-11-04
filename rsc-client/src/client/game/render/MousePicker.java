package client.game.render;

import client.game.scene.Model;

public class MousePicker {

    private static final int MAX_MOUSE_PICKS = 100;

    private int mouseX;
    private int mouseY;

    private int baseX;

    private int mousePickedCount;
    private Model mousePickedModels[] = new Model[MAX_MOUSE_PICKS];
    private int mousePickedFaces[] = new int[MAX_MOUSE_PICKS];

    public MousePicker(int baseX) {
        this.baseX = baseX;
    }

    public void add(Model gameModel, int faceId) {
        mousePickedModels[mousePickedCount] = gameModel;
        mousePickedFaces[mousePickedCount] = faceId;
        mousePickedCount++;
    }

    public int getMousePickedCount() {
        return mousePickedCount;
    }

    public Model[] getMousePickedModels() {
        return mousePickedModels;
    }

    public int[] getMousePickedFaces() {
        return mousePickedFaces;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    // Must be called every frame for mouse picking to be accurate!
    public void setMousePos(int mouseX, int mouseY) {
        this.mouseX = mouseX - baseX;
        this.mouseY = mouseY;
        mousePickedCount = 0;
    }

}
