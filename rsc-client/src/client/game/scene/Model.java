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

    public int vertexIndex;
    public int projectVertexX[];
    public int projectVertexY[];
    public int projectVertexZ[];
    public int vertexViewX[];
    public int vertexViewY[];
    public int vertexIntensity[];
    public byte vertexAmbience[];
    public int numFaces;
    public int faceNumVertices[];
    public int faceVertices[][];
    public int faceFillFront[];
    public int faceFillBack[];
    public int normalMagnitude[];
    public int normalScale[];
    public int faceIntensity[];
    public int distXRatio[];
    public int distYRatio[];
    private int distZRatio[];
    public int anInt245;
    public int transformState;
    public boolean visible;
    public int anInt248;
    public int anInt249;
    public int anInt250;
    public int anInt251;
    public int anInt252;
    public int anInt253;
    public boolean textureTranslucent;
    public boolean transparent;
    public int entityId;
    public int faceTag[];
    private boolean aBoolean260;
    public boolean aBoolean261;
    public boolean aBoolean262;
    public boolean unpickable;
    public boolean aBoolean264;
    private static int trigValues1[];
    private static int trigValues2[];
    private static byte someConstantRanges[];
    private static int someConstantsRanges2[];
    private int anInt270;
    public int maxVertices;
    public int vertexX[];
    public int vertexY[];
    public int vertexZ[];
    public int xPosition[];
    public int yPosition[];
    public int zPosition[];
    private int count2;
    private int someMatrix2[][];
    private int anIntArray280[];
    private int anIntArray281[];
    private int anIntArray282[];
    private int anIntArray283[];
    private int anIntArray284[];
    private int anIntArray285[];
    private int translateX;
    private int translateY;
    private int translateZ;
    private int rotX;
    private int rotY;
    private int rotZ;
    private int scaleX;
    private int scaleY;
    private int scaleZ;
    private int initially256_1;
    private int initially256_2;
    private int initially256_3;
    private int initially256_4;
    private int initially256_5;
    private int initially256_6;
    private int state;
    private int anInt302;
    private int distX;
    private int distY;
    private int distZ;
    private int distance;
    protected int anInt307;
    public int lightAmbience;
    private int indexInByteArray;

    static {
        trigValues1 = new int[512];
        trigValues2 = new int[2048];
        someConstantRanges = new byte[64];
        someConstantsRanges2 = new int[256];
        for (int i = 0; i < 256; i++) {
            trigValues1[i] = (int) (Math.sin(i * 0.02454369D) * 32768D); // 32768
                                                                         // is
                                                                         // 2^15
            trigValues1[i + 256] = (int) (Math.cos(i * 0.02454369D) * 32768D);
        }

        for (int j = 0; j < 1024; j++) {
            trigValues2[j] = (int) (Math.sin(j * 0.00613592315D) * 32768D);
            trigValues2[j + 1024] = (int) (Math.cos(j * 0.00613592315D) * 32768D);
        }

        for (int k = 0; k < 10; k++) {
            someConstantRanges[k] = (byte) (48 + k);
        }

        for (int l = 0; l < 26; l++) {
            someConstantRanges[l + 10] = (byte) (65 + l);
        }

        for (int i1 = 0; i1 < 26; i1++) {
            someConstantRanges[i1 + 36] = (byte) (97 + i1);
        }

        someConstantRanges[62] = -93;
        someConstantRanges[63] = 36;
        for (int j1 = 0; j1 < 10; j1++) {
            someConstantsRanges2[48 + j1] = j1;
        }

        for (int k1 = 0; k1 < 26; k1++) {
            someConstantsRanges2[65 + k1] = k1 + 10;
        }

        for (int l1 = 0; l1 < 26; l1++) {
            someConstantsRanges2[97 + l1] = l1 + 36;
        }

        someConstantsRanges2[163] = 62;
        someConstantsRanges2[36] = 63;
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
        textureTranslucent = false;
        transparent = false;
        entityId = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        unpickable = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        distX = 180;
        distY = 155;
        distZ = 95;
        distance = 256;
        anInt307 = 512;
        lightAmbience = 32;
        int j = DataUtils.getUnsignedShort(abyte0, i);
        i += 2;
        int k = DataUtils.getUnsignedShort(abyte0, i);
        i += 2;
        initialise(j, k);
        someMatrix2 = new int[k][1];
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

        vertexIndex = j;
        for (int k1 = 0; k1 < k; k1++) {
            faceNumVertices[k1] = abyte0[i++] & 0xff;
        }

        for (int l1 = 0; l1 < k; l1++) {
            faceFillFront[l1] = DataUtils.getSigned2Bytes(abyte0, i);
            i += 2;
            if (faceFillFront[l1] == 32767) {
                faceFillFront[l1] = anInt270;
            }
        }

        for (int i2 = 0; i2 < k; i2++) {
            faceFillBack[i2] = DataUtils.getSigned2Bytes(abyte0, i);
            i += 2;
            if (faceFillBack[i2] == 32767) {
                faceFillBack[i2] = anInt270;
            }
        }

        for (int j2 = 0; j2 < k; j2++) {
            int k2 = abyte0[i++] & 0xff;
            if (k2 == 0) {
                faceIntensity[j2] = 0;
            } else {
                faceIntensity[j2] = anInt270;
            }
        }

        for (int l2 = 0; l2 < k; l2++) {
            faceVertices[l2] = new int[faceNumVertices[l2]];
            for (int i3 = 0; i3 < faceNumVertices[l2]; i3++) {
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
        textureTranslucent = false;
        transparent = false;
        entityId = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        unpickable = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        distX = 180;
        distY = 155;
        distZ = 95;
        distance = 256;
        anInt307 = 512;
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
            vertexIndex = 0;
            numFaces = 0;
            return;
        }
        int l = readIntFromByteArray(abyte0);
        int someCount = readIntFromByteArray(abyte0);
        initialise(l, someCount);
        someMatrix2 = new int[someCount][];
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
            anInt307 = readIntFromByteArray(abyte0);
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
            someMatrix2[k3] = ai1;
            if (i3 == 0) {
                faceIntensity[j4] = 0;
            } else {
                faceIntensity[j4] = anInt270;
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
        textureTranslucent = false;
        transparent = false;
        entityId = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        unpickable = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        distX = 180;
        distY = 155;
        distZ = 95;
        distance = 256;
        anInt307 = 512;
        lightAmbience = 32;
        aBoolean260 = flag;
        aBoolean261 = flag1;
        aBoolean262 = flag2;
        unpickable = flag3;
        copyModel(models, i, false);
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
        textureTranslucent = false;
        transparent = false;
        entityId = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        unpickable = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        distX = 180;
        distY = 155;
        distZ = 95;
        distance = 256;
        anInt307 = 512;
        lightAmbience = 32;
        copyModel(models, i, true);
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
        textureTranslucent = false;
        transparent = false;
        entityId = -1;
        aBoolean260 = false;
        aBoolean261 = false;
        aBoolean262 = false;
        unpickable = false;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        distX = 180;
        distY = 155;
        distZ = 95;
        distance = 256;
        anInt307 = 512;
        lightAmbience = 32;
        initialise(i, j);
        someMatrix2 = new int[j][1];
        for (int k = 0; k < j; k++) {
            someMatrix2[k][0] = k;
        }

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
        textureTranslucent = false;
        transparent = false;
        entityId = -1;
        aBoolean264 = false;
        anInt270 = 0xbc614e;
        anInt302 = 0xbc614e;
        distX = 180;
        distY = 155;
        distZ = 95;
        distance = 256;
        anInt307 = 512;
        lightAmbience = 32;
        aBoolean260 = flag;
        aBoolean261 = flag1;
        aBoolean262 = flag2;
        this.unpickable = unpickable;
        aBoolean264 = flag4;
        initialise(maxVertices, maxFaces);
    }

    private void initialise(int maxVertices, int maxFaces) {
        vertexX = new int[maxVertices];
        vertexY = new int[maxVertices];
        vertexZ = new int[maxVertices];
        vertexIntensity = new int[maxVertices];
        vertexAmbience = new byte[maxVertices];
        faceNumVertices = new int[maxFaces];
        faceVertices = new int[maxFaces][];
        faceFillFront = new int[maxFaces];
        faceFillBack = new int[maxFaces];
        faceIntensity = new int[maxFaces];
        normalScale = new int[maxFaces];
        normalMagnitude = new int[maxFaces];
        if (!aBoolean264) {
            projectVertexX = new int[maxVertices];
            projectVertexY = new int[maxVertices];
            projectVertexZ = new int[maxVertices];
            vertexViewX = new int[maxVertices];
            vertexViewY = new int[maxVertices];
        }
        if (!unpickable) {
            faceTag = new int[maxFaces];
        }
        if (aBoolean260) {
            xPosition = vertexX;
            yPosition = vertexY;
            zPosition = vertexZ;
        } else {
            xPosition = new int[maxVertices];
            yPosition = new int[maxVertices];
            zPosition = new int[maxVertices];
        }
        if (!aBoolean262 || !aBoolean261) {
            distXRatio = new int[maxFaces];
            distYRatio = new int[maxFaces];
            distZRatio = new int[maxFaces];
        }
        if (!aBoolean261) {
            anIntArray280 = new int[maxFaces];
            anIntArray281 = new int[maxFaces];
            anIntArray282 = new int[maxFaces];
            anIntArray283 = new int[maxFaces];
            anIntArray284 = new int[maxFaces];
            anIntArray285 = new int[maxFaces];
        }
        numFaces = 0;
        vertexIndex = 0;
        this.maxVertices = maxVertices;
        this.count2 = maxFaces;
        translateX = translateY = translateZ = 0;
        rotX = rotY = rotZ = 0;
        scaleX = scaleY = scaleZ = 256;
        initially256_1 = initially256_2 = initially256_3 = initially256_4 = initially256_5 = initially256_6 = 256;
        state = 0;
    }

    public void resetSomeArrays() {
        projectVertexX = new int[vertexIndex];
        projectVertexY = new int[vertexIndex];
        projectVertexZ = new int[vertexIndex];
        vertexViewX = new int[vertexIndex];
        vertexViewY = new int[vertexIndex];
    }

    public void clear() {
        numFaces = 0;
        vertexIndex = 0;
    }

    public void reduceCounters(int i, int j) {
        numFaces -= i;
        if (numFaces < 0) {
            numFaces = 0;
        }
        vertexIndex -= j;
        if (vertexIndex < 0) {
            vertexIndex = 0;
        }
    }

    /**
     * Copies data from the Model at the given index of the given array.
     *
     * @param models
     * @param i
     * @param flag
     */
    public void copyModel(Model models[], int i, boolean flag) {
        int j = 0;
        int k = 0;
        for (int l = 0; l < i; l++) {
            j += models[l].numFaces;
            k += models[l].vertexIndex;
        }

        initialise(k, j);
        if (flag) {
            someMatrix2 = new int[j][];
        }
        for (int i1 = 0; i1 < i; i1++) {
            Model gameModel = models[i1];
            gameModel.resetTransformation();
            lightAmbience = gameModel.lightAmbience;
            anInt307 = gameModel.anInt307;
            distX = gameModel.distX;
            distY = gameModel.distY;
            distZ = gameModel.distZ;
            distance = gameModel.distance;
            for (int j1 = 0; j1 < gameModel.numFaces; j1++) {
                int ai[] = new int[gameModel.faceNumVertices[j1]];
                int ai1[] = gameModel.faceVertices[j1];
                for (int k1 = 0; k1 < gameModel.faceNumVertices[j1]; k1++) {
                    ai[k1] = createVertexWithoutDuplication(gameModel.vertexX[ai1[k1]], gameModel.vertexY[ai1[k1]],
                            gameModel.vertexZ[ai1[k1]]);
                }

                int l1 = createFace(gameModel.faceNumVertices[j1], ai, gameModel.faceFillFront[j1],
                        gameModel.faceFillBack[j1]);
                faceIntensity[l1] = gameModel.faceIntensity[j1];
                normalScale[l1] = gameModel.normalScale[j1];
                normalMagnitude[l1] = gameModel.normalMagnitude[j1];
                if (flag) {
                    if (i > 1) {
                        someMatrix2[l1] = new int[gameModel.someMatrix2[j1].length + 1];
                        someMatrix2[l1][0] = i1;
                        for (int i2 = 0; i2 < gameModel.someMatrix2[j1].length; i2++) {
                            someMatrix2[l1][i2 + 1] = gameModel.someMatrix2[j1][i2];
                        }

                    } else {
                        someMatrix2[l1] = new int[gameModel.someMatrix2[j1].length];
                        for (int j2 = 0; j2 < gameModel.someMatrix2[j1].length; j2++) {
                            someMatrix2[l1][j2] = gameModel.someMatrix2[j1][j2];
                        }

                    }
                }
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
        for (int l = 0; l < vertexIndex; l++) {
            if (vertexX[l] == x && vertexY[l] == y && vertexZ[l] == z) {
                return l;
            }
        }

        if (vertexIndex >= maxVertices) {
            return -1;
        }

        vertexX[vertexIndex] = x;
        vertexY[vertexIndex] = y;
        vertexZ[vertexIndex] = z;

        return vertexIndex++;
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

        if (vertexIndex >= maxVertices) {
            return -1;
        }

        vertexX[vertexIndex] = x;
        vertexY[vertexIndex] = z;
        vertexZ[vertexIndex] = y;

        return vertexIndex++;
    }

    public int createFace(int numVertices, int vertices[], int fillFront, int fillBack) {

        if (numFaces >= count2) {
            return -1;
        }

        faceNumVertices[numFaces] = numVertices;
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
            int i3 = faceNumVertices[i2];
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
            models[l2].anInt307 = anInt307;
            models[l2].lightAmbience = lightAmbience;
        }

        for (int j3 = 0; j3 < numFaces; j3++) {
            int k3 = 0;
            int j4 = 0;
            int l4 = faceNumVertices[j3];
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
        gameModel.normalScale[nextIndex] = normalScale[index];
        gameModel.normalMagnitude[nextIndex] = normalMagnitude[index];
    }

    public void recalculateLighting(boolean flag, int i, int j, int distX, int distY, int distZ) {
        lightAmbience = 256 - i * 4;
        anInt307 = (64 - j) * 16 + 128;
        if (aBoolean262) {
            return;
        }
        for (int j1 = 0; j1 < numFaces; j1++) {
            if (flag) {
                faceIntensity[j1] = anInt270;
            } else {
                faceIntensity[j1] = 0;
            }
        }

        this.distX = distX;
        this.distY = distY;
        this.distZ = distZ;
        distance = (int) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
        recalculateFaceLighting();
    }

    public void setLight(int i, int j, int distX, int distY, int distZ) {
        lightAmbience = 256 - i * 4;
        anInt307 = (64 - j) * 16 + 128;
        if (aBoolean262) {
            return;
        } else {
            this.distX = distX;
            this.distY = distY;
            this.distZ = distZ;
            distance = (int) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
            recalculateFaceLighting();
            return;
        }
    }

    public void setLight(int distX, int distY, int distZ) {
        if (aBoolean262) {
            return;
        } else {
            this.distX = distX;
            this.distY = distY;
            this.distZ = distZ;
            distance = (int) Math.sqrt(distX * distX + distY * distY + distZ * distZ);
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
        if (initially256_1 != 256 || initially256_2 != 256 || initially256_3 != 256 || initially256_4 != 256
                || initially256_5 != 256 || initially256_6 != 256) {
            state = 4;
            return;
        }
        if (scaleX != 256 || scaleY != 256 || scaleZ != 256) {
            state = 3;
            return;
        }
        if (rotX != 0 || rotY != 0 || rotZ != 0) {
            state = 2;
            return;
        }
        if (translateX != 0 || translateY != 0 || translateZ != 0) {
            state = 1;
            return;
        } else {
            state = 0;
            return;
        }
    }

    private void translate(int dx, int dy, int dz) {
        for (int i = 0; i < vertexIndex; i++) {
            xPosition[i] += dx;
            yPosition[i] += dy;
            zPosition[i] += dz;
        }
    }

    private void rotate(int rotX, int rotY, int rotZ) {
        for (int i3 = 0; i3 < vertexIndex; i3++) {
            if (rotZ != 0) {
                int l = trigValues1[rotZ];
                int k1 = trigValues1[rotZ + 256];
                int j2 = yPosition[i3] * l + xPosition[i3] * k1 >> 15;
                yPosition[i3] = yPosition[i3] * k1 - xPosition[i3] * l >> 15;
                xPosition[i3] = j2;
            }
            if (rotX != 0) {
                int i1 = trigValues1[rotX];
                int l1 = trigValues1[rotX + 256];
                int k2 = yPosition[i3] * l1 - zPosition[i3] * i1 >> 15;
                zPosition[i3] = yPosition[i3] * i1 + zPosition[i3] * l1 >> 15;
                yPosition[i3] = k2;
            }
            if (rotY != 0) {
                int j1 = trigValues1[rotY];
                int i2 = trigValues1[rotY + 256];
                int l2 = zPosition[i3] * j1 + xPosition[i3] * i2 >> 15;
                zPosition[i3] = zPosition[i3] * i2 - xPosition[i3] * j1 >> 15;
                xPosition[i3] = l2;
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
        for (int i = 0; i < vertexIndex; i++) {
            if (yMultiplierToX != 0) {
                xPosition[i] += yPosition[i] * yMultiplierToX >> 8;
            }
            if (yMultiplierToZ != 0) {
                zPosition[i] += yPosition[i] * yMultiplierToZ >> 8;
            }
            if (zMultiplierToX != 0) {
                xPosition[i] += zPosition[i] * zMultiplierToX >> 8;
            }
            if (zMultiplierToY != 0) {
                yPosition[i] += zPosition[i] * zMultiplierToY >> 8;
            }
            if (xMultiplierToZ != 0) {
                zPosition[i] += xPosition[i] * xMultiplierToZ >> 8;
            }
            if (xMultiplierToY != 0) {
                yPosition[i] += xPosition[i] * xMultiplierToY >> 8;
            }
        }

    }

    private void scale(int i, int j, int k) {
        for (int l = 0; l < vertexIndex; l++) {
            xPosition[l] = xPosition[l] * i >> 8;
            yPosition[l] = yPosition[l] * j >> 8;
            zPosition[l] = zPosition[l] * k >> 8;
        }

    }

    private void doSomeBoundsChecking() {
        anInt248 = anInt250 = anInt252 = 0xf423f;
        anInt302 = anInt249 = anInt251 = anInt253 = 0xfff0bdc1;
        for (int i = 0; i < numFaces; i++) {
            int ai[] = faceVertices[i];
            int k = ai[0];
            int i1 = faceNumVertices[i];
            int j1;
            int k1 = j1 = xPosition[k];
            int l1;
            int i2 = l1 = yPosition[k];
            int j2;
            int k2 = j2 = zPosition[k];
            for (int j = 0; j < i1; j++) {
                int l = ai[j];
                if (xPosition[l] < j1) {
                    j1 = xPosition[l];
                } else if (xPosition[l] > k1) {
                    k1 = xPosition[l];
                }
                if (yPosition[l] < l1) {
                    l1 = yPosition[l];
                } else if (yPosition[l] > i2) {
                    i2 = yPosition[l];
                }
                if (zPosition[l] < j2) {
                    j2 = zPosition[l];
                } else if (zPosition[l] > k2) {
                    k2 = zPosition[l];
                }
            }

            if (!aBoolean261) {
                anIntArray280[i] = j1;
                anIntArray281[i] = k1;
                anIntArray282[i] = l1;
                anIntArray283[i] = i2;
                anIntArray284[i] = j2;
                anIntArray285[i] = k2;
            }
            // This looks like bounds checking
            if (k1 - j1 > anInt302) {
                anInt302 = k1 - j1;
            }
            if (i2 - l1 > anInt302) {
                anInt302 = i2 - l1;
            }
            if (k2 - j2 > anInt302) {
                anInt302 = k2 - j2;
            }
            if (j1 < anInt248) {
                anInt248 = j1;
            }
            if (k1 > anInt249) {
                anInt249 = k1;
            }
            if (l1 < anInt250) {
                anInt250 = l1;
            }
            if (i2 > anInt251) {
                anInt251 = i2;
            }
            if (j2 < anInt252) {
                anInt252 = j2;
            }
            if (k2 > anInt253) {
                anInt253 = k2;
            }
        }

    }

    public void recalculateFaceLighting() {
        if (aBoolean262) {
            return;
        }
        int i = anInt307 * distance >> 8;
        for (int j = 0; j < numFaces; j++) {
            if (faceIntensity[j] != anInt270) {
                faceIntensity[j] = (distXRatio[j] * distX + distYRatio[j] * distY + distZRatio[j] * distZ) / i;
            }
        }

        int ai[] = new int[vertexIndex];
        int ai1[] = new int[vertexIndex];
        int ai2[] = new int[vertexIndex];
        int ai3[] = new int[vertexIndex];
        for (int k = 0; k < vertexIndex; k++) {
            ai[k] = 0;
            ai1[k] = 0;
            ai2[k] = 0;
            ai3[k] = 0;
        }

        for (int l = 0; l < numFaces; l++) {
            if (faceIntensity[l] == anInt270) {
                for (int i1 = 0; i1 < faceNumVertices[l]; i1++) {
                    int k1 = faceVertices[l][i1];
                    ai[k1] += distXRatio[l];
                    ai1[k1] += distYRatio[l];
                    ai2[k1] += distZRatio[l];
                    ai3[k1]++;
                }

            }
        }

        for (int j1 = 0; j1 < vertexIndex; j1++) {
            if (ai3[j1] > 0) {
                vertexIntensity[j1] = (ai[j1] * distX + ai1[j1] * distY + ai2[j1] * distZ) / (i * ai3[j1]);
            }
        }
    }

    public void doSomeDistanceCalculations() {
        if (aBoolean262 && aBoolean261) {
            return;
        }
        for (int i = 0; i < numFaces; i++) {
            int ai[] = faceVertices[i];

            int j = xPosition[ai[0]];
            int k = yPosition[ai[0]];
            int l = zPosition[ai[0]];

            int i1 = xPosition[ai[1]] - j;
            int j1 = yPosition[ai[1]] - k;
            int k1 = zPosition[ai[1]] - l;

            int l1 = xPosition[ai[2]] - j;
            int i2 = yPosition[ai[2]] - k;
            int j2 = zPosition[ai[2]] - l;

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
            distXRatio[i] = (distX * 0x10000) / distance;
            distYRatio[i] = (distY * 0x10000) / distance;
            distZRatio[i] = (distZ * 65535) / distance;
            normalScale[i] = -1;
        }

        recalculateFaceLighting();
    }

    private void transform() {
        if (transformState == 2) {
            transformState = 0;
            for (int i = 0; i < vertexIndex; i++) {
                xPosition[i] = vertexX[i];
                yPosition[i] = vertexY[i];
                zPosition[i] = vertexZ[i];
            }

            anInt248 = anInt250 = anInt252 = 0xff676981;
            anInt302 = anInt249 = anInt251 = anInt253 = 0x98967f;
            return;
        }
        if (transformState == 1) {
            transformState = 0;
            for (int j = 0; j < vertexIndex; j++) {
                xPosition[j] = vertexX[j];
                yPosition[j] = vertexY[j];
                zPosition[j] = vertexZ[j];
            }

            if (state >= 2) {
                rotate(rotX, rotY, rotZ);
            }
            if (state >= 3) {
                scale(scaleX, scaleY, scaleZ);
            }
            if (state >= 4) {
                doSomeVertexTransformation(initially256_1, initially256_2, initially256_3, initially256_4,
                        initially256_5, initially256_6);
            }
            if (state >= 1) {
                translate(translateX, translateY, translateZ);
            }
            doSomeBoundsChecking();
            doSomeDistanceCalculations();
        }
    }

    public void project(Camera camera, int viewDistance, int clipNear) {
        transform();
        if (anInt252 > camera.getFrustumNearZ() ||
                anInt253 < camera.getFrustumFarZ() ||
                anInt248 > camera.getFrustumMinX() ||
                anInt249 < camera.getFrustumMaxX() ||
                anInt250 > camera.getFrustumMaxY() ||
                anInt251 < camera.getFrustumMinY()) {
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
            l2 = trigValues2[camera.getRoll()];
            i3 = trigValues2[camera.getRoll() + 1024];
        }
        if (camera.getPitch() != 0) {
            l3 = trigValues2[camera.getPitch()];
            i4 = trigValues2[camera.getPitch() + 1024];
        }
        if (camera.getYaw() != 0) {
            j3 = trigValues2[camera.getYaw()];
            k3 = trigValues2[camera.getYaw() + 1024];
        }
        for (int index = 0; index < vertexIndex; index++) {
            int k4 = xPosition[index] - camera.getX();
            int l4 = yPosition[index] - camera.getY();
            int i5 = zPosition[index] - camera.getZ();
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
                vertexViewX[index] = (k4 << viewDistance) / i5;
            } else {
                vertexViewX[index] = k4 << viewDistance;
            }
            if (i5 >= clipNear) {
                vertexViewY[index] = (l4 << viewDistance) / i5;
            } else {
                vertexViewY[index] = l4 << viewDistance;
            }
            projectVertexX[index] = k4;
            projectVertexY[index] = l4;
            projectVertexZ[index] = i5;
        }

    }

    public void resetTransformation() {
        transform();
        for (int i = 0; i < vertexIndex; i++) {
            vertexX[i] = xPosition[i];
            vertexY[i] = yPosition[i];
            vertexZ[i] = zPosition[i];
        }

        translateX = translateY = translateZ = 0;
        rotX = rotY = rotZ = 0;
        scaleX = scaleY = scaleZ = 256;
        initially256_1 = initially256_2 = initially256_3 = initially256_4 = initially256_5 = initially256_6 = 256;
        state = 0;
    }

    public Model createNewGiantCrystalFromThisModel() {
        Model models[] = new Model[1];
        models[0] = this;
        Model gameModel = new Model(models, 1);
        gameModel.anInt245 = anInt245;
        gameModel.transparent = transparent;
        return gameModel;
    }

    public Model createNewModelFromThisOne(boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        Model models[] = new Model[1];
        models[0] = this;
        Model gameModel = new Model(models, 1, flag, flag1, flag2, flag3);
        gameModel.anInt245 = anInt245;
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
        int i = someConstantsRanges2[abyte0[indexInByteArray++] & 0xff];
        int j = someConstantsRanges2[abyte0[indexInByteArray++] & 0xff];
        int k = someConstantsRanges2[abyte0[indexInByteArray++] & 0xff];
        int l = (i * 4096 + j * 64 + k) - 0x20000;
        if (l == 0x1e240) {
            l = anInt270;
        }
        return l;
    }

}
