package client.game.model;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A 2D grid of Tiles.
 */
public class Sector {

    /**
     * The width of a Sector, in tiles.
     */
    public static final int WIDTH = 48;

    /**
     * The depth of a Sector, in tiles.
     */
    public static final int DEPTH = 48;

    /**
     * Total number of Tiles within a Sector.
     */
    public static final int NUM_TILES = WIDTH * DEPTH;

    /**
     * Total number of faces within a Sector.
     */
    public static final int NUM_FACES = NUM_TILES * 2;

    /**
     * An array containing all the tiles within this Sector.
     */
    private Tile[] tiles;

    /**
     * Creates a new Sector full of blank tiles.
     */
    public Sector() {
        tiles = new Tile[NUM_TILES];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile();
        }
    }

    /**
     * Sets the the Tile at the given coords.
     *
     * @param x
     * @param z
     * @param t
     */
    public void setTile(int x, int z, Tile t) {
        setTile(x * Sector.WIDTH + z, t);
    }

    /**
     * Sets the Tile at the given index.
     */
    private void setTile(int i, Tile t) {
        tiles[i] = t;
    }

    /**
     * Gets the Tile at the given coords.
     *
     * @param x
     * @param z
     * @return
     */
    public Tile getTile(int x, int z) {
        return getTile(x * Sector.WIDTH + z);
    }

    /**
     * Gets the Tile at the given index.
     *
     * @param i
     * @return
     */
    public Tile getTile(int i) {
        return tiles[i];
    }

    /**
     * Writes the Sector raw data into a ByteBuffer.
     *
     * @return
     * @throws IOException
     */
    public ByteBuffer pack() throws IOException {
        ByteBuffer out = ByteBuffer.allocate(10 * tiles.length);

        for (int i = 0; i < tiles.length; i++) {
            out.put(tiles[i].pack());
        }

        out.flip();
        return out;
    }

    /**
     * Create a new Sector from raw data packed into the given ByteBuffer.
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static Sector unpack(ByteBuffer in) throws IOException {

        if (in.remaining() < (10 * NUM_TILES)) {
            throw new IOException("Provided buffer too short");
        }

        Sector sector = new Sector();

        for (int i = 0; i < NUM_TILES; i++) {
            sector.setTile(i, Tile.unpack(in));
        }

        return sector;
    }

}
