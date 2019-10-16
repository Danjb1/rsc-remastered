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

    public static final int USE_GOURAUD_LIGHTING = 12345678;

    private static int sine9[] = new int[512];
    private static int sine11[] = new int[2048];
    private static int base64Alphabet[] = new int[256];

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
    public int transformState = 1;
    public boolean visible;
    public int x1;
    public int x2;
    public int y1;
    public int y2;
    public int z1;
    public int z2;
    public boolean translucent;
    public boolean transparent;
    public int entityId = -1;
    public int faceTag[];
    private boolean autoCommit;
    public boolean isolated;
    public boolean unlit;
    public boolean unpickable;
    public boolean projected;
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
    private int diameter = USE_GOURAUD_LIGHTING;
    private int lightDirectionX = 180;
    private int lightDirectionY = 155;
    private int lightDirectionZ = 95;
    private int lightDirectionMagnitude = 256;
    protected int lightDiffuse = 512;
    public int lightAmbience = 32;
    private int dataIndex;

    static {
        for (int i = 0; i < 256; i++) {
            // 32768 is 2^15
            sine9[i] = (int) (Math.sin(i * 0.02454369D) * 32768D);
            sine9[i + 256] = (int) (Math.cos(i * 0.02454369D) * 32768D);
        }

        for (int i = 0; i < 1024; i++) {
            sine11[i] = (int) (Math.sin(i * 0.00613592315D) * 32768D);
            sine11[i + 1024] = (int) (Math.cos(i * 0.00613592315D) * 32768D);
        }

        for (int i = 0; i < 10; i++) {
            base64Alphabet[48 + i] = i;
        }

        for (int i = 0; i < 26; i++) {
            base64Alphabet[65 + i] = i + 10;
        }

        for (int i = 0; i < 26; i++) {
            base64Alphabet[97 + i] = i + 36;
        }

        base64Alphabet[163] = 62;
        base64Alphabet[36] = 63;
    }

    /**
     * Creates a Model from a byte array.
     *
     * @param data
     * @param offset
     */
    public Model(byte data[], int offset) {
        int numVertices = DataUtils.getUnsignedShort(data, offset);
        offset += 2;
        int numFaces = DataUtils.getUnsignedShort(data, offset);
        offset += 2;
        initialise(numVertices, numFaces);
        for (int i = 0; i < numVertices; i++) {
            vertexX[i] = DataUtils.getSigned2Bytes(data, offset);
            offset += 2;
        }

        for (int i = 0; i < numVertices; i++) {
            vertexY[i] = DataUtils.getSigned2Bytes(data, offset);
            offset += 2;
        }

        for (int i = 0; i < numVertices; i++) {
            vertexZ[i] = DataUtils.getSigned2Bytes(data, offset);
            offset += 2;
        }

        this.numVertices = numVertices;
        for (int i = 0; i < numFaces; i++) {
            numVerticesPerFace[i] = data[offset++] & 0xff;
        }

        for (int i = 0; i < numFaces; i++) {
            faceFillFront[i] = DataUtils.getSigned2Bytes(data, offset);
            offset += 2;
            if (faceFillFront[i] == 32767) {
                faceFillFront[i] = USE_GOURAUD_LIGHTING;
            }
        }

        for (int i = 0; i < numFaces; i++) {
            faceFillBack[i] = DataUtils.getSigned2Bytes(data, offset);
            offset += 2;
            if (faceFillBack[i] == 32767) {
                faceFillBack[i] = USE_GOURAUD_LIGHTING;
            }
        }

        for (int i = 0; i < numFaces; i++) {
            int k2 = data[offset++] & 0xff;
            if (k2 == 0) {
                faceIntensity[i] = 0;
            } else {
                faceIntensity[i] = USE_GOURAUD_LIGHTING;
            }
        }

        for (int i = 0; i < numFaces; i++) {
            faceVertices[i] = new int[numVerticesPerFace[i]];
            for (int i3 = 0; i3 < numVerticesPerFace[i]; i3++) {
                if (numVertices < 256) {
                    faceVertices[i][i3] = data[offset++] & 0xff;
                } else {
                    faceVertices[i][i3] = DataUtils.getUnsignedShort(data, offset);
                    offset += 2;
                }
            }
        }

        this.numFaces = numFaces;
    }

    /**
     * Loads a Model from a file.
     *
     * @param path
     */
    public Model(String path) {
        byte abyte0[] = null;
        try {
            java.io.InputStream inputstream = DataUtils.streamFromPath(path);
            DataInputStream datainputstream = new DataInputStream(inputstream);
            abyte0 = new byte[3];
            dataIndex = 0;
            for (int i = 0; i < 3; i += datainputstream.read(abyte0, i, 3 - i)) {
                ;
            }
            int k = readBase64(abyte0);
            abyte0 = new byte[k];
            dataIndex = 0;
            for (int j = 0; j < k; j += datainputstream.read(abyte0, j, k - j)) {
                ;
            }
            datainputstream.close();
        } catch (IOException _ex) {
            numVertices = 0;
            numFaces = 0;
            return;
        }
        int l = readBase64(abyte0);
        int someCount = readBase64(abyte0);
        initialise(l, someCount);
        for (int j3 = 0; j3 < l; j3++) {
            int j1 = readBase64(abyte0);
            int k1 = readBase64(abyte0);
            int l1 = readBase64(abyte0);
            addUniqueVertex(j1, k1, l1);
        }

        for (int k3 = 0; k3 < someCount; k3++) {
            int i2 = readBase64(abyte0);
            int j2 = readBase64(abyte0);
            int k2 = readBase64(abyte0);
            int l2 = readBase64(abyte0);
            lightDiffuse = readBase64(abyte0);
            lightAmbience = readBase64(abyte0);
            int i3 = readBase64(abyte0);
            int ai[] = new int[i2];
            for (int l3 = 0; l3 < i2; l3++) {
                ai[l3] = readBase64(abyte0);
            }

            int ai1[] = new int[l2];
            for (int i4 = 0; i4 < l2; i4++) {
                ai1[i4] = readBase64(abyte0);
            }

            int j4 = addFace(i2, ai, j2, k2);
            if (i3 == 0) {
                faceIntensity[j4] = 0;
            } else {
                faceIntensity[j4] = USE_GOURAUD_LIGHTING;
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
        autoCommit = flag;
        isolated = flag1;
        unlit = flag2;
        unpickable = flag3;
        merge(models, i);
    }

    /**
     * Copies the Model at the given index of the given array.
     *
     * @param models
     * @param i
     */
    public Model(Model models[], int i) {
        merge(models, i);
    }

    /**
     * Creates a Model with some number of vertices and faces.
     *
     * @param maxVertices
     * @param maxFaces
     */
    public Model(int maxVertices, int maxFaces) {
        initialise(maxVertices, maxFaces);
    }

    /**
     * Creates a Model with some settings.
     *
     * @param maxVertices
     * @param maxFaces
     * @param autoCommit
     * @param isolated
     * @param unlit
     * @param unpickable
     * @param projected
     */
    public Model(
            int maxVertices,
            int maxFaces,
            boolean autoCommit,
            boolean isolated,
            boolean unlit,
            boolean unpickable,
            boolean projected) {
        this.autoCommit = autoCommit;
        this.isolated = isolated;
        this.unlit = unlit;
        this.unpickable = unpickable;
        this.projected = projected;
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

    public void clearProjection() {
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

    public void removeGeometry(int faces, int vertices) {
        numFaces -= faces;
        if (numFaces < 0) {
            numFaces = 0;
        }
        numVertices -= vertices;
        if (numVertices < 0) {
            numVertices = 0;
        }
    }

    /**
     * Merges some other Models into this one.
     *
     * @param models
     * @param numModels
     */
    public void merge(Model models[], int numModels) {

        int numFaces = 0;
        int numVertices = 0;

        for (int i = 0; i < numModels; i++) {
            numFaces += models[i].numFaces;
            numVertices += models[i].numVertices;
        }

        initialise(numVertices, numFaces);

        for (int modelId = 0; modelId < numModels; modelId++) {
            Model gameModel = models[modelId];
            gameModel.commitTransform();
            lightAmbience = gameModel.lightAmbience;
            lightDiffuse = gameModel.lightDiffuse;
            lightDirectionX = gameModel.lightDirectionX;
            lightDirectionY = gameModel.lightDirectionY;
            lightDirectionZ = gameModel.lightDirectionZ;
            lightDirectionMagnitude = gameModel.lightDirectionMagnitude;

            for (int faceId = 0; faceId < gameModel.numFaces; faceId++) {

                int faces[] = new int[gameModel.numVerticesPerFace[faceId]];
                int vertices[] = gameModel.faceVertices[faceId];

                for (int vertId = 0; vertId < gameModel.numVerticesPerFace[faceId]; vertId++) {
                    faces[vertId] = addUniqueVertex(
                            gameModel.vertexX[vertices[vertId]],
                            gameModel.vertexY[vertices[vertId]],
                            gameModel.vertexZ[vertices[vertId]]);
                }

                int faceIndex = addFace(
                        gameModel.numVerticesPerFace[faceId],
                        faces,
                        gameModel.faceFillFront[faceId],
                        gameModel.faceFillBack[faceId]);

                faceIntensity[faceIndex] = gameModel.faceIntensity[faceId];
                faceCameraNormalScale[faceIndex] = gameModel.faceCameraNormalScale[faceId];
                faceCameraNormalMagnitude[faceIndex] = gameModel.faceCameraNormalMagnitude[faceId];
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
    public int addUniqueVertex(int x, int y, int z) {

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
    public int addVertex(int x, int z, int y) {

        if (numVertices >= maxVertices) {
            return -1;
        }

        vertexX[numVertices] = x;
        vertexY[numVertices] = z;
        vertexZ[numVertices] = y;

        return numVertices++;
    }

    public int addFace(int numVertices, int vertices[], int fillFront, int fillBack) {

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

    public Model[] split(int pieceDx, int pieceDz, int rows, int count, int maxVertices, boolean unpickable) {
        commitTransform();
        int numVerticesInPiece[] = new int[count];
        int numFacesInPiece[] = new int[count];
        for (int i = 0; i < count; i++) {
            numVerticesInPiece[i] = 0;
            numFacesInPiece[i] = 0;
        }

        for (int i = 0; i < numFaces; i++) {
            int j2 = 0;
            int k2 = 0;
            int i3 = numVerticesPerFace[i];
            int ai2[] = faceVertices[i];
            for (int i4 = 0; i4 < i3; i4++) {
                j2 += vertexX[ai2[i4]];
                k2 += vertexZ[ai2[i4]];
            }

            int k4 = j2 / (i3 * pieceDx) + (k2 / (i3 * pieceDz)) * rows;
            numVerticesInPiece[k4] += i3;
            numFacesInPiece[k4]++;
        }

        Model models[] = new Model[count];
        for (int i = 0; i < count; i++) {
            if (numVerticesInPiece[i] > maxVertices) {
                numVerticesInPiece[i] = maxVertices;
            }
            models[i] = new Model(
                    numVerticesInPiece[i],
                    numFacesInPiece[i],
                    true, true, true, unpickable, true);
            models[i].lightDiffuse = lightDiffuse;
            models[i].lightAmbience = lightAmbience;
        }

        for (int i = 0; i < numFaces; i++) {
            int k3 = 0;
            int j4 = 0;
            int l4 = numVerticesPerFace[i];
            int ai3[] = faceVertices[i];
            for (int i5 = 0; i5 < l4; i5++) {
                k3 += vertexX[ai3[i5]];
                j4 += vertexZ[ai3[i5]];
            }

            int j5 = k3 / (l4 * pieceDx) + (j4 / (l4 * pieceDz)) * rows;
            copyModelData(models[j5], ai3, l4, i);
        }

        for (int i = 0; i < count; i++) {
            models[i].clearProjection();
        }

        return models;
    }

    public void copyModelData(
            Model gameModel,
            int srcVertices[],
            int count,
            int faceId) {

        int destVertices[] = new int[count];
        for (int i = 0; i < count; i++) {
            int l = destVertices[i] = gameModel.addUniqueVertex(
                    vertexX[srcVertices[i]],
                    vertexY[srcVertices[i]],
                    vertexZ[srcVertices[i]]);
            gameModel.vertexIntensity[l] = vertexIntensity[srcVertices[i]];
            gameModel.vertexAmbience[l] = vertexAmbience[srcVertices[i]];
        }

        int nextIndex = gameModel.addFace(count, destVertices, faceFillFront[faceId], faceFillBack[faceId]);
        if (!gameModel.unpickable && !unpickable) {
            gameModel.faceTag[nextIndex] = faceTag[faceId];
        }
        gameModel.faceIntensity[nextIndex] = faceIntensity[faceId];
        gameModel.faceCameraNormalScale[nextIndex] = faceCameraNormalScale[faceId];
        gameModel.faceCameraNormalMagnitude[nextIndex] = faceCameraNormalMagnitude[faceId];
    }

    public void setLighting(
            boolean useGouraud,
            int ambient,
            int diffuse,
            int lightDirectionX,
            int lightDirectionY,
            int lightDirectionZ) {

        lightAmbience = 256 - ambient * 4;
        lightDiffuse = (64 - diffuse) * 16 + 128;

        if (unlit) {
            return;
        }

        for (int i = 0; i < numFaces; i++) {
            if (useGouraud) {
                faceIntensity[i] = USE_GOURAUD_LIGHTING;
            } else {
                faceIntensity[i] = 0;
            }
        }

        this.lightDirectionX = lightDirectionX;
        this.lightDirectionY = lightDirectionY;
        this.lightDirectionZ = lightDirectionZ;
        lightDirectionMagnitude = (int) Math.sqrt(
                lightDirectionX * lightDirectionX
                + lightDirectionY * lightDirectionY
                + lightDirectionZ * lightDirectionZ);
        light();
    }

    public void setLighting(
            int ambient,
            int diffuse,
            int lightDirectionX,
            int lightDirectionY,
            int lightDirectionZ) {

        lightAmbience = 256 - ambient * 4;
        lightDiffuse = (64 - diffuse) * 16 + 128;

        if (unlit) {
            return;
        }

        this.lightDirectionX = lightDirectionX;
        this.lightDirectionY = lightDirectionY;
        this.lightDirectionZ = lightDirectionZ;
        lightDirectionMagnitude = (int) Math.sqrt(
                lightDirectionX * lightDirectionX
                + lightDirectionY * lightDirectionY
                + lightDirectionZ * lightDirectionZ);
        light();
    }

    public void setLighting(
            int lightDirectionX,
            int lightDirectionY,
            int lightDirectionZ) {

        if (unlit) {
            return;
        }

        this.lightDirectionX = lightDirectionX;
        this.lightDirectionY = lightDirectionY;
        this.lightDirectionZ = lightDirectionZ;
        lightDirectionMagnitude = (int) Math.sqrt(lightDirectionX * lightDirectionX + lightDirectionY * lightDirectionY + lightDirectionZ * lightDirectionZ);
        light();
    }

    public void setVertexAmbience(int vertex, int ambience) {
        vertexAmbience[vertex] = (byte) ambience;
    }

    public void modRotation(int rotX, int rotY, int rotZ) {
        this.rotX += rotX & 0xff;
        this.rotY += rotY & 0xff;
        this.rotZ += rotZ & 0xff;
        determineTransformType();
        transformState = 1;
    }

    public void setRotation(int i, int j, int k) {
        rotX = i & 0xff;
        rotY = j & 0xff;
        rotZ = k & 0xff;
        determineTransformType();
        transformState = 1;
    }

    public void translate(int translateX, int translateY, int translateZ) {
        this.translateX += translateX;
        this.translateY += translateY;
        this.translateZ += translateZ;
        determineTransformType();
        transformState = 1;
    }

    public void setTranslate(int translateX, int translateY, int translateZ) {
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
        determineTransformType();
        transformState = 1;
    }

    private void determineTransformType() {
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

    private void applyTranslate(int dx, int dy, int dz) {
        for (int i = 0; i < numVertices; i++) {
            vertexTransformedX[i] += dx;
            vertexTransformedY[i] += dy;
            vertexTransformedZ[i] += dz;
        }
    }

    private void applyRotation(int rotX, int rotY, int rotZ) {
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

    private void applyShear(
            int xy,
            int xz,
            int yx,
            int yz,
            int zx,
            int zy) {
        for (int i = 0; i < numVertices; i++) {
            if (xy != 0) {
                vertexTransformedX[i] += vertexTransformedY[i] * xy >> 8;
            }
            if (xz != 0) {
                vertexTransformedZ[i] += vertexTransformedY[i] * xz >> 8;
            }
            if (yx != 0) {
                vertexTransformedX[i] += vertexTransformedZ[i] * yx >> 8;
            }
            if (yz != 0) {
                vertexTransformedY[i] += vertexTransformedZ[i] * yz >> 8;
            }
            if (zx != 0) {
                vertexTransformedZ[i] += vertexTransformedX[i] * zx >> 8;
            }
            if (zy != 0) {
                vertexTransformedY[i] += vertexTransformedX[i] * zy >> 8;
            }
        }

    }

    private void scale(int scaleX, int scaleY, int scaleZ) {
        for (int l = 0; l < numVertices; l++) {
            vertexTransformedX[l] = vertexTransformedX[l] * scaleX >> 8;
            vertexTransformedY[l] = vertexTransformedY[l] * scaleY >> 8;
            vertexTransformedZ[l] = vertexTransformedZ[l] * scaleZ >> 8;
        }

    }

    private void computeBounds() {
        x1 = y1 = z1 = 0xf423f;
        diameter = x2 = y2 = z2 = 0xfff0bdc1;
        for (int i = 0; i < numFaces; i++) {
            int ai[] = faceVertices[i];
            int k = ai[0];
            int i1 = numVerticesPerFace[i];
            int x1;
            int x2 = x1 = vertexTransformedX[k];
            int y1;
            int y2 = y1 = vertexTransformedY[k];
            int z1;
            int z2 = z1 = vertexTransformedZ[k];
            for (int j = 0; j < i1; j++) {
                int l = ai[j];
                if (vertexTransformedX[l] < x1) {
                    x1 = vertexTransformedX[l];
                } else if (vertexTransformedX[l] > x2) {
                    x2 = vertexTransformedX[l];
                }
                if (vertexTransformedY[l] < y1) {
                    y1 = vertexTransformedY[l];
                } else if (vertexTransformedY[l] > y2) {
                    y2 = vertexTransformedY[l];
                }
                if (vertexTransformedZ[l] < z1) {
                    z1 = vertexTransformedZ[l];
                } else if (vertexTransformedZ[l] > z2) {
                    z2 = vertexTransformedZ[l];
                }
            }

            if (!isolated) {
                faceBoundLeft[i] = x1;
                faceBoundRight[i] = x2;
                faceBoundBottom[i] = y1;
                faceBoundTop[i] = y2;
                faceBoundNear[i] = z1;
                faceBoundFar[i] = z2;
            }
            if (x2 - x1 > diameter) {
                diameter = x2 - x1;
            }
            if (y2 - y1 > diameter) {
                diameter = y2 - y1;
            }
            if (z2 - z1 > diameter) {
                diameter = z2 - z1;
            }
            if (x1 < this.x1) {
                this.x1 = x1;
            }
            if (x2 > this.x2) {
                this.x2 = x2;
            }
            if (y1 < this.y1) {
                this.y1 = y1;
            }
            if (y2 > this.y2) {
                this.y2 = y2;
            }
            if (z1 < this.z1) {
                this.z1 = z1;
            }
            if (z2 > this.z2) {
                this.z2 = z2;
            }
        }

    }

    public void light() {
        if (unlit) {
            return;
        }
        int i = lightDiffuse * lightDirectionMagnitude >> 8;
        for (int j = 0; j < numFaces; j++) {
            if (faceIntensity[j] != USE_GOURAUD_LIGHTING) {
                faceIntensity[j] =
                        (faceNormalX[j] * lightDirectionX
                        + faceNormalY[j] * lightDirectionY
                        + faceNormalZ[j] * lightDirectionZ) / i;
            }
        }

        int normalX[] = new int[numVertices];
        int normalY[] = new int[numVertices];
        int normalZ[] = new int[numVertices];
        int normalMagnitude[] = new int[numVertices];
        for (int k = 0; k < numVertices; k++) {
            normalX[k] = 0;
            normalY[k] = 0;
            normalZ[k] = 0;
            normalMagnitude[k] = 0;
        }

        for (int l = 0; l < numFaces; l++) {
            if (faceIntensity[l] == USE_GOURAUD_LIGHTING) {
                for (int i1 = 0; i1 < numVerticesPerFace[l]; i1++) {
                    int k1 = faceVertices[l][i1];
                    normalX[k1] += faceNormalX[l];
                    normalY[k1] += faceNormalY[l];
                    normalZ[k1] += faceNormalZ[l];
                    normalMagnitude[k1]++;
                }

            }
        }

        for (int j1 = 0; j1 < numVertices; j1++) {
            if (normalMagnitude[j1] > 0) {
                vertexIntensity[j1] =
                        (normalX[j1] * lightDirectionX
                        + normalY[j1] * lightDirectionY
                        + normalZ[j1] * lightDirectionZ)
                        / (i * normalMagnitude[j1]);
            }
        }
    }

    public void relight() {
        if (unlit && isolated) {
            return;
        }
        for (int i = 0; i < numFaces; i++) {
            int ai[] = faceVertices[i];

            int aX = vertexTransformedX[ai[0]];
            int aY = vertexTransformedY[ai[0]];
            int aZ = vertexTransformedZ[ai[0]];

            int bX = vertexTransformedX[ai[1]] - aX;
            int bY = vertexTransformedY[ai[1]] - aY;
            int bZ = vertexTransformedZ[ai[1]] - aZ;

            int cX = vertexTransformedX[ai[2]] - aX;
            int cY = vertexTransformedY[ai[2]] - aY;
            int cZ = vertexTransformedZ[ai[2]] - aZ;

            int normalX = (bY * cZ) - (cY * bZ);
            int normalY = (bZ * cX) - (cZ * bX);
            int normalZ;

            for (normalZ = bX * cY - cX * bY;
                    normalX > 8192
                    || normalY > 8192
                    || normalZ > 8192
                    || normalX < -8192
                    || normalY < -8192
                    || normalZ < -8192; normalZ >>= 1) {
                normalX >>= 1;
                normalY >>= 1;
            }

            int normalMagnitude = (int) (256D * Math.sqrt(
                    normalX * normalX
                    + normalY * normalY
                    + normalZ * normalZ));
            if (normalMagnitude <= 0) {
                normalMagnitude = 1;
            }
            faceNormalX[i] = (normalX * 0x10000) / normalMagnitude;
            faceNormalY[i] = (normalY * 0x10000) / normalMagnitude;
            faceNormalZ[i] = (normalZ * 65535) / normalMagnitude;
            faceCameraNormalScale[i] = -1;
        }

        light();
    }

    private void applyTransform() {
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
                applyRotation(rotX, rotY, rotZ);
            }
            if (transformType >= 3) {
                scale(scaleX, scaleY, scaleZ);
            }
            if (transformType >= 4) {
                applyShear(shearXY, shearXZ, shearYX, shearYZ,
                        shearZX, shearZY);
            }
            if (transformType >= 1) {
                applyTranslate(translateX, translateY, translateZ);
            }
            computeBounds();
            relight();
        }
    }

    public void project(Camera camera, int viewDistance, int clipNear) {
        applyTransform();
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

    public void commitTransform() {
        applyTransform();
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

    public Model copy() {
        Model models[] = new Model[1];
        models[0] = this;
        Model gameModel = new Model(models, 1);
        gameModel.depth = depth;
        gameModel.transparent = transparent;
        return gameModel;
    }

    public Model copy(
            boolean autoCommit,
            boolean isolated,
            boolean unlit,
            boolean unpickable) {
        Model models[] = new Model[1];
        models[0] = this;
        Model gameModel = new Model(models, 1, autoCommit, isolated, unlit, unpickable);
        gameModel.depth = depth;
        return gameModel;
    }

    public void copyTransform(Model gameModel) {
        rotX = gameModel.rotX;
        rotY = gameModel.rotY;
        rotZ = gameModel.rotZ;
        translateX = gameModel.translateX;
        translateY = gameModel.translateY;
        translateZ = gameModel.translateZ;
        determineTransformType();
        transformState = 1;
    }

    public int readBase64(byte data[]) {
        for (; data[dataIndex] == 10 || data[dataIndex] == 13; dataIndex++) {
            ;
        }
        int high = base64Alphabet[data[dataIndex++] & 0xff];
        int mid = base64Alphabet[data[dataIndex++] & 0xff];
        int low = base64Alphabet[data[dataIndex++] & 0xff];
        int val = (high * 4096 + mid * 64 + low) - 0x20000;
        if (val == 0x1e240) {
            val = USE_GOURAUD_LIGHTING;
        }
        return val;
    }

}
