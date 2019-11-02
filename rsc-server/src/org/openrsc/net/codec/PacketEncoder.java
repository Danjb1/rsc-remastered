package org.openrsc.net.codec;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.openrsc.net.packet.Packet;

/**
 * Writes an outgoing packet.
 */
public class PacketEncoder extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object obj) throws Exception {
		// Get the packet object.
		Packet packet = (Packet) obj;
		
		 // Create a new buffer.
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		
		// Write the packet length.
		buffer.writeInt(packet.getPacketLength());
		
		// Write the packet opcode.
		buffer.writeInt(packet.getOpcode() & 0xff);
		
		// Write the packet data
		buffer.writeBytes(packet.toByteArray());
		
		// The buffer.
		return buffer;
	}

}