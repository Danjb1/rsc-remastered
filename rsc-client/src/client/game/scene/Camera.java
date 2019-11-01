package client.game.scene;

/**
 * Class representing the camera within the game world.
 *
 * @author Dan Bryce
 */
public class Camera {

    public static final int DEFAULT_HEIGHT = 550;

    public static final int DEFAULT_PITCH = 912;

    public static final int DEFAULT_FOG_DISTANCE = 2300;

    private static int sin2048Cache[] = new int[2048];
    private static int sin512Cache[] = new int[512];

    static {
        for (int i1 = 0; i1 < 256; i1++) {
            sin512Cache[i1] = (int) (Math.sin(i1 * 0.02454369D) * 32768D);
            sin512Cache[i1 + 256] = (int) (Math.cos(i1 * 0.02454369D) * 32768D);
        }
        for (int j1 = 0; j1 < 1024; j1++) {
            sin2048Cache[j1] = (int) (Math.sin(j1 * 0.00613592315D) * 32768D);
            sin2048Cache[j1 + 1024] = (int) (Math.cos(j1 * 0.00613592315D) * 32768D);
        }
    }

    private int x;
    private int y;
    private int z;
    private int yaw;
    private int pitch;
    private int roll;

    private int frustumMaxX;
    private int frustumMinX;
    private int frustumMinY;
    private int frustumMaxY;
    private int frustumFarZ;
    private int frustumNearZ;

    public void prepareForRendering(int clipX, int clipY, int clipFar3d, int clipXModified, int clipYModified) {
        frustumMaxX = 0;
        frustumMinX = 0;
        frustumMinY = 0;
        frustumMaxY = 0;
        frustumFarZ = 0;
        frustumNearZ = 0;
        setFrustum(-clipXModified, -clipYModified, clipFar3d);
        setFrustum(-clipXModified, clipYModified, clipFar3d);
        setFrustum(clipXModified, -clipYModified, clipFar3d);
        setFrustum(clipXModified, clipYModified, clipFar3d);
        setFrustum(-clipX, -clipY, 0);
        setFrustum(-clipX, clipY, 0);
        setFrustum(clipX, -clipY, 0);
        setFrustum(clipX, clipY, 0);
        frustumMaxX += x;
        frustumMinX += x;
        frustumMinY += y;
        frustumMaxY += y;
        frustumFarZ += z;
        frustumNearZ += z;
    }

    public void set(int x, int y, int z, int pitch, int yaw, int roll, int cameraHeight) {
        pitch &= 0x3ff;
        yaw &= 0x3ff;
        roll &= 0x3ff;
        this.yaw = 1024 - pitch & 0x3ff;
        this.pitch = 1024 - yaw & 0x3ff;
        this.roll = 1024 - roll & 0x3ff;
        int l1 = 0;
        int i2 = 0;
        int j2 = cameraHeight;
        if (pitch != 0) {
            int k2 = sin2048Cache[pitch];
            int j3 = sin2048Cache[pitch + 1024];
            int i4 = i2 * j3 - j2 * k2 >> 15;
            j2 = i2 * k2 + j2 * j3 >> 15;
            i2 = i4;
        }
        if (yaw != 0) {
            int l2 = sin2048Cache[yaw];
            int k3 = sin2048Cache[yaw + 1024];
            int j4 = j2 * l2 + l1 * k3 >> 15;
            j2 = j2 * k3 - l1 * l2 >> 15;
            l1 = j4;
        }
        if (roll != 0) {
            int i3 = sin2048Cache[roll];
            int l3 = sin2048Cache[roll + 1024];
            int k4 = i2 * i3 + l1 * l3 >> 15;
            i2 = i2 * l3 - l1 * i3 >> 15;
            l1 = k4;
        }
        this.x = x - l1;
        this.y = y - i2;
        this.z = z - j2;
    }

    public void setFrustum(int x, int y, int z) {

        /*
         * Transform the camera position according to its rotation
         */

        int yawVar = -yaw + 1024 & 0x3ff;
        int pitchVar = -pitch + 1024 & 0x3ff;
        int rollVar = -roll + 1024 & 0x3ff;

        if (rollVar != 0) {
            int k1 = sin2048Cache[rollVar];
            int j2 = sin2048Cache[rollVar + 1024];
            int i3 = y * k1 + x * j2 >> 15;
            y = y * j2 - x * k1 >> 15;
            x = i3;
        }

        if (yawVar != 0) {
            int l1 = sin2048Cache[yawVar];
            int k2 = sin2048Cache[yawVar + 1024];
            int j3 = y * k2 - z * l1 >> 15;
            z = y * l1 + z * k2 >> 15;
            y = j3;
        }

        if (pitchVar != 0) {
            int i2 = sin2048Cache[pitchVar];
            int l2 = sin2048Cache[pitchVar + 1024];
            int k3 = z * i2 + x * l2 >> 15;
            z = z * l2 - x * i2 >> 15;
            x = k3;
        }

        /*
         * Bounds checking
         */

        if (x < frustumMaxX) {
            frustumMaxX = x;
        }
        if (x > frustumMinX) {
            frustumMinX = x;
        }
        if (y < frustumMinY) {
            frustumMinY = y;
        }
        if (y > frustumMaxY) {
            frustumMaxY = y;
        }
        if (z < frustumFarZ) {
            frustumFarZ = z;
        }
        if (z > frustumNearZ) {
            frustumNearZ = z;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getPitch() {
        return pitch;
    }

    public int getRoll() {
        return roll;
    }

    public int getYaw() {
        return yaw;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public int getFrustumMinX() {
        return frustumMinX;
    }

    public int getFrustumMaxX() {
        return frustumMaxX;
    }

    public int getFrustumMinY() {
        return frustumMinY;
    }

    public int getFrustumMaxY() {
        return frustumMaxY;
    }

    public int getFrustumFarZ() {
        return frustumFarZ;
    }

    public int getFrustumNearZ() {
        return frustumNearZ;
    }

}
