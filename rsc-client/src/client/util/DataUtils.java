package client.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DataUtils {

    /**
     * Returns a ByteBuffer containing all available data from the given
     * InputStream.
     *
     * @return
     * @throws IOException
     */
    public static final ByteBuffer streamToBuffer(BufferedInputStream in)
            throws IOException {
        byte[] buffer = new byte[in.available()];
        in.read(buffer, 0, buffer.length);
        return ByteBuffer.wrap(buffer);
    }

}
