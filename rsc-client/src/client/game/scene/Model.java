package client.game.scene;

import java.io.DataInputStream;
import java.io.IOException;

import org.joml.Vector2i;
import org.joml.Vector3i;

import client.util.DataUtils;
import client.util.VectorUtils;

/**
 * Class representing a 3d model.
 *
 * @author Dan Bryce
 */
public class Model {

    public static final int USE_GOURAUD_LIGHTING = 12345678;

    private static final int DEFAULT_SCALE = 256;

    public enum TransformState {
        CLEAN,
        PENDING,
        BILLBOARD
    }

    private static int sine9[] = new int[512];
    private static int sine11[] = new int[2048];
    private static int base64Alphabet[] = new int[256];

    // Vertices
    public int numVertices;
    public int maxVertices;
    public Vector3i[] vertices;
    public Vector3i[] verticesTransformed;
    public Vector3i[] verticesProjected;
    public Vector2i[] verticesView;
    public int vertexIntensity[];
    public byte vertexAmbience[];

    // Transform
    private Vector3i translate;
    private Vector3i rotate;
    private Vector3i scale;
    private int shearXY;
    private int shearXZ;
    private int shearYX;
    private int shearYZ;
    private int shearZX;
    private int shearZY;
    private int transformType;
    public TransformState transformState = TransformState.PENDING;

    // Faces
    public int numFaces;
    private int maxFaces;
    public int numVerticesPerFace[];
    public int faceVertices[][];
    public int faceFillFront[];
    public int faceFillBack[];
    public int faceCameraNormalMagnitude[];
    public int faceCameraNormalScale[];
    public int faceIntensity[];
    public Vector3i[] faceNormals;
    public int faceTag[];
    private int faceBoundLeft[];
    private int faceBoundRight[];
    private int faceBoundBottom[];
    private int faceBoundTop[];
    private int faceBoundNear[];
    private int faceBoundFar[];

    // Properties
    public int entityId = -1;
    public int depth;
    public boolean visible;
    public boolean translucent;
    public boolean transparent;
    private boolean autoCommit;
    public boolean isolated;
    public boolean unlit;
    public boolean unpickable;
    public boolean projected;

    // Bounds
    public int x1;
    public int x2;
    public int y1;
    public int y2;
    public int z1;
    public int z2;

    // Lighting
    private int diameter = USE_GOURAUD_LIGHTING;
    private Vector3i lightDirection = new Vector3i(180, 155, 95);
    protected int lightDiffuse = 512;
    public int lightAmbience = 32;

    // Base64 Input
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
            vertices[i].x = DataUtils.getSigned2Bytes(data, offset);
            offset += 2;
        }

        for (int i = 0; i < numVertices; i++) {
            vertices[i].y = DataUtils.getSigned2Bytes(data, offset);
            offset += 2;
        }

        for (int i = 0; i < numVertices; i++) {
            vertices[i].z = DataUtils.getSigned2Bytes(data, offset);
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

        int numVertices = readBase64(abyte0);
        int numFaces = readBase64(abyte0);

        initialise(numVertices, numFaces);

        for (int j3 = 0; j3 < numVertices; j3++) {
            int x = readBase64(abyte0);
            int y = readBase64(abyte0);
            int z = readBase64(abyte0);
            addUniqueVertex(x, y, z);
        }

        for (int k3 = 0; k3 < numFaces; k3++) {
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

        transformState = TransformState.PENDING;
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
        vertices = new Vector3i[maxVertices];
        for (int i = 0; i < maxVertices; i++) {
            vertices[i] = new Vector3i();
        }
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
            clearProjection();
        }
        if (!unpickable) {
            faceTag = new int[maxFaces];
        }
        if (autoCommit) {
            verticesTransformed = vertices;
        } else {
            verticesTransformed = new Vector3i[maxVertices];
        }
        if (!unlit || !isolated) {
            faceNormals = new Vector3i[maxFaces];
            for (int i = 0; i < maxFaces; i++) {
                faceNormals[i] = new Vector3i();
            }
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
        translate = new Vector3i(0, 0, 0);
        rotate = new Vector3i(0, 0, 0);
        scale = new Vector3i(DEFAULT_SCALE, DEFAULT_SCALE, DEFAULT_SCALE);
        shearXY = shearXZ = shearYX = shearYZ = shearZX = shearZY = 256;
        transformType = 0;
    }

    public void clearProjection() {
        verticesProjected = new Vector3i[numVertices];
        verticesView = new Vector2i[numVertices];

        for (int i = 0; i < numVertices; i++) {
            verticesProjected[i] = new Vector3i();
            verticesView[i] = new Vector2i();
        }
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
            lightDirection = gameModel.lightDirection;

            for (int faceId = 0; faceId < gameModel.numFaces; faceId++) {

                int faces[] = new int[gameModel.numVerticesPerFace[faceId]];
                int vertices[] = gameModel.faceVertices[faceId];

                for (int vertId = 0; vertId < gameModel.numVerticesPerFace[faceId]; vertId++) {
                    faces[vertId] = addUniqueVertex(
                            gameModel.vertices[vertices[vertId]].x,
                            gameModel.vertices[vertices[vertId]].y,
                            gameModel.vertices[vertices[vertId]].z);
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

        transformState = TransformState.PENDING;
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
            if (vertices[l].x == x && vertices[l].y == y && vertices[l].z == z) {
                return l;
            }
        }

        if (numVertices >= maxVertices) {
            return -1;
        }

        vertices[numVertices] = new Vector3i(x, y, z);

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

        vertices[numVertices] = new Vector3i(x, y, z);

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
        transformState = TransformState.PENDING;

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
                j2 += vertices[ai2[i4]].x;
                k2 += vertices[ai2[i4]].z;
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
                k3 += vertices[ai3[i5]].x;
                j4 += vertices[ai3[i5]].z;
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
                    vertices[srcVertices[i]].x,
                    vertices[srcVertices[i]].y,
                    vertices[srcVertices[i]].z);
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

        lightDirection.x = lightDirectionX;
        lightDirection.y = lightDirectionY;
        lightDirection.z = lightDirectionZ;
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

        lightDirection.x = lightDirectionX;
        lightDirection.y = lightDirectionY;
        lightDirection.z = lightDirectionZ;
        light();
    }

    public void setLighting(
            int lightDirectionX,
            int lightDirectionY,
            int lightDirectionZ) {

        if (unlit) {
            return;
        }

        lightDirection.x = lightDirectionX;
        lightDirection.y = lightDirectionY;
        lightDirection.z = lightDirectionZ;
        light();
    }

    public void setVertexAmbience(int vertex, int ambience) {
        vertexAmbience[vertex] = (byte) ambience;
    }

    public void modRotation(int rotX, int rotY, int rotZ) {
        rotate.add(rotX & 0xff, rotY & 0xff, rotZ & 0xff);
        determineTransformType();
        transformState = TransformState.PENDING;
    }

    public void setRotation(int rotX, int rotY, int rotZ) {
        rotate.set(rotX & 0xff, rotY & 0xff, rotZ & 0xff);
        determineTransformType();
        transformState = TransformState.PENDING;
    }

    public void translate(int translateX, int translateY, int translateZ) {
        translate.add(translateX, translateY, translateZ);
        determineTransformType();
        transformState = TransformState.PENDING;
    }

    public void setTranslate(int translateX, int translateY, int translateZ) {
        translate.set(translateX, translateY, translateZ);
        determineTransformType();
        transformState = TransformState.PENDING;
    }

    private void determineTransformType() {
        if (shearXY != 256 || shearXZ != 256 || shearYX != 256 || shearYZ != 256
                || shearZX != 256 || shearZY != 256) {
            transformType = 4;
            return;
        }
        if (scale.x != DEFAULT_SCALE || scale.y != DEFAULT_SCALE || scale.z != DEFAULT_SCALE) {
            transformType = 3;
            return;
        }
        if (rotate.x != 0 || rotate.y != 0 || rotate.z != 0) {
            transformType = 2;
            return;
        }
        if (translate.x != 0 || translate.y != 0 || translate.z != 0) {
            transformType = 1;
            return;
        } else {
            transformType = 0;
            return;
        }
    }

    private void applyTranslate(int dx, int dy, int dz) {
        for (int i = 0; i < numVertices; i++) {
            verticesTransformed[i].x += dx;
            verticesTransformed[i].y += dy;
            verticesTransformed[i].z += dz;
        }
    }

    private void applyRotation(int rotX, int rotY, int rotZ) {
        for (int i = 0; i < numVertices; i++) {
            if (rotZ != 0) {
                int l = sine9[rotZ];
                int k1 = sine9[rotZ + 256];
                int j2 = verticesTransformed[i].y * l + verticesTransformed[i].x * k1 >> 15;
                verticesTransformed[i].y = verticesTransformed[i].y * k1 - verticesTransformed[i].x * l >> 15;
                verticesTransformed[i].x = j2;
            }
            if (rotX != 0) {
                int i1 = sine9[rotX];
                int l1 = sine9[rotX + 256];
                int k2 = verticesTransformed[i].y * l1 - verticesTransformed[i].z * i1 >> 15;
                verticesTransformed[i].z = verticesTransformed[i].y * i1 + verticesTransformed[i].z * l1 >> 15;
                verticesTransformed[i].y = k2;
            }
            if (rotY != 0) {
                int j1 = sine9[rotY];
                int i2 = sine9[rotY + 256];
                int l2 = verticesTransformed[i].z * j1 + verticesTransformed[i].x * i2 >> 15;
                verticesTransformed[i].z = verticesTransformed[i].z * i2 - verticesTransformed[i].x * j1 >> 15;
                verticesTransformed[i].x = l2;
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
                verticesTransformed[i].x += verticesTransformed[i].y * xy >> 8;
            }
            if (xz != 0) {
                verticesTransformed[i].z += verticesTransformed[i].y * xz >> 8;
            }
            if (yx != 0) {
                verticesTransformed[i].x += verticesTransformed[i].z * yx >> 8;
            }
            if (yz != 0) {
                verticesTransformed[i].y += verticesTransformed[i].z * yz >> 8;
            }
            if (zx != 0) {
                verticesTransformed[i].z += verticesTransformed[i].x * zx >> 8;
            }
            if (zy != 0) {
                verticesTransformed[i].y += verticesTransformed[i].x * zy >> 8;
            }
        }

    }

    private void scale(int scaleX, int scaleY, int scaleZ) {
        for (int i = 0; i < numVertices; i++) {
            verticesTransformed[i].mul(scaleX >> 8);
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
            int x2 = x1 = verticesTransformed[k].x;
            int y1;
            int y2 = y1 = verticesTransformed[k].y;
            int z1;
            int z2 = z1 = verticesTransformed[k].z;
            for (int j = 0; j < i1; j++) {
                int l = ai[j];
                if (verticesTransformed[l].x < x1) {
                    x1 = verticesTransformed[l].x;
                } else if (verticesTransformed[l].x > x2) {
                    x2 = verticesTransformed[l].x;
                }
                if (verticesTransformed[l].y < y1) {
                    y1 = verticesTransformed[l].y;
                } else if (verticesTransformed[l].y > y2) {
                    y2 = verticesTransformed[l].y;
                }
                if (verticesTransformed[l].z < z1) {
                    z1 = verticesTransformed[l].z;
                } else if (verticesTransformed[l].z > z2) {
                    z2 = verticesTransformed[l].z;
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
        int i = lightDiffuse * VectorUtils.magnitude(lightDirection) >> 8;
        for (int j = 0; j < numFaces; j++) {
            if (faceIntensity[j] != USE_GOURAUD_LIGHTING) {
                faceIntensity[j] =
                        (faceNormals[j].x * lightDirection.x
                        + faceNormals[j].y * lightDirection.y
                        + faceNormals[j].z * lightDirection.z) / i;
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
                    normalX[k1] += faceNormals[l].x;
                    normalY[k1] += faceNormals[l].y;
                    normalZ[k1] += faceNormals[l].z;
                    normalMagnitude[k1]++;
                }

            }
        }

        for (int j1 = 0; j1 < numVertices; j1++) {
            if (normalMagnitude[j1] > 0) {
                vertexIntensity[j1] =
                        (normalX[j1] * lightDirection.x
                        + normalY[j1] * lightDirection.y
                        + normalZ[j1] * lightDirection.z)
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

            int aX = verticesTransformed[ai[0]].x;
            int aY = verticesTransformed[ai[0]].y;
            int aZ = verticesTransformed[ai[0]].z;

            int bX = verticesTransformed[ai[1]].x - aX;
            int bY = verticesTransformed[ai[1]].y - aY;
            int bZ = verticesTransformed[ai[1]].z - aZ;

            int cX = verticesTransformed[ai[2]].x - aX;
            int cY = verticesTransformed[ai[2]].y - aY;
            int cZ = verticesTransformed[ai[2]].z - aZ;

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
            faceNormals[i].x = (normalX * 0x10000) / normalMagnitude;
            faceNormals[i].y = (normalY * 0x10000) / normalMagnitude;
            faceNormals[i].z = (normalZ * 65535) / normalMagnitude;
            faceCameraNormalScale[i] = -1;
        }

        light();
    }

    private void applyTransform() {

        if (transformState == TransformState.BILLBOARD) {
            transformState = TransformState.CLEAN;
            for (int i = 0; i < numVertices; i++) {
                verticesTransformed[i].set(vertices[i]);
            }

            x1 = y1 = z1 = 0xff676981;
            diameter = x2 = y2 = z2 = 0x98967f;

        } else if (transformState == TransformState.PENDING) {
            transformState = TransformState.CLEAN;
            for (int i = 0; i < numVertices; i++) {
                verticesTransformed[i].set(vertices[i]);
            }

            if (transformType >= 2) {
                applyRotation(rotate.x, rotate.y, rotate.z);
            }
            if (transformType >= 3) {
                scale(scale.x, scale.y, scale.z);
            }
            if (transformType >= 4) {
                applyShear(
                        shearXY, shearXZ, shearYX, shearYZ, shearZX, shearZY);
            }
            if (transformType >= 1) {
                applyTranslate(translate.x, translate.y, translate.z);
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
            int projectedX = verticesTransformed[index].x - camera.getX();
            int projectedY = verticesTransformed[index].y - camera.getY();
            int projectedZ = verticesTransformed[index].z - camera.getZ();
            if (camera.getRoll() != 0) {
                int i2 = projectedY * l2 + projectedX * i3 >> 15;
                projectedY = projectedY * i3 - projectedX * l2 >> 15;
                projectedX = i2;
            }
            if (camera.getPitch() != 0) {
                int j2 = projectedZ * l3 + projectedX * i4 >> 15;
                projectedZ = projectedZ * i4 - projectedX * l3 >> 15;
                projectedX = j2;
            }
            if (camera.getYaw() != 0) {
                int k2 = projectedY * k3 - projectedZ * j3 >> 15;
                projectedZ = projectedY * j3 + projectedZ * k3 >> 15;
                projectedY = k2;
            }
            if (projectedZ >= clipNear) {
                verticesView[index].x = (projectedX << viewDistance) / projectedZ;
            } else {
                verticesView[index].x = projectedX << viewDistance;
            }
            if (projectedZ >= clipNear) {
                verticesView[index].y = (projectedY << viewDistance) / projectedZ;
            } else {
                verticesView[index].y = projectedY << viewDistance;
            }
            verticesProjected[index].set(projectedX, projectedY, projectedZ);
        }

    }

    public void commitTransform() {
        applyTransform();
        for (int i = 0; i < numVertices; i++) {
            vertices[i] = verticesTransformed[i];
        }

        translate.set(0, 0, 0);
        rotate.set(0, 0, 0);
        scale.set(DEFAULT_SCALE, DEFAULT_SCALE, DEFAULT_SCALE);
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
        translate.set(gameModel.translate);
        rotate.set(gameModel.rotate);
        determineTransformType();
        transformState = TransformState.PENDING;
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
