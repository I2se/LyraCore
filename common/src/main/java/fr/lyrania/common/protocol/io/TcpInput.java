package fr.lyrania.common.protocol.io;

import java.io.IOException;
import java.util.UUID;

public interface TcpInput {

    byte readByte() throws IOException;

    boolean readBoolean() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    byte[] readBytes(int length) throws IOException;

    String readString() throws IOException;

    UUID readUUID() throws IOException;

    int available() throws IOException;
}
