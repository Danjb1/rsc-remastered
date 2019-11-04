package client.net;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * A dynamic object that contains data sent from/to client/server.
 */
public class Packet {

    private ChannelBuffer buffer = null;
    private int opcode = 0;

    /**
     * Creating a new empty packet.
     * 
     * @param opcode
     *            the opcode of the packet
     */
    public Packet(int opcode) {
        // Creates a new big-endian dynamic buffer with the specified estimated data
        // length.
        // More accurate estimation yields less unexpected reallocation overhead.
        this.buffer = ChannelBuffers.dynamicBuffer(4096/* 8192 */);
        this.opcode = opcode;
    }

    /**
     * Creating an already filled packet, used at the handling of the incoming
     * packets
     *
     * @param opcode
     *            the opcode of the packet
     * @param buffer
     *            the buffer
     * @param length
     *            the length of the packet
     */
    public Packet(int opcode, ChannelBuffer buffer, int length) {
        this.buffer = ChannelBuffers.copiedBuffer(buffer.readBytes(length));
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

    /**
     * The byte data type is an 8-bit signed two's complement integer. It has a
     * minimum value of -128 and a maximum value of 127 (inclusive).
     */
    public Packet putByte(int b) {
        buffer.writeByte(b);
        return this;
    }

    public Packet putBytes(byte[] b) {
        buffer.writeLong(b.length);
        buffer.writeBytes(b);
        return this;
    }

    /**
     * The short data type is a 16-bit signed two's complement integer. It has a
     * minimum value of -32,768 and a maximum value of 32,767 (inclusive).
     */
    public Packet putShort(short s) {
        buffer.writeShort(s);
        return this;
    }

    /**
     * Sends a small integer (a short).
     */
    public Packet putSmallInt(int i) {
        buffer.writeShort(i);
        return this;
    }

    /**
     * The int data type is a 32-bit signed two's complement integer. It has a
     * minimum value of -2,147,483,648 and a maximum value of 2,147,483,647
     * (inclusive).
     */
    public Packet putInt(int i) {
        buffer.writeInt(i);
        return this;
    }

    /**
     * long: The long data type is a 64-bit signed two's complement integer. It has
     * a minimum value of -9,223,372,036,854,775,808 and a maximum value of
     * 9,223,372,036,854,775,807 (inclusive). Use this data type when you need a
     * range of values wider than those provided by int.
     */
    public Packet putLong(long l) {
        buffer.writeLong(l);
        return this;
    }

    /**
     * Encodes a String into a base37 integral value.
     * 
     * @return The encoded String value.
     */
    public Packet putBase37(String string) {
        String s = "";
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c >= 'a' && c <= 'z') {
                s = s + c;
            } else if (c >= 'A' && c <= 'Z') {
                s = s + (char) ((c + 97) - 65);
            } else if (c >= '0' && c <= '9') {
                s = s + c;
            } else {
                s = s + ' ';
            }
        }

        if (s.length() > 12) {
            s = s.substring(0, 12);
        }
        long base = 0L;
        for (int i = 0; i < s.length(); i++) {
            char c1 = s.charAt(i);
            base *= 37L;
            if (c1 >= 'a' && c1 <= 'z') {
                base += (1 + c1) - 97;
            } else if (c1 >= '0' && c1 <= '9') {
                base += (27 + c1) - 48;
            }
        }
        buffer.writeLong(base);
        return this;
    }

    /**
     * https://docs.oracle.com/javase/7/docs/api/java/lang/Double.html
     */
    public Packet putDouble(double d) {
        buffer.writeDouble(d);
        return this;
    }

    /**
     * https://docs.oracle.com/javase/7/docs/api/java/lang/Float.html
     */
    public Packet putFloat(float f) {
        return putString(Float.toString(f));
    }

    public Packet putBoolean(boolean b) {
        buffer.writeByte((byte) (b ? 1 : 0));
        return this;
    }

    public Packet putString(String s) {
        buffer.writeByte(s.getBytes().length);
        buffer.writeBytes(s.getBytes());
        return this;
    }

    /**
     * @return Reads a Byte value from the buffer.
     */
    public byte getByte() {
        return buffer.readByte();
    }

    /**
     * @return Reads an array of Byte values from the buffer.
     */
    public byte[] getBytes() {
        int length = (int) buffer.readLong();
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            b[i] = buffer.readByte();
        }
        return b;
    }

    /**
     * @return Reads a Short from the buffer.
     */
    public short getShort() {
        return buffer.readShort();
    }

    /**
     * @return Reads an Small Integer (Short) from the buffer.
     */
    public int getSmallInt() {
        return buffer.readShort();
    }

    /**
     * @return Reads an Integer from the buffer.
     */
    public int getInt() {
        return buffer.readInt();
    }

    /**
     * @return Reads a Long from the buffer.
     */
    public long getLong() {
        return buffer.readLong();
    }

    /**
     * Decodes a base37 integral value into a String.
     * 
     * @return The decoded String value.
     */
    public String getBase37() {
        long base = buffer.readLong();
        if (base < 0L) {
            return "invalid_name";
        }
        String string = "";
        while (base != 0L) {
            int value = (int) (base % 37L);
            base /= 37L;
            if (value == 0) {
                string = " " + string;
            } else if (value < 27) {
                if (base % 37L == 0L) {
                    string = (char) ((value + 65) - 1) + string;
                } else {
                    string = (char) ((value + 97) - 1) + string;
                }
            } else {
                string = (char) ((value + 48) - 27) + string;
            }
        }
        return string;
    }

    /**
     * @return Reads a Double from the buffer.
     */
    public double getDouble() {
        return buffer.readDouble();
    }

    /**
     * @return Reads a Float from the buffer.
     */
    public float getFloat() {
        return Float.parseFloat(getString());
    }

    /**
     * @return Reads a Boolean from the buffer.
     */
    public boolean getBoolean() {
        return buffer.readByte() == 1;
    }

    /**
     * @return Reads a String from the buffer.
     */
    public String getString() {
        int length = getByte();
        byte[] b = new byte[length];
        for (int i = 0; i < b.length; i++) {
            b[i] = buffer.readByte();
        }
        return new String(b);
    }

    /**
     * @return The byte array contains all of the packet data.
     */
    public byte[] toByteArray() {
        if (buffer.hasArray()) {
            return Arrays.copyOfRange(buffer.array(), 0, buffer.writerIndex());
        }
        throw new IllegalStateException();
    }

    /**
     * @return The packet length.
     */
    public int getPacketLength() {
        return buffer.readableBytes();
    }

}