
package dev.ivanov.utils.memzipenc;


import dev.ivanov.utils.memzipenc.zip.EncryptZipEntry;
import dev.ivanov.utils.memzipenc.zip.EncryptZipOutput;

import java.io.*;
import java.util.zip.Deflater;

public class MemZipEnc {

    public static int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;

    public static byte[] getEncryptZipByte(final File[] srcfile, final String password) {
        final ByteArrayOutputStream tempOStream = new ByteArrayOutputStream(1024);
        byte[] tempBytes = null;
        final byte[] buf = new byte[1024];
        try {
            final EncryptZipOutput out = new EncryptZipOutput(tempOStream, password, COMPRESSION_LEVEL);
            for (File file : srcfile) {
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new EncryptZipEntry(file.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            tempOStream.flush();
            out.close();
            tempBytes = tempOStream.toByteArray();
            tempOStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempBytes;
    }

    public static byte[] getEncryptZipByte(final byte[] srcfile, final String password, final String fileName) {
        final ByteArrayOutputStream tempOStream = new ByteArrayOutputStream(1024);
        byte[] tempBytes = null;
        byte[] buf = new byte[1024];
        try {
            final EncryptZipOutput out = new EncryptZipOutput(tempOStream, password, COMPRESSION_LEVEL);

            final ByteArrayInputStream in = new ByteArrayInputStream(srcfile);
            out.putNextEntry(new EncryptZipEntry(fileName));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
            tempOStream.flush();
            out.close();
            tempBytes = tempOStream.toByteArray();
            tempOStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempBytes;
    }
}
