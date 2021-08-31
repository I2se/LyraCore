package fr.lyrania.common.protocol.tcp;

import fr.lyrania.common.protocol.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class TcpPacketEncryptor extends ByteToMessageCodec<ByteBuf> {

    private final Connection connection;
    private byte[] decryptedBytes;
    private byte[] encryptedBytes;

    public TcpPacketEncryptor(Connection connection) {
        this.connection = connection;
        this.decryptedBytes = new byte[0];
        this.encryptedBytes = new byte[0];
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        if (this.connection.getEncryption() != null) {
            int length = in.readableBytes();
            this.readDecryptedBytes(in, length);
            int outLength = this.connection.getEncryption().getEncryptOutputSize(length);
            if (this.encryptedBytes.length < length) {
                this.encryptedBytes = new byte[outLength];
            }

            out.writeBytes(this.encryptedBytes, 0, this.connection.getEncryption().encrypt(
                    this.decryptedBytes,
                    0,
                    length,
                    this.encryptedBytes,
                    0
            ));
        } else {
            out.writeBytes(in);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readableBytes();

        if (this.connection.getEncryption() != null) {
            this.readDecryptedBytes(in, length);
            ByteBuf decryptedBuf = ctx.alloc().heapBuffer(this.connection.getEncryption().getDecryptOutputSize(length));
            decryptedBuf.writerIndex(this.connection.getEncryption().decrypt(
                    this.decryptedBytes,
                    0,
                    length,
                    decryptedBuf.array(),
                    decryptedBuf.arrayOffset()
            ));
            out.add(decryptedBuf);
        } else {
            out.add(in.readBytes(length));
        }
    }

    private void readDecryptedBytes(ByteBuf buf, int length) {
        if (this.decryptedBytes.length < length) {
            this.decryptedBytes = new byte[length];
        }

        buf.readBytes(this.decryptedBytes, 0, length);
    }
}
