package client.game.render;

import client.Canvas;
import client.game.ui.Menu;

public class InventoryMenuRenderer extends MenuRenderer {

    public InventoryMenuRenderer(Menu menu) {
        super(menu);
    }

    @Override
    protected void renderContents(Canvas canvas, int x, int y) {
//        for (int j = 0; j < inventorySize; j++) {
//            int k = i + (j % 5) * 49;
//            int i1 = 36 + (j / 5) * 34;
//            if (j < numItemsInInventory && wearing[j] == 1) {
//                gameGraphics.drawBoxAlpha(k, i1, 49, 34, 0xff0000, 128);
//            } else {
//                gameGraphics.drawBoxAlpha(k, i1, 49, 34, Surface.convertRGBToLong(181, 181, 181), 128);
//            }
//            if (j < numItemsInInventory) {
//                gameGraphics.spriteClip4(k, i1, 48, 32,
//                        SPRITE_ITEM_START + EntityHandler.getItemDef(inventoryItems[j]).getSprite(),
//                        EntityHandler.getItemDef(inventoryItems[j]).getPictureMask(), 0, 0, false);
//                if (EntityHandler.getItemDef(inventoryItems[j]).isStackable()) {
//                    gameGraphics.drawString(String.valueOf(inventoryItemsCount[j]), k + 1, i1 + 10, 1, 0xffff00);
//                }
//            }
//        }
//
//        for (int l = 1; l <= 4; l++) {
//            gameGraphics.drawLineY(i + l * 49, 36, (inventorySize / 5) * 34, 0);
//        }
//
//        for (int j1 = 1; j1 <= inventorySize / 5 - 1; j1++) {
//            gameGraphics.drawLineX(i, 36 + j1 * 34, 245, 0);
//        }
    }

}
