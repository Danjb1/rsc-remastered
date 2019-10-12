package client.res;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Sprite {

    private static final int TRANSPARENT = Color.BLACK.getRGB();

    private int[] pixels;
    private int width;
    private int height;

    private String packageName = "unknown";
    private int id = -1;

    private boolean hasDrawOffset;
    private int drawOffsetX = 0;
    private int drawOffsetY = 0;

    private int textureWidthMaybe;
    private int textureHeightMaybe;

    public Sprite() {
        pixels = new int[0];
        width = 0;
        height = 0;
    }

    public Sprite(int[] pixels, int width, int height) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    public void setTextureDimensions(int textureWidthMaybe, int textureHeightMaybe) {
        this.textureWidthMaybe = textureWidthMaybe;
        this.textureHeightMaybe = textureHeightMaybe;
    }

    public int getTextureWidth() {
        return textureWidthMaybe;
    }

    public int getTextureHeight() {
        return textureHeightMaybe;
    }

    public void setName(int id, String packageName) {
        this.id = id;
        this.packageName = packageName;
    }

    public int getID() {
        return id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setDrawOffset(int drawOffsetX, int drawOffsetY) {
        this.drawOffsetX = drawOffsetX;
        this.drawOffsetY = drawOffsetY;
    }

    public void setHasDrawOffset(boolean hasDrawOffset) {
        this.hasDrawOffset = hasDrawOffset;
    }

    public boolean hasDrawOffset() {
        return hasDrawOffset;
    }

    public int getDrawOffsetX() {
        return drawOffsetX;
    }

    public int getDrawOffsetY() {
        return drawOffsetY;
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getPixel(int i) {
        return pixels[i];
    }

    public void setPixel(int i, int val) {
        pixels[i] = val;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "id = " + id + "; package = " + packageName;
    }

    /*
     * IO Operations
     */

    public void serializeTo(File file) throws IOException {
        ResourceLoader.writeData(file, this);
    }

    public BufferedImage toImage() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                img.setRGB(x, y, pixels[x + y * width]);
            }
        }
        return img;
    }

    /**
     * WARNING: packageName, id, xShift, yShift, something1, something2 are lost
     * when loading from img.
     *
     * @param img
     * @return
     */
    public static Sprite fromImage(BufferedImage img) {
        int[] pixels = new int[img.getWidth() * img.getHeight()];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                if (rgb == TRANSPARENT) {
                    rgb = 0;
                }
                pixels[x + y * img.getWidth()] = rgb;
            }
        }
        return new Sprite(pixels, img.getWidth(), img.getHeight());
    }

    /**
     * Writes the Sprite's raw data into a ByteBuffer
     *
     * @return
     * @throws IOException
     */
    public ByteBuffer serialise() throws IOException {
        ByteBuffer out = ByteBuffer.allocate(25 + (pixels.length * 4));

        out.putInt(width);
        out.putInt(height);

        out.put((byte) (hasDrawOffset ? 1 : 0));
        out.putInt(drawOffsetX);
        out.putInt(drawOffsetY);

        out.putInt(textureWidthMaybe);
        out.putInt(textureHeightMaybe);

        for (int c = 0; c < pixels.length; c++) {
            out.putInt(pixels[c]);
        }

        out.flip();
        return out;
    }

    /**
     * Create a new sprite from raw data packed into the given ByteBuffer
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static Sprite deserialise(ByteBuffer in) throws IOException {

        if (in.remaining() < 25) {
            throw new IOException("Provided buffer too short - Headers missing");
        }
        int width = in.getInt();
        int height = in.getInt();

        boolean requiresShift = in.get() == 1;
        int xShift = in.getInt();
        int yShift = in.getInt();

        int textureWidth = in.getInt();
        int textureHeight = in.getInt();

        int[] pixels = new int[width * height];
        if (in.remaining() < (pixels.length * 4)) {
            throw new IOException("Provided buffer too short - Pixels missing");
        }
        for (int c = 0; c < pixels.length; c++) {
            pixels[c] = in.getInt();
        }

        Sprite sprite = new Sprite(pixels, width, height);
        sprite.setHasDrawOffset(requiresShift);
        sprite.setDrawOffset(xShift, yShift);
        sprite.setTextureDimensions(textureWidth, textureHeight);

        return sprite;
    }

}