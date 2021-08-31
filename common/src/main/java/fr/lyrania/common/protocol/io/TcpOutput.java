package fr.lyrania.common.protocol.io;

import java.io.IOException;
import java.util.UUID;

public interface TcpOutput {

    void writeByte(byte value) throws IOException;

    void writeBoolean(boolean value) throws IOException;

    void writeShort(short value) throws IOException;

    void writeInt(int value) throws IOException;

    void writeLong(long value) throws IOException;

    void writeFloat(float value) throws IOException;

    void writeDouble(double value) throws IOException;

    void writeBytes(byte[] value) throws IOException;

    void writeString(String value) throws IOException;

    void writeUUID(UUID value) throws IOException;

    void flush() throws IOException;
}
