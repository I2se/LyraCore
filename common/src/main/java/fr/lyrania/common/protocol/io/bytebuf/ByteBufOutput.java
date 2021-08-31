package fr.lyrania.common.protocol.io.bytebuf;

import fr.lyrania.common.protocol.io.TcpOutput;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ByteBufOutput implements TcpOutput {

    private final ByteBuf buf;

    public ByteBufOutput(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public void writeByte(byte value) throws IOException {
        this.buf.writeByte(value);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        this.buf.writeBoolean(value);
    }

    @Override
    public void writeShort(short value) throws IOException {
        this.buf.writeShort(value);
    }

    @Override
    public void writeInt(int value) throws IOException {
        this.buf.writeInt(value);
    }

    @Override
    public void writeLong(long value) throws IOException {
        this.buf.writeLong(value);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        this.buf.writeFloat(value);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        this.buf.writeDouble(value);
    }

    @Override
    public void writeBytes(byte[] value) throws IOException {
        this.buf.writeBytes(value);
    }

    @Override
    public void writeString(String value) throws IOException {
        if (value == null) {
            throw new IllegalStateException("String cannot be null!");
        }

        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 0x7FFF) {
            throw new IOException("String provided exceeded the max size of " + 0x7FFF + ", was " + bytes.length + "!");
        } else {
            this.writeInt(bytes.length);
            this.writeBytes(bytes);
        }
    }

    @Override
    public void writeUUID(UUID value) throws IOException {
        if (value == null) {
            throw new IllegalStateException("UUID cannot be null!");
        }

        this.writeLong(value.getMostSignificantBits());
        this.writeLong(value.getLeastSignificantBits());
    }

    @Override
    public void flush() throws IOException {}

    public ByteBuf getBuf() {
        return buf;
    }
}
