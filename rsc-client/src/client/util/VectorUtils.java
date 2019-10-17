package client.util;

import org.joml.Vector3i;

public class VectorUtils {

    public static int magnitude(Vector3i v) {
        return (int) Math.round(v.length());
    }

}
