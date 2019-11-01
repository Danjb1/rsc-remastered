package client;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import client.res.Resources;
import client.res.Sprite;

/**
 * Class responsible for storing and manipulating pixel data of a 2D image.
 *
 * <p><i>Based on <code>Surface.java</code> from other RSC sources.</i>
 *
 * @author Dan Bryce
 */
public class Canvas {

    private static final int COLOUR_BLACK = 0;

    private BufferedImage image;

    private int width;

    private int height;

    private int[] pixels;

    /**
     * Creates a Canvas that wraps a BufferedImage.
     *
     * @param image
     */
    public Canvas(BufferedImage image) {
        this.image = image;

        width = image.getWidth();
        height = image.getHeight();

        DataBufferInt buffer = (DataBufferInt) image.getRaster().getDataBuffer();
        pixels = buffer.getData();
    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = COLOUR_BLACK;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPixels() {
        return pixels;
    }

    /**
     * Draws the sprite with the given ID at the given position.
     *
     * @param x
     * @param y
     * @param id
     */
    public void drawSprite(int x, int y, int id) {

        Sprite sprite = Resources.getSprite(id);

        if (sprite.hasDrawOffset()) {
            x += sprite.getDrawOffsetX();
            y += sprite.getDrawOffsetY();
        }

        int targetIndex = x + y * width;
        int sourceIndex = 0;
        int spriteHeight = sprite.getHeight();
        int spriteWidth = sprite.getWidth();
        int screenRowIncrement = width - spriteWidth;
        int spriteRowIncrement = 0;

        /*
         * Bounds checking.
         *
         * If part of the Sprite is offscreen, this ensures that we only draw
         * the visible part of the image. Attempting to draw the full image
         * would result in parts of the image wrapping onto the next row of
         * pixels.
         */

        if (y < 0) {
            spriteHeight += y;
            sourceIndex -= y * spriteWidth;
            targetIndex -= y * width;
            y = 0;
        }

        if (y + spriteHeight >= height) {
            spriteHeight -= ((y + spriteHeight) - height) + 1;
        }

        if (x < 0) {
            spriteWidth += x;
            sourceIndex -= x;
            targetIndex -= x;
            spriteRowIncrement -= x;
            screenRowIncrement -= x;
            x = 0;
        }

        if (x + spriteWidth >= width) {
            int adjustment = ((x + spriteWidth) - width) + 1;
            spriteWidth -= adjustment;
            spriteRowIncrement += adjustment;
            screenRowIncrement += adjustment;
        }

        if (spriteWidth <= 0 || spriteHeight <= 0) {
            return;
        }

        setPixels(pixels, sprite.getPixels(),
                sourceIndex, targetIndex,
                spriteWidth, spriteHeight,
                screenRowIncrement, spriteRowIncrement);
    }

    /**
     * Copies a block of pixels from the source to the target.
     *
     * @param target Target pixel data.
     * @param source Source pixel data.
     * @param sourceIndex Starting index for the source array.
     * @param targetIndex Starting index for the target array.
     * @param sourceWidth Width of the source image.
     * @param sourceHeight Height of the source image.
     * @param targetRowIncrement
     *      Value to add to the target index after each row is copied.
     * @param sourceRowIncrement
     *      Value to add to the source index after each row is copied.
     */
    private static void setPixels(
            int target[], int source[],
            int sourceIndex, int targetIndex,
            int sourceWidth, int sourceHeight,
            int targetRowIncrement, int sourceRowIncrement) {

        /*
         * The original source code copied multiple pixels at a time inside the
         * loop body, presumably intended as some kind of optimisation. Here I
         * have favoured simplicity over efficiency.
         */
        for (int y = 0; y < sourceHeight; y++) {
            for (int x = 0; x < sourceWidth; x++) {

                int colour = source[sourceIndex];
                if (colour != 0) {
                    target[targetIndex] = colour;
                }

                sourceIndex++;
                targetIndex++;
            }

            targetIndex += targetRowIncrement;
            sourceIndex += sourceRowIncrement;
        }
    }

    public void spriteClip(int x, int y, int width, int height, int id) {

        Sprite sprite = Resources.getSprite(id);
        int j1 = sprite.getWidth();
        int k1 = sprite.getHeight();
        int l1 = 0;
        int i2 = 0;
        int j2 = (j1 << 16) / width;
        int k2 = (k1 << 16) / height;
        if (sprite.hasDrawOffset()) {
            int l2 = sprite.getTextureWidth();
            int j3 = sprite.getTextureHeight();
            j2 = (l2 << 16) / width;
            k2 = (j3 << 16) / height;
            x += ((sprite.getDrawOffsetX() * width + l2) - 1) / l2;
            y += ((sprite.getDrawOffsetY() * height + j3) - 1) / j3;
            if ((sprite.getDrawOffsetX() * width) % l2 != 0) {
                l1 = (l2 - (sprite.getDrawOffsetX() * width) % l2 << 16) / width;
            }
            if ((sprite.getDrawOffsetY() * height) % j3 != 0) {
                i2 = (j3 - (sprite.getDrawOffsetY() * height) % j3 << 16) / height;
            }
            width = (width * (sprite.getWidth() - (l1 >> 16))) / l2;
            height = (height * (sprite.getHeight() - (i2 >> 16))) / j3;
        }
        int i3 = x + y * this.width;
        int k3 = this.width - width;
        if (y < 0) {
            int l3 = 0 - y;
            height -= l3;
            y = 0;
            i3 += l3 * this.width;
            i2 += k2 * l3;
        }
        if (y + height >= this.height) {
            height -= ((y + height) - this.height) + 1;
        }
        if (x < 0) {
            int i4 = 0 - x;
            width -= i4;
            x = 0;
            i3 += i4;
            l1 += j2 * i4;
            k3 += i4;
        }
        if (x + width >= this.width) {
            int j4 = ((x + width) - this.width) + 1;
            width -= j4;
            k3 += j4;
        }
        byte byte0 = 1;
        plotSale(sprite.getPixels(), 0, l1, i2, i3, k3, width, height, j2, k2, j1, byte0);
    }

    private void plotSale(int texturePixels[], int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2,
            int j2, int k2) {

        int l2 = j;
        for (int i3 = -k1; i3 < 0; i3 += k2) {
            int j3 = (k >> 16) * j2;
            for (int k3 = -j1; k3 < 0; k3++) {
                i = texturePixels[(j >> 16) + j3];
                if (i != 0) {
                    pixels[l++] = i;
                } else {
                    l++;
                }
                j += l1;
            }

            k += i2;
            j = l2;
            l += i1;
        }
    }

    /**
     * Draws a textured scanline.
     *
     * Used for walls and roofs.
     *
     * @param texturePixels
     * @param i
     * @param j
     * @param paramA
     * @param paramB
     * @param paramC
     * @param paramAModifier
     * @param paramBModifier
     * @param paramCModifier
     * @param length
     * @param pxOffset
     * @param paramD
     * @param paramDModifier
     */
    public void renderScanline_LargeTexture(
            int texturePixels[],
            int i,
            int j,
            int paramA,
            int paramB,
            int paramC,
            int paramAModifier,
            int paramBModifier,
            int paramCModifier,
            int length,
            int pxOffset,
            int paramD,
            int paramDModifier) {

        if (length <= 0) {
            return;
        }

        int i3 = 0;
        int j3 = 0;
        int colorShift = 0;

        if (paramC != 0) {
            i = paramA / paramC << 7;
            j = paramB / paramC << 7;
        }

        // Bounds checking
        if (i < 0) {
            i = 0;
        } else if (i > 16256) {
            i = 16256;
        }

        paramA += paramAModifier;
        paramB += paramBModifier;
        paramC += paramCModifier;

        if (paramC != 0) {
            i3 = paramA / paramC << 7;
            j3 = paramB / paramC << 7;
        }

        // Bounds checking
        if (i3 < 0) {
            i3 = 0;
        } else if (i3 > 16256) {
            i3 = 16256;
        }

        int k3 = i3 - i >> 4;
        int l3 = j3 - j >> 4;

        // Draw 16 pixels with each loop iteration
        for (int j4 = 0; j4 < length >> 4; j4++) {

            // These next sections could be rolled up into 2 nested for-loops

            i += paramD & 0x600000;
            colorShift = paramD >> 23;
            paramD += paramDModifier;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;

            i += k3;
            j += l3;
            i = (i & 0x3fff) + (paramD & 0x600000);
            colorShift = paramD >> 23;
            paramD += paramDModifier;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;

            i += k3;
            j += l3;
            i = (i & 0x3fff) + (paramD & 0x600000);
            colorShift = paramD >> 23;
            paramD += paramDModifier;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;

            i += k3;
            j += l3;
            i = (i & 0x3fff) + (paramD & 0x600000);
            colorShift = paramD >> 23;
            paramD += paramDModifier;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;

            i = i3;
            j = j3;
            paramA += paramAModifier;
            paramB += paramBModifier;
            paramC += paramCModifier;

            if (paramC != 0) {
                i3 = paramA / paramC << 7;
                j3 = paramB / paramC << 7;
            }

            // Bounds checking
            if (i3 < 0) {
                i3 = 0;
            } else if (i3 > 16256) {
                i3 = 16256;
            }

            k3 = i3 - i >> 4;
            l3 = j3 - j >> 4;
        }

        // Render the last part of the scanline
        for (int k4 = 0; k4 < (length & 0xf); k4++) {
            if ((k4 & 3) == 0) {
                i = (i & 0x3fff) + (paramD & 0x600000);
                colorShift = paramD >> 23;
                paramD += paramDModifier;
            }
            pixels[pxOffset++] = texturePixels[(j & 0x3f80) + (i >> 7)] >>> colorShift;
            i += k3;
            j += l3;
        }
    }

    public void renderScanline_LargeTranslucentTexture(
            int texturePixels[],
            int texOffset,
            int texStart,
            int k,
            int l,
            int i1,
            int j1,
            int k1,
            int l1,
            int length,
            int pxIndex,
            int k2,
            int l2) {

        if (length <= 0) {
            return;
        }

        int i3 = 0;
        int j3 = 0;
        int colorShift = 0;

        if (i1 != 0) {
            texOffset = k / i1 << 7;
            texStart = l / i1 << 7;
        }

        if (texOffset < 0) {
            texOffset = 0;
        } else if (texOffset > 16256) {
            texOffset = 16256;
        }

        k += j1;
        l += k1;
        i1 += l1;

        if (i1 != 0) {
            i3 = k / i1 << 7;
            j3 = l / i1 << 7;
        }

        if (i3 < 0) {
            i3 = 0;
        } else if (i3 > 16256) {
            i3 = 16256;
        }

        int texOffsetStride = i3 - texOffset >> 4;
        int texStartStride = j3 - texStart >> 4;

        for (int j4 = length >> 4; j4 > 0; j4--) {

            texOffset += k2 & 0x600000;
            colorShift = k2 >> 23;
            k2 += l2;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;

            texOffset = (texOffset & 0x3fff) + (k2 & 0x600000);
            colorShift = k2 >> 23;
            k2 += l2;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;

            texOffset = (texOffset & 0x3fff) + (k2 & 0x600000);
            colorShift = k2 >> 23;
            k2 += l2;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;

            texOffset = (texOffset & 0x3fff) + (k2 & 0x600000);
            colorShift = k2 >> 23;
            k2 += l2;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset = i3;
            texStart = j3;

            k += j1;
            l += k1;
            i1 += l1;

            if (i1 != 0) {
                i3 = k / i1 << 7;
                j3 = l / i1 << 7;
            }

            if (i3 < 0) {
                i3 = 0;
            } else if (i3 > 16256) {
                i3 = 16256;
            }

            texOffsetStride = i3 - texOffset >> 4;
            texStartStride = j3 - texStart >> 4;
        }

        // Render the last part of the scanline
        for (int k4 = 0; k4 < (length & 0xf); k4++) {
            if ((k4 & 3) == 0) {
                texOffset = (texOffset & 0x3fff) + (k2 & 0x600000);
                colorShift = k2 >> 23;
                k2 += l2;
            }
            pixels[pxIndex++] = (texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            texOffset += texOffsetStride;
            texStart += texStartStride;
        }
    }

    public void renderScanline_LargeTextureWithTransparency(
            int i,
            int texOffset,
            int texStart,
            int texturePixels[],
            int l,
            int i1,
            int j1,
            int k1,
            int l1,
            int i2,
            int length,
            int pxIndex,
            int l2,
            int i3) {

        if (length <= 0) {
            return;
        }

        int j3 = 0;
        int k3 = 0;
        i3 <<= 2;

        if (j1 != 0) {
            j3 = l / j1 << 7;
            k3 = i1 / j1 << 7;
        }

        if (j3 < 0) {
            j3 = 0;
        } else if (j3 > 16256) {
            j3 = 16256;
        }

        for (int j4 = length; j4 > 0; j4 -= 16) {

            l += k1;
            i1 += l1;
            j1 += i2;
            texOffset = j3;
            texStart = k3;

            if (j1 != 0) {
                j3 = l / j1 << 7;
                k3 = i1 / j1 << 7;
            }

            if (j3 < 0) {
                j3 = 0;
            } else if (j3 > 16256) {
                j3 = 16256;
            }

            int texOffsetStride = j3 - texOffset >> 4;
            int texStartStride = k3 - texStart >> 4;
            int colorShift = l2 >> 23;

            texOffset += l2 & 0x600000;
            l2 += i3;

            if (j4 < 16) {

                // Render fewer than 16 pixels

                for (int l4 = 0; l4 < j4; l4++) {

                    if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                        pixels[pxIndex] = i;
                    }

                    pxIndex++;
                    texOffset += texOffsetStride;
                    texStart += texStartStride;

                    if ((l4 & 3) == 3) {
                        texOffset = (texOffset & 0x3fff) + (l2 & 0x600000);
                        colorShift = l2 >> 23;
                        l2 += i3;
                    }
                }

            } else {

                // Render 16 pixels

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                texOffset = (texOffset & 0x3fff) + (l2 & 0x600000);
                colorShift = l2 >> 23;
                l2 += i3;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                texOffset = (texOffset & 0x3fff) + (l2 & 0x600000);
                colorShift = l2 >> 23;
                l2 += i3;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                texOffset = (texOffset & 0x3fff) + (l2 & 0x600000);
                colorShift = l2 >> 23;
                l2 += i3;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;

                if ((i = texturePixels[(texStart & 0x3f80) + (texOffset >> 7)] >>> colorShift) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
            }
        }
    }

    /*
     * Used for wooden floors!
     */
    public void renderScanline_SmallTexture(
            int texturePixels[],
            int texOffset,
            int texStart,
            int k,
            int l,
            int i1,
            int j1,
            int k1,
            int l1,
            int length,
            int pxIndex,
            int k2,
            int l2) {

        if (length <= 0) {
            return;
        }

        int i3 = 0;
        int j3 = 0;
        l2 <<= 2;
        if (i1 != 0) {
            i3 = k / i1 << 6;
            j3 = l / i1 << 6;
        }
        if (i3 < 0) {
            i3 = 0;
        } else if (i3 > 4032) {
            i3 = 4032;
        }
        for (int i4 = length; i4 > 0; i4 -= 16) {
            k += j1;
            l += k1;
            i1 += l1;
            texOffset = i3;
            texStart = j3;
            if (i1 != 0) {
                i3 = k / i1 << 6;
                j3 = l / i1 << 6;
            }
            if (i3 < 0) {
                i3 = 0;
            } else if (i3 > 4032) {
                i3 = 4032;
            }
            int texOffsetStride = i3 - texOffset >> 4;
            int texStartStride = j3 - texStart >> 4;
            int colorShift = k2 >> 20;
            texOffset += k2 & 0xc0000;
            k2 += l2;
            if (i4 < 16) {
                for (int k4 = 0; k4 < i4; k4++) {
                    pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                    texOffset += texOffsetStride;
                    texStart += texStartStride;
                    if ((k4 & 3) == 3) {
                        texOffset = (texOffset & 0xfff) + (k2 & 0xc0000);
                        colorShift = k2 >> 20;
                        k2 += l2;
                    }
                }

            } else {
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (k2 & 0xc0000);
                colorShift = k2 >> 20;
                k2 += l2;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (k2 & 0xc0000);
                colorShift = k2 >> 20;
                k2 += l2;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (k2 & 0xc0000);
                colorShift = k2 >> 20;
                k2 += l2;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift;
            }
        }

    }

    public void renderScanline_SmallTranslucentTexture(
            int texturePixels[],
            int texOffset,
            int texStart,
            int k,
            int l,
            int i1,
            int j1,
            int k1,
            int l1,
            int length,
            int pxIndex,
            int k2,
            int l2) {

        if (length <= 0) {
            return;
        }

        int i3 = 0;
        int j3 = 0;
        l2 <<= 2;
        if (i1 != 0) {
            i3 = k / i1 << 6;
            j3 = l / i1 << 6;
        }
        if (i3 < 0) {
            i3 = 0;
        } else if (i3 > 4032) {
            i3 = 4032;
        }
        for (int i4 = length; i4 > 0; i4 -= 16) {
            k += j1;
            l += k1;
            i1 += l1;
            texOffset = i3;
            texStart = j3;
            if (i1 != 0) {
                i3 = k / i1 << 6;
                j3 = l / i1 << 6;
            }
            if (i3 < 0) {
                i3 = 0;
            } else if (i3 > 4032) {
                i3 = 4032;
            }
            int texOffsetStride = i3 - texOffset >> 4;
            int texStartStride = j3 - texStart >> 4;
            int colorShift = k2 >> 20;
            texOffset += k2 & 0xc0000;
            k2 += l2;
            if (i4 < 16) {
                for (int k4 = 0; k4 < i4; k4++) {
                    pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                    texOffset += texOffsetStride;
                    texStart += texStartStride;
                    if ((k4 & 3) == 3) {
                        texOffset = (texOffset & 0xfff) + (k2 & 0xc0000);
                        colorShift = k2 >> 20;
                        k2 += l2;
                    }
                }

            } else {
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (k2 & 0xc0000);
                colorShift = k2 >> 20;
                k2 += l2;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (k2 & 0xc0000);
                colorShift = k2 >> 20;
                k2 += l2;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (k2 & 0xc0000);
                colorShift = k2 >> 20;
                k2 += l2;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
                texOffset += texOffsetStride;
                texStart += texStartStride;
                pixels[pxIndex++] = (texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> colorShift) + (pixels[pxIndex] >> 1 & 0x7f7f7f);
            }
        }

    }

    public void renderScanline_SmallTextureWithTransparency(
            int texturePixels[],
            int l,
            int i1,
            int j1,
            int k1,
            int l1,
            int i2,
            int length,
            int pxIndex,
            int l2,
            int i3) {

        if (length <= 0) {
            return;
        }

        int i = 0;
        int texOffset = 0;
        int texStart = 0;
        int j3 = 0;
        int k3 = 0;
        i3 <<= 2;
        if (j1 != 0) {
            j3 = l / j1 << 6;
            k3 = i1 / j1 << 6;
        }
        if (j3 < 0) {
            j3 = 0;
        } else if (j3 > 4032) {
            j3 = 4032;
        }
        for (int j4 = length; j4 > 0; j4 -= 16) {
            l += k1;
            i1 += l1;
            j1 += i2;
            texOffset = j3;
            texStart = k3;
            if (j1 != 0) {
                j3 = l / j1 << 6;
                k3 = i1 / j1 << 6;
            }
            if (j3 < 0) {
                j3 = 0;
            } else if (j3 > 4032) {
                j3 = 4032;
            }
            int texOffsetStride = j3 - texOffset >> 4;
            int texStartStride = k3 - texStart >> 4;
            int k4 = l2 >> 20;
            texOffset += l2 & 0xc0000;
            l2 += i3;
            if (j4 < 16) {
                for (int l4 = 0; l4 < j4; l4++) {
                    if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                        pixels[pxIndex] = i;
                    }
                    pxIndex++;
                    texOffset += texOffsetStride;
                    texStart += texStartStride;
                    if ((l4 & 3) == 3) {
                        texOffset = (texOffset & 0xfff) + (l2 & 0xc0000);
                        k4 = l2 >> 20;
                        l2 += i3;
                    }
                }

            } else {
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (l2 & 0xc0000);
                k4 = l2 >> 20;
                l2 += i3;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (l2 & 0xc0000);
                k4 = l2 >> 20;
                l2 += i3;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                texOffset = (texOffset & 0xfff) + (l2 & 0xc0000);
                k4 = l2 >> 20;
                l2 += i3;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
                texOffset += texOffsetStride;
                texStart += texStartStride;
                if ((i = texturePixels[(texStart & 0xfc0) + (texOffset >> 6)] >>> k4) != 0) {
                    pixels[pxIndex] = i;
                }
                pxIndex++;
            }
        }

    }

    /*
     * No idea what this is used for.
     */
    public void renderScanline_TranslucentGradient(int length, int pxIndex, int gradient[], int gradientIndex, int stride) {

        if (length < 0) {
            return;
        }

        int color = 0;

        for (int i = 0; i < length; i++) {

            // Colour changes every 4 pixels
            if (i % 4 == 0) {
                color = gradient[gradientIndex >> 8 & 0xff];
                gradientIndex += stride;
            }

            pixels[pxIndex++] = color + (pixels[pxIndex] >> 1 & 0x7f7f7f);
        }
    }

    /*
     * Used for terrain!
     */
    public void renderScanline_Gradient(int length, int pxIndex, int gradient[], int gradientIndex, int stride) {

        if (length < 0) {
            return;
        }

        int color = 0;

        for (int i = 0; i < length; i++) {

            // Colour changes every 4 pixels
            if (i % 4 == 0) {
                color = gradient[gradientIndex >> 8 & 0xff];
                gradientIndex += stride;
            }

            pixels[pxIndex++] = color;
        }
    }

    public void drawLineX(int x1, int y, int x2, int colour) {

        if (y < 0 || y >= height) {
            // Line is outside the image bounds
            return;
        }

        if (x1 < 0) {
            // Ensure we don't start outside the image bounds
            x2 -= 0 - x1;
            x1 = 0;
        }

        if (x1 + x2 > width) {
            // Ensure we don't finish outside the image bounds
            x2 = width - x1;
        }

        int startIndex = x1 + y * width;

        for (int i = 0; i < x2; i++) {
            pixels[startIndex + i] = colour;
        }
    }

}
