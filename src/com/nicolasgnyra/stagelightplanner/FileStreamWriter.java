package com.nicolasgnyra.stagelightplanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * FileStreamReader Class
 * Helper methods for the FileInputStream class.
 *
 * Date: 2016-06-13
 *
 * @author Nicolas Gnyra
 * @version 4.0
 */
public class FileStreamWriter extends FileOutputStream {

    /**
     * FileStreamWriter(File) Constructor:
     * Creates a new instance of the FileStreamWriter class.
     *
     * Input: File to be written.
     *
     * Process: Calls the superclass' constructor.
     *
     * Output: New FileStreamWriter class.
     *
     * @param file Output file.
     * @throws IOException Input/Output error
     */
    public FileStreamWriter(File file) throws IOException {

        // call superclass constructor
        super(file);

    }

    public void writeShort(short s) throws IOException {
        write(ByteBuffer.allocate(2).putShort(s).array());
    }

    public void writeInt(int i) throws IOException {
        write(ByteBuffer.allocate(4).putInt(i).array());
    }

    public void writeFloat(float f) throws IOException {
        write(ByteBuffer.allocate(4).putFloat(f).array());
    }

    public void writeString(String str) throws IOException {
        writeString(str, StandardCharsets.UTF_8);
    }

    public void writeString(String str, Charset charset) throws IOException {
        write(str.getBytes(charset));
    }

}
