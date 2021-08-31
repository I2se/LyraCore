package fr.lyrania.common.protocol.io.bytebuf;

import fr.lyrania.common.protocol.io.TcpInput;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ByteBufInput implements TcpInput {

    private final ByteBuf buf;

    public ByteBufInput(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public byte readByte() throws IOException {
        return this.buf.readByte();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.buf.readBoolean();
    }

    @Override
    public short readShort() throws IOException {
        return this.buf.readShort();
    }

    @Override
    public int readInt() throws IOException {
        return this.buf.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.buf.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return this.buf.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return this.buf.readDouble();
    }

    @Override
    public byte[] readBytes(int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("The length of an array can't be less than 0!");
        }

        byte[] bytes = new byte[length];
        this.buf.readBytes(bytes);
        return bytes;
    }

    @Override
    public String readString() throws IOException {
        return new String(
                this.readBytes(this.readInt()),
                StandardCharsets.UTF_8
        );
    }

    @Override
    public UUID readUUID() throws IOException {
        return new UUID(this.readLong(), this.readLong());
    }

    @Override
    public int available() throws IOException {
        return this.buf.readableBytes();
    }
}
