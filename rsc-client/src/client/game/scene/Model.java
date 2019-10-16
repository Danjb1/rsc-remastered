package client.game.scene;

import java.io.DataInputStream;
import java.io.IOException;

import client.util.DataUtils;

/**
 * Class respresenting a 3d model.
 *
 * @author Dan Bryce
 */
public class Model {

    private static int sine9[];
    private static int sine11[];
    private static int base64Alphabet[];

    public int numVertices;
    public int projectedVertX[];
    public int projectedVertY[];
    public int projectedVertZ[];
    public int viewVertX[];
    public int viewVertY[];
    public int vertexIntensity[];
    public byte vertexAmbience[];
    public int numFaces;
    public int numVerticesPerFace[];
    public int faceVertices[][];
    public int faceFillFront[];
    public int faceFillBack[];
    public int faceCameraNormalMagnitude[];
    public int faceCameraNormalScale[];
    public int faceIntensity[];
    public int faceNormalX[];
    public int faceNormalY[];
    private int faceNormalZ[];
    public int depth;
    public int transformState;
    public boolean visible;
    public int x1;
    public int x2;
    public int y1;
    public int y2;
    public int z1;
    public int z2;
    public boolean translucent;
    public boolean transparent;
    public int entityId;
    public int faceTag[];
    private boolean autoCommit;
    public boolean isolated;
    public boolean unlit;
    public boolean unpickable;
    public boolean projected;
    private int defaultFaceValue;
    public int maxVertices;
    public int vertexX[];
    public int vertexY[];
    public int vertexZ[];
    public int vertexTransformedX[];
    public int vertexTransformedY[];
    public int vertexTransformedZ[];
    private int maxFaces;
    private int faceBoundLeft[];
    private int faceBoundRight[];
    private int faceBoundBottom[];
    private int faceBoundTop[];
    private int faceBoundNear[];
    private int faceBoundFar[];
    private int translateX;
    private int translateY;
    private int translateZ;
    private int rotX;
    private int rotY;
    private int rotZ;
    private int scaleX;
    private int scaleY;
    private int scaleZ;
    private int shearXY;
    private int shearXZ;
    private int shearYX;
    private int shearYZ;
    private int shearZX;
    private int shearZY;
    private int transformType;
    private int diameter;
    private int lightDirectionX;
    private int lightDirectionY;
    private int lightDirectionZ;
    private int lightDirectionMagnitude;
    protected int lightDiffuse;
    public int lightAmbience;
    private int indexInByteArray;

    static {
        sine9 = new int[512];
        sine11 = new int[2048];
        base64Alphabet = new int[256];
        for (int i = 0; i < 256; i++) {
            sine9[i] = (int) (Math.sin(i * 0.02454369D) * 32768D); // 32768
                                                                         // is
                                                                         // 2^15
            sine9[i + 256] = (int) (Math.cos(i * 0.02454369D) * 32768D);
        }

        for (int j = 0; j < 1024; j++) {
            sine11[j] = (int) (Math.sin(j * 0.00613592315D) * 32768D);
            sine11[j + 1024] = (int) (Math.cos(j * 0.00613592315D) * 32768D);
        }

        for (int j1 = 0; j1 < 10; j1++) {
            base64Alphabet[48 + j1] = j1;
        }

        for (int k1 = 0; k1 < 26; k1++) {
            base64Alphabet[65 + k1] = k1 + 10;
        }

        for (int l1 = 0; l1 < 26; l1++) {
            base64Alphabet[97 + l1] = l1 + 36;
        }

        base64Alphabet[163] = 62;
        base64Alphabet[36] = 63;
    }

    /**
     * Creates a Model from a byte array.
     *
     * @param abyte0
     * @param i
     * @param flag
     */
    public Model(byte abyte0[], int i, boolean flag) {
        transformState = 1;
        visible = true;
        translucent = false;
        transparent = false;
        entityId = -1;
        autoCommit = false;
        isolated = false;
        unlit = false;
        unpickable = false;
        projected = false;
        defaultFaceValue = 0xbc614e;
        diameter = 0xbc614e;
        lightDirectionX = 180;
        lightDirectionY = 155;
        lightDirectionZ = 95;
        lightDirectionMagnitude = 256;
        lightDiffuse = 512;
        lightAmbience = 32;
        int j = DataUtils.getUnsignedShort(abyte0, i);
        i += 2;
        int k = DataUtils.getUnsignedShort(abyte0, i);
        i += 2;
        initialise(j, k);
        for (int l = 0; l < j; l++) {
            vertexX[l] = DataUtils.getSigned2Bytes(abyte0, i);
            i += 2;
        }

        for (int i1 = 0; i1 < j; i1++) {
            vertexY[i1] = DataUtils.getSigned2Bytes(abyte0, i);
            i += 2;
        }

        for (int j1 = 0; j1 < j; j1++) {
            vertexZ[j1] = DataUtils.getSigned2Bytes(abyte0, i);
            i += 2;
        }

        numVertices = j;
        for (int k1 = 0; k1 < k; k1++) {
            numVerticesPerFace[k1] = abyte0[i++] & 0xff;
        }

        for (int l1 = 0; l1 < k; l1++) {
            faceFillFront[l1] = DataUtils.getSigned2Bytes(abyte0, i);
            i += 2;
            if (faceFillFront[l1] == 32767) {
                faceFillFront[l1] = defaultFaceValue;
            }
        }

        for (int i2 = 0; i2 < k; i2++) {
            faceFillBack[i2] = DataUtils.getSigned2Bytes(abyte0, i);
            i += 2;
            if (faceFillBack[i2] == 32767) {
                faceFillBack[i2] = defaultFaceValue;
            }
        }

        for (int j2 = 0; j2 < k; j2++) {
            int k2 = abyte0[i++] & 0xff;
            if (k2 == 0) {
                faceIntensity[j2] = 0;
            } else {
                faceIntensity[j2] = defaultFaceValue;
            }
        }

        for (int l2 = 0; l2 < k; l2++) {
            faceVertices[l2] = new int[numVerticesPerFace[l2]];
            for (int i3 = 0; i3 < numVerticesPerFace[l2]; i3++) {
                if (j < 256) {
                    faceVertices[l2][i3] = abyte0[i++] & 0xff;
                } else {
                    faceVertices[l2][i3] = DataUtils.getUnsignedShort(abyte0, i);
                    i += 2;
                }
            }

        }

        numFaces = k;
        transformState = 1;
    }

    /**
     * Loads a Model from a file.
     *
     * @param path
     */
    public Model(String path) {
        transformState = 1;
        visible = true;
        translucent = false;
        transparent = false;
        entityId = -1;
        autoCommit = false;
        isolated = false;
        unlit = false;
        unpickable = false;
        projected = false;
        defaultFaceValue = 0xbc614e;
        diameter = 0xbc614e;
        lightDirectionX = 180;
        lightDirectionY = 155;
        lightDirectionZ = 95;
        lightDirectionMagnitude = 256;
        lightDiffuse = 512;
        lightAmbience = 32;
        byte abyte0[] = null;
        try {
            java.io.InputStream inputstream = DataUtils.streamFromPath(path);
            DataInputStream datainputstream = new DataInputStream(inputstream);
            abyte0 = new byte[3];
            indexInByteArray = 0;
            for (int i = 0; i < 3; i += datainputstream.read(abyte0, i, 3 - i)) {
                ;
            }
            int k = readIntFromByteArray(abyte0);
            abyte0 = new byte[k];
            indexInByteArray = 0;
            for (int j = 0; j < k; j += datainputstream.read(abyte0, j, k - j)) {
                ;
            }
            datainputstream.close();
        } catch (IOException _ex) {
            numVertices = 0;
            numFaces = 0;
            return;
        }
        int l = readIntFromByteArray(abyte0);
        int someCount = readIntFromByteArray(abyte0);
        initialise(l, someCount);
        for (int j3 = 0; j3 < l; j3++) {
            int j1 = readIntFromByteArray(abyte0);
            int k1 = readIntFromByteArray(abyte0);
            int l1 = readIntFromByteArray(abyte0);
            createVertexWithoutDuplication(j1, k1, l1);
        }

        for (int k3 = 0; k3 < someCount; k3++) {
            int i2 = readIntFromByteArray(abyte0);
            int j2 = readIntFromByteArray(abyte0);
            int k2 = readIntFromByteArray(abyte0);
            int l2 = readIntFromByteArray(abyte0);
            lightDiffuse = readIntFromByteArray(abyte0);
            lightAmbience = readIntFromByteArray(abyte0);
            int i3 = readIntFromByteArray(abyte0);
            int ai[] = new int[i2];
            for (int l3 = 0; l3 < i2; l3++) {
                ai[l3] = readIntFromByteArray(abyte0);
            }

            int ai1[] = new int[l2];
            for (int i4 = 0; i4 < l2; i4++) {
                ai1[i4] = readIntFromByteArray(abyte0);
            }

            int j4 = createFace(i2, ai, j2, k2);
            if (i3 == 0) {
                faceIntensity[j4] = 0;
            } else {
                faceIntensity[j4] = defaultFaceValue;
            }
        }

        transformState = 1;
    }

    /**
     * Copies the Model at the given index of the given array, and sets some
     * flags.
     *
     * @param models
     * @param i
     * @param flag
     * @param flag1
     * @param flag2
     * @param flag3
     */
    public Model(Model models[], int i, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        transformState = 1;
        visible = true;
        translucent = false;
        transparent = false;
        entityId = -1;
        autoCommit = false;
        isolated = false;
        unlit = false;
        unpickable = false;
        projected = false;
        defaultFaceValue = 0xbc614e;
        diameter = 0xbc614e;
        lightDirectionX = 180;
        lightDirectionY = 155;
        lightDirectionZ = 95;
        lightDirectionMagnitude = 256;
        lightDiffuse = 512;
        lightAmbience = 32;
        autoCommit = flag;
        isolated = flag1;
        unlit = flag2;
        unpickable = flag3;
        copyModel(models, i);
    }

    /**
     * Copies the Model at the given index of the given array.
     *
     * @param models
     * @param i
     */
    public Model(Model models[], int i) {
        transformState = 1;
        visible = true;
        translucent = false;
        transparent = false;
        entityId = -1;
        autoCommit = false;
        isolated = false;
        unlit = false;
        unpickable = false;
        projected = false;
        defaultFaceValue = 0xbc614e;
        diameter = 0xbc614e;
        lightDirectionX = 180;
        lightDirectionY = 155;
        lightDirectionZ = 95;
        lightDirectionMagnitude = 256;
        lightDiffuse = 512;
        lightAmbience = 32;
        copyModel(models, i);
    }

    /**
     * Creates a Model.
     *
     * @param i
     * @param j
     */
    public Model(int i, int j) {
        transformState = 1;
        visible = true;
        translucent = false;
        transparent = false;
        entityId = -1;
        autoCommit = false;
        isolated = false;
        unlit = false;
        unpickable = false;
        projected = false;
        defaultFaceValue = 0xbc614e;
        diameter = 0xbc614e;
        lightDirectionX = 180;
        lightDirectionY = 155;
        lightDirectionZ = 95;
        lightDirectionMagnitude = 256;
        lightDiffuse = 512;
        lightAmbience = 32;
        initialise(i, j);
    }

    /**
     * Creates a Model and sets some flags.
     *
     * @param maxVertices
     * @param maxFaces
     * @param flag
     * @param flag1
     * @param flag2
     * @param unpickable
     * @param flag4
     */
    public Model(int maxVertices, int maxFaces, boolean flag, boolean flag1, boolean flag2, boolean unpickable, boolean flag4) {
        transformState = 1;
        visible = true;
        translucent = false;
        transparent = false;
        entityId = -1;
        projected = false;
        defaultFaceValue = 0xbc614e;
        diameter = 0xbc614e;
        lightDirectionX = 180;
        lightDirectionY = 155;
        lightDirectionZ = 95;
        lightDirectionMagnitude = 256;
        lightDiffuse = 512;
        lightAmbience = 32;
        autoCommit = flag;
        isolated = flag1;
        unlit = flag2;
        this.unpickable = unpickable;
        projected = flag4;
        initialise(maxVertices, maxFaces);
    }

    private void initialise(int maxVertices, int maxFaces) {
        vertexX = new int[maxVertices];
        vertexY = new int[maxVertices];
        vertexZ = new int[maxVertices];
        vertexIntensity = new int[maxVertices];
        vertexAmbience = new byte[maxVertices];
        numVerticesPerFace = new int[maxFaces];
        faceVertices = new int[maxFaces][];
        faceFillFront = new int[maxFaces];
        faceFillBack = new int[maxFaces];
        faceIntensity = new int[maxFaces];
        faceCameraNormalScale = new int[maxFaces];
        faceCameraNormalMagnitude = new int[maxFaces];
        if (!projected) {
            projectedVertX = new int[maxVertices];
            projectedVertY = new int[maxVertices];
            projectedVertZ = new int[maxVertices];
            viewVertX = new int[maxVertices];
            viewVertY = new int[maxVertices];
        }
        if (!unpickable) {
            faceTag = new int[maxFaces];
        }
        if (autoCommit) {
            vertexTransformedX = vertexX;
            vertexTransformedY = vertexY;
            vertexTransformedZ = vertexZ;
        } else {
            vertexTransformedX = new int[maxVertices];
            vertexTransformedY = new int[maxVertices];
            vertexTransformedZ = new int[maxVertices];
        }
        if (!unlit || !isolated) {
            faceNormalX = new int[maxFaces];
            faceNormalY = new int[maxFaces];
            faceNormalZ = new int[maxFaces];
        }
        if (!isolated) {
            faceBoundLeft = new int[maxFaces];
            faceBoundRight = new int[maxFaces];
            faceBoundBottom = new int[maxFaces];
            faceBoundTop = new int[maxFaces];
            faceBoundNear = new int[maxFaces];
            faceBoundFar = new int[maxFaces];
        }
        numFaces = 0;
        numVertices = 0;
        this.maxVertices = maxVertices;
        this.maxFaces = maxFaces;
        translateX = translateY = translateZ = 0;
        rotX = rotY = rotZ = 0;
        scaleX = scaleY = scaleZ = 256;
        shearXY = shearXZ = shearYX = shearYZ = shearZX = shearZY = 256;
        transformType = 0;
    }

    public void resetSomeArrays() {
        projectedVertX = new int[numVertices];
        projectedVertY = new int[numVertices];
        projectedVertZ = new int[numVertices];
        viewVertX = new int[numVertices];
        viewVertY = new int[numVertices];
    }

    public void clear() {
        numFaces = 0;
        numVertices = 0;
    }

    public void reduceCounters(int i, int j) {
        numFaces -= i;
        if (numFaces < 0) {
            numFaces = 0;
        }
        numVertices -= j;
        if (numVertices < 0) {
            numVertices = 0;
        }
    }

    /**
     * Copies data from the Model at the given index of the given array.
     *
     * @param models
     * @param i
     */
    public void copyModel(Model models[], int i) {
        int j = 0;
        int k = 0;
        for (int l = 0; l < i; l++) {
            j += models[l].numFaces;
            k += models[l].numVertices;
        }

        initialise(k, j);
        for (int i1 = 0; i1 < i; i1++) {
            Model gameModel = models[i1];
            gameModel.resetTransformation();
            lightAmbience = gameModel.lightAmbience;
            lightDiffuse = gameModel.lightDiffuse;
            lightDirectionX = gameModel.lightDirectionX;
            lightDirectionY = gameModel.lightDirectionY;
            lightDirectionZ = gameModel.lightDirectionZ;
            lightDirectionMagnitude = gameModel.lightDirectionMagnitude;
            for (int j1 = 0; j1 < gameModel.numFaces; j1++) {
                int ai[] = new int[gameModel.numVerticesPerFace[j1]];
                int ai1[] = gameModel.faceVertices[j1];
                for (int k1 = 0; k1 < gameModel.numVerticesPerFace[j1]; k1++) {
                    ai[k1] = createVertexWithoutDuplication(gameModel.vertexX[ai1[k1]], gameModel.vertexY[ai1[k1]],
                            gameModel.vertexZ[ai1[k1]]);
                }

                int l1 = createFace(gameModel.numVerticesPerFace[j1], ai, gameModel.faceFillFront[j1],
                        gameModel.faceFillBack[j1]);
                faceIntensity[l1] = gameModel.faceIntensity[j1];
                faceCameraNormalScale[l1] = gameModel.faceCameraNormalScale[j1];
                faceCameraNormalMagnitude[l1] = gameModel.faceCameraNormalMagnitude[j1];
            }

        }

        transformState = 1;
    }

    /**
     * Adds the given vertex, and returns its index.
     *
     * If the vertex already exists, simply returns its index.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int createVertexWithoutDuplication(int x, int y, int z) {

        // Check if vertex has already been added
        for (int l = 0; l < numVertices; l++) {
            if (vertexX[l] == x && vertexY[l] == y && vertexZ[l] == z) {
                return l;
            }
        }

        if (numVertices >= maxVertices) {
            return -1;
        }

        vertexX[numVertices] = x;
        vertexY[numVertices] = y;
        vertexZ[numVertices] = z;

        return numVertices++;
    }

    /**
     * Adds the given vertex, and returns its index.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int createVertex(int x, int z, int y) {

        if (numVertices >= maxVertices) {
            return -1;
        }

        vertexX[numVertices] = x;
        vertexY[numVertices] = z;
        vertexZ[numVertices] = y;

        return numVertices++;
    }

    public int createFace(int numVertices, int vertices[], int fillFront, int fillBack) {

        if (numFaces >= maxFaces) {
            return -1;
        }

        numVerticesPerFace[numFaces] = numVertices;
        faceVertices[numFaces] = vertices;
        faceFillFront[numFaces] = fillFront;
        faceFillBack[numFaces] = fillBack;
        transformState = 1;

        return numFaces++;
    }

    public Model[] createModelArray(int i, int j, int k, int l, int i1, int count, int k1, boolean flag) {
        resetTransformation();
        int ai[] = new int[count];
        int ai1[] = new int[count];
        for (int l1 = 0; l1 < count; l1++) {
            ai[l1] = 0;
            ai1[l1] = 0;
        }

        for (int i2 = 0; i2 < numFaces; i2++) {
            int j2 = 0;
            int k2 = 0;
            int i3 = numVerticesPerFace[i2];
            int ai2[] = faceVertices[i2];
            for (int i4 = 0; i4 < i3; i4++) {
                j2 += vertexX[ai2[i4]];
                k2 += vertexZ[ai2[i4]];
            }

            int k4 = j2 / (i3 * k) + (k2 / (i3 * l)) * i1;
            ai[k4] += i3;
            ai1[k4]++;
        }

        Model models[] = new Model[count];
        for (int l2 = 0; l2 < count; l2++) {
            if (ai[l2] > k1) {
                ai[l2] = k1;
            }
            models[l2] = new Model(ai[l2], ai1[l2], true, true, true, flag, true);
            models[l2].lightDiffuse = lightDiffuse;
            models[l2].lightAmbience = lightAmbience;
        }

        for (int j3 = 0; j3 < numFaces; j3++) {
            int k3 = 0;
            int j4 = 0;
            int l4 = numVerticesPerFace[j3];
            int ai3[] = faceVertices[j3];
            for (int i5 = 0; i5 < l4; i5++) {
                k3 += vertexX[ai3[i5]];
                j4 += vertexZ[ai3[i5]];
            }

            int j5 = k3 / (l4 * k) + (j4 / (l4 * l)) * i1;
            copySomeDataIntoTheNextIndex(models[j5], ai3, l4, j3);
        }

        for (int l3 = 0; l3 < count; l3++) {
            models[l3].resetSomeArrays();
        }

        return models;
    }

    public void copySomeDataIntoTheNextIndex(Model gameModel, int ai[], int count, int index) {
        int ai1[] = new int[count];
        for (int k = 0; k < count; k++) {
            int l = ai1[k] = gameModel.createVertexWithoutDuplication(vertexX[ai[k]], vertexY[ai[k]], vertexZ[ai[k]]);
            gameModel.vertexIntensity[l] = vertexIntensity[ai[k]];
            gameModel.vertexAmbience[l] = vertexAmbience[ai[k]];
        }

        int nextIndex = gameModel.createFace(count, ai1, faceFillFront[index], faceFillBack[index]);
        if (!gameModel.unpickable && !unpickable) {
            gameModel.faceTag[nextIndex] = faceTag[index];
        }
        gameModel.faceIntensity[nextIndex] = faceIntensity[index];
        gameModel.faceCameraNormalScale[nextIndex] = faceCameraNormalScale[index];
        gameModel.faceCameraNormalMagnitude[nextIndex] = faceCameraNormalMagnitude[index];
    }

    public void recalculateLighting(boolean flag, int i, int j, int distX, int distY, int distZ) {
        lightAmbience = 256 - i * 4;
        lightDiffuse = (64 - j) * 16 + 128;
        if (unlit) {
            return;
        }
        for (int j1 = 0; j1 < numFaces; j1++) {
            if (flag) {
                faceIntensity[j1] = defaultFaceValue;
            } else {
                faceIntensity[j1] = 0;
            }
        }

        this.lightDirectionX = distX;
        this.lightDirectionY = distY;
        this.lightDirectionZ = distZ;
        lightDirectionMagnitude = (int) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
        recalculateFaceLighting();
    }

    public void setLight(int i, int j, int distX, int distY, int distZ) {
        lightAmbience = 256 - i * 4;
        lightDiffuse = (64 - j) * 16 + 128;
        if (unlit) {
            return;
        } else {
            this.lightDirectionX = distX;
            this.lightDirectionY = distY;
            this.lightDirectionZ = distZ;
            lightDirectionMagnitude = (int) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
            recalculateFaceLighting();
            return;
        }
    }

    public void setLight(int distX, int distY, int distZ) {
        if (unlit) {
            return;
        } else {
            this.lightDirectionX = distX;
            this.lightDirectionY = distY;
            this.lightDirectionZ = distZ;
            lightDirectionMagnitude = (int) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
            recalculateFaceLighting();
            return;
        }
    }

    public void setVertexAmbience(int vertex, int ambience) {
        vertexAmbience[vertex] = (byte) ambience;
    }

    public void modRotation(int rotX, int rotY, int rotZ) {
        this.rotX += rotX & 0xff;
        this.rotY += rotY & 0xff;
        this.rotZ += rotZ & 0xff;
        updateState();
        transformState = 1;
    }

    public void setRotation(int i, int j, int k) {
        rotX = i & 0xff;
        rotY = j & 0xff;
        rotZ = k & 0xff;
        updateState();
        transformState = 1;
    }

    public void modTranslation(int translateX, int translateY, int translateZ) {
        this.translateX += translateX;
        this.translateY += translateY;
        this.translateZ += translateZ;
        updateState();
        transformState = 1;
    }

    public void setTranslation(int translateX, int translateY, int translateZ) {
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
        updateState();
        transformState = 1;
    }

    private void updateState() {
        if (shearXY != 256 || shearXZ != 256 || shearYX != 256 || shearYZ != 256
                || shearZX != 256 || shearZY != 256) {
            transformType = 4;
            return;
        }
        if (scaleX != 256 || scaleY != 256 || scaleZ != 256) {
            transformType = 3;
            return;
        }
        if (rotX != 0 || rotY != 0 || rotZ != 0) {
            transformType = 2;
            return;
        }
        if (translateX != 0 || translateY != 0 || translateZ != 0) {
            transformType = 1;
            return;
        } else {
            transformType = 0;
            return;
        }
    }

    private void translate(int dx, int dy, int dz) {
        for (int i = 0; i < numVertices; i++) {
            vertexTransformedX[i] += dx;
            vertexTransformedY[i] += dy;
            vertexTransformedZ[i] += dz;
        }
    }

    private void rotate(int rotX, int rotY, int rotZ) {
        for (int i3 = 0; i3 < numVertices; i3++) {
            if (rotZ != 0) {
                int l = sine9[rotZ];
                int k1 = sine9[rotZ + 256];
                int j2 = vertexTransformedY[i3] * l + vertexTransformedX[i3] * k1 >> 15;
                vertexTransformedY[i3] = vertexTransformedY[i3] * k1 - vertexTransformedX[i3] * l >> 15;
                vertexTransformedX[i3] = j2;
            }
            if (rotX != 0) {
                int i1 = sine9[rotX];
                int l1 = sine9[rotX + 256];
                int k2 = vertexTransformedY[i3] * l1 - vertexTransformedZ[i3] * i1 >> 15;
                vertexTransformedZ[i3] = vertexTransformedY[i3] * i1 + vertexTransformedZ[i3] * l1 >> 15;
                vertexTransformedY[i3] = k2;
            }
            if (rotY != 0) {
                int j1 = sine9[rotY];
                int i2 = sine9[rotY + 256];
                int l2 = vertexTransformedZ[i3] * j1 + vertexTransformedX[i3] * i2 >> 15;
                vertexTransformedZ[i3] = vertexTransformedZ[i3] * i2 - vertexTransformedX[i3] * j1 >> 15;
                vertexTransformedX[i3] = l2;
            }
        }

    }

    private void doSomeVertexTransformation(
            int yMultiplierToX,
            int yMultiplierToZ,
            int zMultiplierToX,
            int zMultiplierToY,
            int xMultiplierToZ,
            int xMultiplierToY) {
        for (int i = 0; i < numVertices; i++) {
            if (yMultiplierToX != 0) {
                vertexTransformedX[i] += vertexTransformedY[i] * yMultiplierToX >> 8;
            }
            if (yMultiplierToZ != 0) {
                vertexTransformedZ[i] += vertexTransformedY[i] * yMultiplierToZ >> 8;
            }
            if (zMultiplierToX != 0) {
                vertexTransformedX[i] += vertexTransformedZ[i] * zMultiplierToX >> 8;
            }
            if (zMultiplierToY != 0) {
                vertexTransformedY[i] += vertexTransformedZ[i] * zMultiplierToY >> 8;
            }
            if (xMultiplierToZ != 0) {
                vertexTransformedZ[i] += vertexTransformedX[i] * xMultiplierToZ >> 8;
            }
            if (xMultiplierToY != 0) {
                vertexTransformedY[i] += vertexTransformedX[i] * xMultiplierToY >> 8;
            }
        }

    }

    private void scale(int i, int j, int k) {
        for (int l = 0; l < numVertices; l++) {
            vertexTransformedX[l] = vertexTransformedX[l] * i >> 8;
            vertexTransformedY[l] = vertexTransformedY[l] * j >> 8;
            vertexTransformedZ[l] = vertexTransformedZ[l] * k >> 8;
        }

    }

    private void doSomeBoundsChecking() {
        x1 = y1 = z1 = 0xf423f;
        diameter = x2 = y2 = z2 = 0xfff0bdc1;
        for (int i = 0; i < numFaces; i++) {
            int ai[] = faceVertices[i];
            int k = ai[0];
            int i1 = numVerticesPerFace[i];
            int j1;
            int k1 = j1 = vertexTransformedX[k];
            int l1;
            int i2 = l1 = vertexTransformedY[k];
            int j2;
            int k2 = j2 = vertexTransformedZ[k];
            for (int j = 0; j < i1; j++) {
                int l = ai[j];
                if (vertexTransformedX[l] < j1) {
                    j1 = vertexTransformedX[l];
                } else if (vertexTransformedX[l] > k1) {
                    k1 = vertexTransformedX[l];
                }
                if (vertexTransformedY[l] < l1) {
                    l1 = vertexTransformedY[l];
                } else if (vertexTransformedY[l] > i2) {
                    i2 = vertexTransformedY[l];
                }
                if (vertexTransformedZ[l] < j2) {
                    j2 = vertexTransformedZ[l];
                } else if (vertexTransformedZ[l] > k2) {
                    k2 = vertexTransformedZ[l];
                }
            }

            if (!isolated) {
                faceBoundLeft[i] = j1;
                faceBoundRight[i] = k1;
                faceBoundBottom[i] = l1;
                faceBoundTop[i] = i2;
                faceBoundNear[i] = j2;
                faceBoundFar[i] = k2;
            }
            // This looks like bounds checking
            if (k1 - j1 > diameter) {
                diameter = k1 - j1;
            }
            if (i2 - l1 > diameter) {
                diameter = i2 - l1;
            }
            if (k2 - j2 > diameter) {
                diameter = k2 - j2;
            }
            if (j1 < x1) {
                x1 = j1;
            }
            if (k1 > x2) {
                x2 = k1;
            }
            if (l1 < y1) {
                y1 = l1;
            }
            if (i2 > y2) {
                y2 = i2;
            }
            if (j2 < z1) {
                z1 = j2;
            }
            if (k2 > z2) {
                z2 = k2;
            }
        }

    }

    public void recalculateFaceLighting() {
        if (unlit) {
            return;
        }
        int i = lightDiffuse * lightDirectionMagnitude >> 8;
        for (int j = 0; j < numFaces; j++) {
            if (faceIntensity[j] != defaultFaceValue) {
                faceIntensity[j] = (faceNormalX[j] * lightDirectionX + faceNormalY[j] * lightDirectionY + faceNormalZ[j] * lightDirectionZ) / i;
            }
        }

        int ai[] = new int[numVertices];
        int ai1[] = new int[numVertices];
        int ai2[] = new int[numVertices];
        int ai3[] = new int[numVertices];
        for (int k = 0; k < numVertices; k++) {
            ai[k] = 0;
            ai1[k] = 0;
            ai2[k] = 0;
            ai3[k] = 0;
        }

        for (int l = 0; l < numFaces; l++) {
            if (faceIntensity[l] == defaultFaceValue) {
                for (int i1 = 0; i1 < numVerticesPerFace[l]; i1++) {
                    int k1 = faceVertices[l][i1];
                    ai[k1] += faceNormalX[l];
                    ai1[k1] += faceNormalY[l];
                    ai2[k1] += faceNormalZ[l];
                    ai3[k1]++;
                }

            }
        }

        for (int j1 = 0; j1 < numVertices; j1++) {
            if (ai3[j1] > 0) {
                vertexIntensity[j1] = (ai[j1] * lightDirectionX + ai1[j1] * lightDirectionY + ai2[j1] * lightDirectionZ) / (i * ai3[j1]);
            }
        }
    }

    public void doSomeDistanceCalculations() {
        if (unlit && isolated) {
            return;
        }
        for (int i = 0; i < numFaces; i++) {
            int ai[] = faceVertices[i];

            int j = vertexTransformedX[ai[0]];
            int k = vertexTransformedY[ai[0]];
            int l = vertexTransformedZ[ai[0]];

            int i1 = vertexTransformedX[ai[1]] - j;
            int j1 = vertexTransformedY[ai[1]] - k;
            int k1 = vertexTransformedZ[ai[1]] - l;

            int l1 = vertexTransformedX[ai[2]] - j;
            int i2 = vertexTransformedY[ai[2]] - k;
            int j2 = vertexTransformedZ[ai[2]] - l;

            int distX = (j1 * j2) - (i2 * k1);
            int distY = (k1 * l1) - (j2 * i1);
            int distZ;
            for (distZ = i1 * i2 - l1 * j1; distX > 8192 || distY > 8192 || distZ > 8192 || distX < -8192
                    || distY < -8192 || distZ < -8192; distZ >>= 1) {
                distX >>= 1;
                distY >>= 1;
            }

            int distance = (int) (256D * Math.sqrt(distX * distX + distY * distY + distZ * distZ));
            if (distance <= 0) {
                distance = 1;
            }
            faceNormalX[i] = (distX * 0x10000) / distance;
            faceNormalY[i] = (distY * 0x10000) / distance;
            faceNormalZ[i] = (distZ * 65535) / distance;
            faceCameraNormalScale[i] = -1;
        }

        recalculateFaceLighting();
    }

    private void transform() {
        if (transformState == 2) {
            transformState = 0;
            for (int i = 0; i < numVertices; i++) {
                vertexTransformedX[i] = vertexX[i];
                vertexTransformedY[i] = vertexY[i];
                vertexTransformedZ[i] = vertexZ[i];
            }

            x1 = y1 = z1 = 0xff676981;
            diameter = x2 = y2 = z2 = 0x98967f;
            return;
        }
        if (transformState == 1) {
            transformState = 0;
            for (int j = 0; j < numVertices; j++) {
                vertexTransformedX[j] = vertexX[j];
                vertexTransformedY[j] = vertexY[j];
                vertexTransformedZ[j] = vertexZ[j];
            }

            if (transformType >= 2) {
                rotate(rotX, rotY, rotZ);
            }
            if (transformType >= 3) {
                scale(scaleX, scaleY, scaleZ);
            }
            if (transformType >= 4) {
                doSomeVertexTransformation(shearXY, shearXZ, shearYX, shearYZ,
                        shearZX, shearZY);
            }
            if (transformType >= 1) {
                translate(translateX, translateY, translateZ);
            }
            doSomeBoundsChecking();
            doSomeDistanceCalculations();
        }
    }

    public void project(Camera camera, int viewDistance, int clipNear) {
        transform();
        if (z1 > camera.getFrustumNearZ() ||
                z2 < camera.getFrustumFarZ() ||
                x1 > camera.getFrustumMinX() ||
                x2 < camera.getFrustumMaxX() ||
                y1 > camera.getFrustumMaxY() ||
                y2 < camera.getFrustumMinY()) {
            visible = false;
            return;
        }
        visible = true;
        int l2 = 0;
        int i3 = 0;
        int j3 = 0;
        int k3 = 0;
        int l3 = 0;
        int i4 = 0;
        if (camera.getRoll() != 0) {
            l2 = sine11[camera.getRoll()];
            i3 = sine11[camera.getRoll() + 1024];
        }
        if (camera.getPitch() != 0) {
            l3 = sine11[camera.getPitch()];
            i4 = sine11[camera.getPitch() + 1024];
        }
        if (camera.getYaw() != 0) {
            j3 = sine11[camera.getYaw()];
            k3 = sine11[camera.getYaw() + 1024];
        }
        for (int index = 0; index < numVertices; index++) {
            int k4 = vertexTransformedX[index] - camera.getX();
            int l4 = vertexTransformedY[index] - camera.getY();
            int i5 = vertexTransformedZ[index] - camera.getZ();
            if (camera.getRoll() != 0) {
                int i2 = l4 * l2 + k4 * i3 >> 15;
                l4 = l4 * i3 - k4 * l2 >> 15;
                k4 = i2;
            }
            if (camera.getPitch() != 0) {
                int j2 = i5 * l3 + k4 * i4 >> 15;
                i5 = i5 * i4 - k4 * l3 >> 15;
                k4 = j2;
            }
            if (camera.getYaw() != 0) {
                int k2 = l4 * k3 - i5 * j3 >> 15;
                i5 = l4 * j3 + i5 * k3 >> 15;
                l4 = k2;
            }
            if (i5 >= clipNear) {
                viewVertX[index] = (k4 << viewDistance) / i5;
            } else {
                viewVertX[index] = k4 << viewDistance;
            }
            if (i5 >= clipNear) {
                viewVertY[index] = (l4 << viewDistance) / i5;
            } else {
                viewVertY[index] = l4 << viewDistance;
            }
            projectedVertX[index] = k4;
            projectedVertY[index] = l4;
            projectedVertZ[index] = i5;
        }

    }

    public void resetTransformation() {
        transform();
        for (int i = 0; i < numVertices; i++) {
            vertexX[i] = vertexTransformedX[i];
            vertexY[i] = vertexTransformedY[i];
            vertexZ[i] = vertexTransformedZ[i];
        }

        translateX = translateY = translateZ = 0;
        rotX = rotY = rotZ = 0;
        scaleX = scaleY = scaleZ = 256;
        shearXY = shearXZ = shearYX = shearYZ = shearZX = shearZY = 256;
        transformType = 0;
    }

    public Model createNewGiantCrystalFromThisModel() {
        Model models[] = new Model[1];
        models[0] = this;
        Model gameModel = new Model(models, 1);
        gameModel.depth = depth;
        gameModel.transparent = transparent;
        return gameModel;
    }

    public Model createNewModelFromThisOne(boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        Model models[] = new Model[1];
        models[0] = this;
        Model gameModel = new Model(models, 1, flag, flag1, flag2, flag3);
        gameModel.depth = depth;
        return gameModel;
    }

    public void copyDataFromModel(Model gameModel) {
        rotX = gameModel.rotX;
        rotY = gameModel.rotY;
        rotZ = gameModel.rotZ;
        translateX = gameModel.translateX;
        translateY = gameModel.translateY;
        translateZ = gameModel.translateZ;
        updateState();
        transformState = 1;
    }

    public int readIntFromByteArray(byte abyte0[]) {
        for (; abyte0[indexInByteArray] == 10 || abyte0[indexInByteArray] == 13; indexInByteArray++) {
            ;
        }
        int i = base64Alphabet[abyte0[indexInByteArray++] & 0xff];
        int j = base64Alphabet[abyte0[indexInByteArray++] & 0xff];
        int k = base64Alphabet[abyte0[indexInByteArray++] & 0xff];
        int l = (i * 4096 + j * 64 + k) - 0x20000;
        if (l == 0x1e240) {
            l = defaultFaceValue;
        }
        return l;
    }

}
