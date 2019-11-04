package client.res;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.PixelGrabber;

import javax.swing.JFrame;

public abstract class Fonts {

    private static final String ALLOWED_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
    private static final String CHARS_WITH_EXTRA_WIDTH = "ftwvkxyAVW";
    private static final int NUM_CHAR_PROPERTIES = 9;

    private static byte fontProperties[][] = new byte[50][];
    private static byte fontData[] = new byte[100000];
    private static boolean redraw[] = new boolean[12];
    private static int nextDataIndex;
    private static int charIndexes[] = new int[256];

    static {
        for (int i = 0; i < charIndexes.length; i++) {
            int index = ALLOWED_CHARS.indexOf(i);
            if (index == -1) {
                // Replace unknown characters with '='?
                index = 74;
            }
            charIndexes[i] = index * NUM_CHAR_PROPERTIES;
        }
    }
    public static void loadFonts(JFrame frame) {
        loadFont("helvetica", 11, Font.PLAIN, 0, frame, false, false);
        loadFont("helvetica", 12, Font.BOLD, 1, frame, false, false);
        loadFont("helvetica", 12, Font.PLAIN, 2, frame, false, false);
        loadFont("helvetica", 13, Font.BOLD, 3, frame, false, false);
        loadFont("helvetica", 14, Font.BOLD, 4, frame, false, false);
        loadFont("helvetica", 16, Font.BOLD, 5, frame, false, false);
        loadFont("helvetica", 20, Font.BOLD, 6, frame, false, false);
        loadFont("helvetica", 24, Font.BOLD, 7, frame, false, false);
    }

    private static void loadFont(String fontName, int size, int style,
            int fontNumber, JFrame frame, boolean fontWasRedrawn,
            boolean addCharWidth) {

        Font font = new Font("Helvetica", style, size);
        FontMetrics fontmetrics = frame.getFontMetrics(font);

        nextDataIndex = ALLOWED_CHARS.length() * NUM_CHAR_PROPERTIES;
        for (int i = 0; i < ALLOWED_CHARS.length(); i++) {
            drawLetter(font, fontmetrics, ALLOWED_CHARS.charAt(i), i, frame,
                    fontNumber * NUM_CHAR_PROPERTIES, addCharWidth);
        }

        // First 855 elements of fontProperties for a given font are the 9
        // properties of each character. This is followed by the pixel data of
        // each character (?).
        fontProperties[fontNumber] = new byte[nextDataIndex];
        for (int i = 0; i < nextDataIndex; i++) {
            fontProperties[fontNumber][i] = fontData[i];
        }

        if (style == Font.BOLD && redraw[fontNumber]) {
            redraw[fontNumber] = false;
            loadFont(fontName, size, Font.PLAIN, fontNumber, frame, true, false);
        }
        if (fontWasRedrawn && !redraw[fontNumber]) {
            redraw[fontNumber] = false;
            loadFont(fontName, size, Font.PLAIN, fontNumber, frame, false, true);
        }
    }

    public static void drawLetter(Font font, FontMetrics fontMetrics, char c,
            int charIndex, JFrame frame, int fontNumber, boolean addCharWidth) {

        // Determine properties of this character
        int charWidth = fontMetrics.charWidth(c);
        int oldCharWidth = charWidth;
        if (addCharWidth) {
            if (c == '/') {
                addCharWidth = false;
            }
            if (CHARS_WITH_EXTRA_WIDTH.indexOf(c) > 0){
                charWidth++;
            }
        }
        int maxAscent = fontMetrics.getMaxAscent();
        int maxCharHeight =
                fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent();
        int standardCharHeight = fontMetrics.getHeight();

        // Create a blank image
        Image image = frame.createImage(charWidth, maxCharHeight);

        // Draw the character (white on black)
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, charWidth, maxCharHeight);
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(String.valueOf(c), 0, maxAscent);
        if (addCharWidth) {
            g.drawString(String.valueOf(c), 1, maxAscent);
        }

        // Get pixels from image
        int pix[] = new int[charWidth * maxCharHeight];
        PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, charWidth,
                maxCharHeight, pix, 0, charWidth);
        try {
            pixelgrabber.grabPixels();
        } catch (InterruptedException ex) {
            return;
        }
        image.flush();

        // Determine character bounds
        int drawOffsetX = 0;
        int drawOffsetY = 0;
        int drawWidth = charWidth;
        int drawHeight = maxCharHeight;

        searchRowsDown:
            for (int y = 0; y < maxCharHeight; y++) {
                for (int x = 0; x < charWidth; x++) {
                    int col = pix[x + y * charWidth];
                    if ((col & 0xffffff) == 0) {
                        // Pixel has no colour
                        continue;
                    }
                    drawOffsetY = y;
                    break searchRowsDown;
                }

            }

        searchColumnsRight:
            for (int x = 0; x < charWidth; x++) {
                for (int y = 0; y < maxCharHeight; y++) {
                    int col = pix[x + y * charWidth];
                    if ((col & 0xffffff) == 0) {
                        // Pixel has no colour
                        continue;
                    }
                    drawOffsetX = x;
                    break searchColumnsRight;
                }

            }

            searchRowsUp:
                for (int y = maxCharHeight - 1; y >= 0; y--) {
                    for (int x = 0; x < charWidth; x++) {
                        int col = pix[x + y * charWidth];
                        if ((col & 0xffffff) == 0) {
                            // Pixel has no colour
                            continue;
                        }
                        drawHeight = y + 1;
                        break searchRowsUp;
                    }

                }
            searchColumnsLeft:
                for (int x = charWidth - 1; x >= 0; x--) {
                    for (int y = 0; y < maxCharHeight; y++) {
                        int col = pix[x + y * charWidth];
                        if ((col & 0xffffff) == 0) {
                            // Pixel has no colour
                            continue;
                        }
                        drawWidth = x + 1;
                        break searchColumnsLeft;
                    }

                }

                // Store character properties
                //     nextDataIndex: bits 11-18
                fontData[charIndex]     = (byte) (nextDataIndex >> 14);
                //     nextDataIndex: bits 19-26 (& excludes bit 18)
                fontData[charIndex + 1] = (byte) ((nextDataIndex >> 7) & 127);
                //     nextDataIndex: bits 27-32 (& excludes bit 26)
                fontData[charIndex + 2] = (byte) (nextDataIndex & 127);
                fontData[charIndex + 3] = (byte) (drawWidth - drawOffsetX);
                fontData[charIndex + 4] = (byte) (drawHeight - drawOffsetY);
                fontData[charIndex + 5] = (byte) drawOffsetX;
                fontData[charIndex + 6] = (byte) (maxAscent - drawOffsetY);
                fontData[charIndex + 7] = (byte) oldCharWidth;
                fontData[charIndex + 8] = (byte) standardCharHeight;

                // Store character pixel data
                for (int x = drawOffsetY; x < drawHeight; x++) {
                    for (int y = drawOffsetX; y < drawWidth; y++) {
                        int col = pix[y + x * charWidth] & 0xff;
                        if (col > 30 && col < 230) {
                            redraw[fontNumber] = true;
                        }
                        fontData[nextDataIndex] = (byte) col;
                        nextDataIndex++;
                    }
                }
    }

}
