/**/
package dev.ivanov.utils.memzipenc.zip;


import java.util.zip.Deflater;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import dev.ivanov.utils.memzipenc.ZipCrypto;

public class EncryptDeflater extends FilterOutputStream {
    /**
     * Compressor for this stream.
     */
    protected Deflater def;

    /**
     * Output buffer for writing compressed data.
     */
    protected byte[] buf;

    /**
     * Indicates that the stream has been closed.
     */

    private boolean closed = false;

    /**
     * Creates a new output stream with the specified compressor and
     * buffer size.
     *
     * @param out  the output stream
     * @param def  the compressor ("deflater")
     * @param size the output buffer size
     * @throws IllegalArgumentException if size is <= 0
     */
    public EncryptDeflater(OutputStream out, Deflater def, int size) {
        super(out);
        if (out == null || def == null) {
            throw new NullPointerException();
        } else if (size <= 0) {
            throw new IllegalArgumentException("buffer size <= 0");
        }
        this.def = def;
        buf = new byte[size];
    }

    /**
     * Creates a new output stream with the specified compressor and
     * a default buffer size.
     *
     * @param out the output stream
     * @param def the compressor ("deflater")
     */
    public EncryptDeflater(OutputStream out, Deflater def) {
        this(out, def, 512);
    }

    boolean usesDefaultDeflater = false;

    /**
     * Creates a new output stream with a default compressor and buffer size.
     *
     * @param out the output stream
     */
    public EncryptDeflater(OutputStream out) {
        this(out, new Deflater());
        usesDefaultDeflater = true;
    }

    /**
     * Writes a byte to the compressed output stream. This method will
     * block until the byte can be written.
     *
     * @param b the byte to be written
     * @throws IOException if an I/O error has occurred
     */
    @Override
    public void write(int b) throws IOException {
        byte[] buf = new byte[1];
        buf[0] = (byte) (b & 0xff);
        write(buf, 0, 1);
    }

    /**
     * Writes an array of bytes to the compressed output stream. This
     * method will block until all the bytes are written.
     *
     * @param b   the data to be written
     * @param off the start offset of the data
     * @param len the length of the data
     * @throws IOException if an I/O error has occurred
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (def.finished()) {
            throw new IOException("write beyond end of stream");
        }
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        if (!def.finished()) {
            def.setInput(b, off, len);
            while (!def.needsInput()) {
                deflate();
            }
        }
    }

    /**
     * Finishes writing compressed data to the output stream without closing
     * the underlying stream. Use this method when applying multiple filters
     * in succession to the same output stream.
     *
     * @throws IOException if an I/O error has occurred
     */
    public void finish() throws IOException {
        if (!def.finished()) {
            def.finish();
            while (!def.finished()) {
                deflate();
            }
        }
    }

    /**
     * Writes remaining compressed data to the output stream and closes the
     * underlying stream.
     *
     * @throws IOException if an I/O error has occurred
     */
    @Override
    public void close() throws IOException {
        if (!closed) {
            finish();
            if (usesDefaultDeflater) {
                def.end();
            }
            out.close();
            closed = true;
        }
    }

    protected void writeExtData(EncryptZipEntry entry) throws IOException {

        byte[] extData = new byte[12];
        ZipCrypto.InitCipher(password);
        for (int i = 0; i < 11; i++) {
            extData[i] = (byte) Math.round(256);
        }
        extData[11] = (byte) ((entry.time >> 8) & 0xff);
        extData = ZipCrypto.EncryptMessage(extData, 12);
        out.write(extData, 0, extData.length);

    }

    /**
     * Writes next block of compressed data to the output stream.
     *
     * @throws IOException if an I/O error has occurred
     */
    protected void deflate() throws IOException {
        int len = def.deflate(buf, 0, buf.length);
        if (len > 0) {
            if (password != null) {

                byte[] crypto = ZipCrypto.EncryptMessage(buf, len);
                out.write(crypto, 0, len);
                return;
            }
            out.write(buf, 0, len);
        }
    }

    protected String password = null;
}
