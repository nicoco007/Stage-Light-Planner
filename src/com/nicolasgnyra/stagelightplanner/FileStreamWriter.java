package com.nicolasgnyra.stagelightplanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * FileStreamWriter Class:
 * Helper methods for the FileOutputStream class.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
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

    /**
     * writeShort(short) Method:
     * Writes a short (2 bytes) to the file.
     *
     * Input: Short to write.
     *
     * Process: Creates a byte buffer, puts the short, converts it to a byte array, and writes it.
     *
     * Output: Written short.
     *
     * @param s Short to write.
     * @throws IOException Thrown when an error occurs while writing.
     */
    public void writeShort(short s) throws IOException {
        write(ByteBuffer.allocate(2).putShort(s).array());
    }

    /**
     * writeInteger(int) Method:
     * Writes a integer (4 bytes) to the file.
     *
     * Input: Integer to write.
     *
     * Process: Creates a byte buffer, puts the integer, converts it to a byte array, and writes it.
     *
     * Output: Written int.
     *
     * @param i Integer to write.
     * @throws IOException Thrown when an error occurs while writing.
     */
    public void writeInt(int i) throws IOException {
        write(ByteBuffer.allocate(4).putInt(i).array());
    }

    /**
     * writeFloat(float) Method:
     * Writes a float (4 bytes) to the file.
     *
     * Input: Float to write.
     *
     * Process: Creates a byte buffer, puts the float, converts it to a byte array, and writes it.
     *
     * Output: Written float.
     *
     * @param f Float to write.
     * @throws IOException Thrown when an error occurs while writing.
     */
    public void writeFloat(float f) throws IOException {
        write(ByteBuffer.allocate(4).putFloat(f).array());
    }

    /**
     * writeString(String) Method:
     * Writes a string to the file with the default UTF-8 charset.
     *
     * Input: String to write.
     *
     * Process: Writes the string.
     *
     * Output: Written string.
     *
     * @param str String to write.
     * @throws IOException Thrown when an error occurs while writing.
     */
    public void writeString(String str) throws IOException {
        writeString(str, StandardCharsets.UTF_8);
    }

    /**
     * writeString(String, Charset) Method:
     * Writes a string to the file with the specified charset.
     *
     * Input: String to write, charset to use.
     *
     * Process: Writes the string with the specified charset.
     *
     * Output: Written string.
     *
     * @param str String to write.
     * @param charset Charset to use when writing.
     * @throws IOException Thrown when an error occurs while writing.
     */
    public void writeString(String str, Charset charset) throws IOException {
        write(str.getBytes(charset));
    }

}
