package fr.lyrania.common.protocol.tcp;

import fr.lyrania.common.protocol.io.TcpInput;
import fr.lyrania.common.protocol.io.TcpOutput;
import fr.lyrania.common.protocol.io.bytebuf.ByteBufInput;
import fr.lyrania.common.protocol.io.bytebuf.ByteBufOutput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;

import java.io.IOException;
import java.util.List;

public class TcpPacketSizer extends ByteToMessageCodec<ByteBuf> {

    private static final int SIZE = 5;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, ByteBuf out) throws Exception {
        int length = in.readableBytes();
        out.ensureWritable(this.getLengthSize(length) + length);
        this.writeLength(new ByteBufOutput(out), length);
        out.writeBytes(in);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        byte[] lengthBytes = new byte[SIZE];

        for (int i = 0; i < lengthBytes.length; i++) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            lengthBytes[i] = in.readByte();

            if (lengthBytes[i] > 0 || i == SIZE - 1) {
                int length = this.readLength(new ByteBufInput(Unpooled.wrappedBuffer(lengthBytes)));
                if (in.readableBytes() < length) {
                    in.resetReaderIndex();
                    return;
                }

                out.add(in.readBytes(length));
                return;
            }
        }

        throw new CorruptedFrameException("Length is too long.");
    }

    private int getLengthSize(int length) {
        if ((length & 0xFFFFFF80) == 0) {
            return 1;
        } else if ((length & 0xFFFFC000) == 0) {
            return 2;
        } else if ((length & 0xFFE00000) == 0) {
            return 3;
        } else if ((length & 0xF0000000) == 0) {
            return 4;
        } else {
            return 5;
        }
    }

    private void writeLength(TcpOutput out, int length) throws IOException {
        out.writeInt(length);
    }

    private int readLength(TcpInput in) throws IOException {
        return in.readInt();
    }
}
