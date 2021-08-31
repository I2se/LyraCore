package fr.lyrania.common.protocol.encryption;

import javax.crypto.ShortBufferException;

public interface ProtocolEncryption {

    int getDecryptOutputSize(int length);

    int getEncryptOutputSize(int length);

    int decrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws ShortBufferException;

    int encrypt(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws ShortBufferException;
}
