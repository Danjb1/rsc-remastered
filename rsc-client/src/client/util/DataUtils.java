package client.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class DataUtils {

    /**
     * Returns a ByteBuffer containing all available data from the given
     * InputStream.
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static final ByteBuffer streamToBuffer(BufferedInputStream in)
            throws IOException {
        byte[] buffer = new byte[in.available()];
        in.read(buffer, 0, buffer.length);
        return ByteBuffer.wrap(buffer);
    }

    public static int getUnsignedShort(byte abyte0[], int i) {
        return ((abyte0[i] & 0xff) << 8) + (abyte0[i + 1] & 0xff);
    }

    public static int getUnsignedByte(byte byte0) {
        return byte0 & 0xff;
    }

    public static int getSigned2Bytes(byte abyte0[], int i) {
        int j = getUnsignedByte(abyte0[i]) * 256 + getUnsignedByte(abyte0[i + 1]);
        if (j > 32767) {
            j -= 0x10000;
        }
        return j;
    }

    public static InputStream streamFromPath(String path) throws IOException {
        return new BufferedInputStream(new FileInputStream(path));
    }

    public static int rgbToInt(int r, int g, int b) {
        return -1 - (r / 8) * 1024 - (g / 8) * 32 - (b / 8);
    }

}
