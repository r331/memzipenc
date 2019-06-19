
package dev.ivanov.utils.memzipenc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import dev.ivanov.utils.memzipenc.zip.EncryptZipEntry;
import dev.ivanov.utils.memzipenc.zip.EncryptZipInput;


public class MemZipDec {

    public static byte[] unzipFiles(byte[] zipBytes, String password) throws IOException {
        InputStream bais = new ByteArrayInputStream(zipBytes);
        EncryptZipInput zin = new EncryptZipInput(bais, password);
        EncryptZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            ByteArrayOutputStream toScan = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = zin.read(buf)) > 0) {
                toScan.write(buf, 0, len);
            }
            byte[] fileOut = toScan.toByteArray();
            toScan.close();
            zin.close();
            bais.close();
            return fileOut;
        }
        return null;
    }
}
