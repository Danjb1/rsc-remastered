package client.res;

/**
 * Class representing a texture that can be used in rendering.
 *
 * @author Dan Bryce
 */
public class Texture {

    /**
     * Pixel data linking to the palette array.
     *
     * Cleared after the Texture has been loaded.
     */
    public byte colourData[];

    /**
     * List of colours used by this Texture.
     *
     * Cleared after the Texture has been loaded.
     */
    public int palette[];

    /**
     * Pixel colours.
     */
    public int pixels[];

    /**
     * Whether or not this texture contains any transparent pixels.
     */
    private boolean hasTransparency;

    /**
     * Whether or not the texture is a "large" texture (128x128).
     */
    private boolean large;

    public Texture(byte[] colourData, int[] palette, boolean large) {
        this.colourData = colourData;
        this.palette = palette;
        this.large = large;
        this.hasTransparency = false;
        this.pixels = null;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public boolean hasTransparency() {
        return hasTransparency;
    }

    public boolean isLarge() {
        return large;
    }

}
