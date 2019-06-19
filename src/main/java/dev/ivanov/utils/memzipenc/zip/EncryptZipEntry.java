/**/
package dev.ivanov.utils.memzipenc.zip;

import java.util.Date;
import java.util.zip.ZipEntry;

public class EncryptZipEntry extends ZipEntry {

    public EncryptZipEntry(String name) {
        super(name);
        this.name = name;
    }

    String name; // entry name
    long time = -1; // modification time (in DOS time)
    long crc = -1; // crc-32 of entry data
    long size = -1; // uncompressed size of entry data
    long csize = -1; // compressed size of entry data
    int method = -1; // compression method
    byte[] extra; // optional extra field data for entry
    String comment; // optional comment string for entry
    // The following flags are used only by Zip{Input,Output}Stream
    int flag; // bit flags
    int version; // version needed to extract
    long offset; // offset of loc header

    public void setTime(long time) {
        this.time = javaToDosTime(time);
    }

    /*
     * Converts Java time to DOS time.
     */
    @SuppressWarnings("deprecation")
    private static long javaToDosTime(long time) {
        Date d = new Date(time);
        int year = d.getYear() + 1900;
        if (year < 1980) {
            return (1 << 21) | (1 << 16);
        }
        return (year - 1980) << 25 | (d.getMonth() + 1) << 21
               | d.getDate() << 16 | d.getHours() << 11 | d.getMinutes() << 5
               | d.getSeconds() >> 1;
    }
}
