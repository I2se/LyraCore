package fr.lyrania.common.protocol.sender;

import fr.lyrania.common.protocol.io.TcpOutput;

import java.io.IOException;

public interface PacketSender {

    void writeData(TcpOutput out) throws IOException;
}
