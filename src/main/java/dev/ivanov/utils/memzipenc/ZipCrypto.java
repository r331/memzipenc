/**/
package dev.ivanov.utils.memzipenc;


import dev.ivanov.utils.memzipenc.zip.Crc32;

public class ZipCrypto {

    private static long _Keys[] = {0x12345678, 0x23456789, 0x34567890};

    private static short MagicByte() {
        int t;
        t = (int) ((_Keys[2] & 0xFFFF) | 2);
        t = ((t * (t ^ 1)) >> 8);
        return (short) t;
    }

    private static void UpdateKeys(short byteValue) {
        short key0val;
        _Keys[0] = Crc32.update(_Keys[0], byteValue);
        key0val = (byte) _Keys[0];
        if ((byte) _Keys[0] < 0) {
            key0val += 256;
        }
        _Keys[1] = _Keys[1] + key0val;
        _Keys[1] = (_Keys[1] * 0x08088405);
        _Keys[1] = _Keys[1] + 1;
        _Keys[2] = Crc32.update(_Keys[2], (byte) (_Keys[1] >> 24));

    }

    public static void InitCipher(String passphrase) {
        _Keys[0] = 0x12345678;
        _Keys[1] = 0x23456789;
        _Keys[2] = 0x34567890;
        for (int i = 0; i < passphrase.length(); i++) {
            UpdateKeys((byte) passphrase.charAt(i));
        }
    }

    public static byte[] DecryptMessage(byte[] cipherText, int length) {
        byte[] PlainText = new byte[length];
        for (int i = 0; i < length; i++) {
            short m = MagicByte();
            byte C = (byte) (cipherText[i] ^ m);
            if (C < 0) {
                UpdateKeys((short) ((short) C + 256));
                PlainText[i] = (byte) (short) ((short) C + 256);
            } else {
                UpdateKeys(C);
                PlainText[i] = C;
            }
        }
        return PlainText;
    }

    public static byte[] EncryptMessage(byte[] plaintext, int length) {
        byte[] CipherText = new byte[length];
        for (int i = 0; i < length; i++) {
            byte C = plaintext[i];
            CipherText[i] = (byte) (plaintext[i] ^ MagicByte());
            UpdateKeys(C);
        }
        return CipherText;
    }
}
