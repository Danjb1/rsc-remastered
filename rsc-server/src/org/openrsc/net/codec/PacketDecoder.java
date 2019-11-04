package org.openrsc.net.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.openrsc.net.packet.Packet;

/**
 * Reads an incoming packet.
 */
public class PacketDecoder extends FrameDecoder {

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        // Make sure the length field was received.
        if (buffer.readableBytes() < 4) {
            // The length field was not received yet - return null.
            // This method will be invoked again when more packets are
            // received and appended to the buffer.
            return null;
        }

        // Mark the current buffer position before reading the length field
        // because the whole frame might not be in the buffer yet.
        // We will reset the buffer position to the marked position if
        // there's not enough bytes in the buffer.
        buffer.markReaderIndex();

        // Read the length field.
        final int length = buffer.readInt();

        // Make sure if there's enough bytes in the buffer.
        if (buffer.readableBytes() < length + 4) {
            // The whole bytes were not received yet - return null.
            // This method will be invoked again when more packets are
            // received and appended to the buffer.

            // Reset to the marked position to read the length field again
            // next time.
            buffer.resetReaderIndex();
            return null;
        }

        // Read the opcode.
        final int opcode = buffer.readInt() & 0xff;

        // Create an executable packet using the buffer data.
        return new Packet(opcode, buffer, length);
    }

}