package com.nicolasgnyra.stagelightplanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
public class FileStreamReader extends FileInputStream {

    /**
     * FileStreamReader(File) Constructor:
     * Creates a new instance of the FileStreamReader class.
     *
     * Input: File to be read.
     *
     * Process: Calls the superclass' constructor.
     *
     * Output: New FileStreamReader class.
     *
     * @since 4.0
     *
     * @param file Input file.
     * @throws IOException
     */
    public FileStreamReader(File file) throws IOException {

        // call superclass constructor
        super(file);

    }

    /**
     * readByte Method:
     * Reads a single byte from a file and returns it.
     *
     * Input: None.
     *
     * Process: Creates a buffer byte array, reads a byte into the array, and returns the byte.
     *
     * Output: Read byte.
     *
     * @since 3.0
     *
     * @return The read byte
     * @throws IOException Thrown when a reading error occurs
     */
    public byte readByte() throws IOException {

        // create buffer
        byte[] buffer = new byte[1];

        // read byes from file
        int read = read(buffer, 0, buffer.length);

        // check if correct amount of bytes was read
        if(read != buffer.length) throw new IOException("Failed to read 1 byte");

        // return byte
        return buffer[0];

    }

    /**
     * readShort Method:
     * Reads a short (two bytes) from a file and returns it.
     *
     * Input: None.
     *
     * Process: Creates a buffer byte array, reads two bytes into the array, converts the bytes to a short, and returns it.
     *
     * Output: Read short.
     *
     * @since 3.0
     *
     * @return The read short
     * @throws IOException Thrown when a reading error occurs
     */
    public short readShort() throws IOException {

        // create buffer
        byte[] buffer = new byte[2];

        // read byes from file
        int read = read(buffer, 0, buffer.length);

        // check if correct amount of bytes was read
        if(read != buffer.length) throw new IOException("Failed to read 2 bytes");

        // convert bytes to short & return
        return ByteBuffer.wrap(buffer).getShort();

    }

    /**
     * readInt Method:
     * Reads an integer (four bytes) from a file and returns it.
     *
     * Input: None.
     *
     * Process: Creates a buffer byte array, reads four bytes into the array, converts the bytes to an integer, and returns it.
     *
     * Output: Read integer.
     *
     * @since 3.0
     *
     * @return The read integer
     * @throws IOException Thrown when a reading error occurs
     */
    public int readInt() throws IOException {

        // create buffer
        byte[] buffer = new byte[4];

        // read byes from file
        int read = read(buffer, 0, buffer.length);

        // check if correct amount of bytes was read
        if(read != buffer.length) throw new IOException("Failed to read 4 bytes");

        // convert bytes to integer & return
        return ByteBuffer.wrap(buffer).getInt();

    }

    public float readFloat() throws IOException {

        // create buffer
        byte[] buffer = new byte[4];

        // read byes from file
        int read = read(buffer, 0, buffer.length);

        // check if correct amount of bytes was read
        if(read != buffer.length) throw new IOException("Failed to read 4 bytes");

        // convert bytes to float & return
        return ByteBuffer.wrap(buffer).getFloat();

    }

    /**
     * readString Method:
     * Reads a string of the specified length (in bytes) and returns it.
     *
     * Input: String length.
     *
     * Process: Creates a buffer byte array, reads the specified amount of bytes into the array, converts the bytes to a string, and returns it.
     *
     * Output: Read string.
     *
     * @since 3.0
     *
     * @param length Length of the string to read (in bytes)
     * @return The read string
     * @throws IOException Thrown when a reading error occurs
     */
    public String readString(int length) throws IOException {

        // create buffer
        byte[] buffer = new byte[length];

        // read byes from file
        int read = read(buffer, 0, buffer.length);

        // check if correct amount of bytes was read
        if(read != buffer.length) throw new IOException("Failed to read " + length + " bytes");

        // convert bytes to string using UTF-8 encoding & return
        return new String(buffer, StandardCharsets.UTF_8);

    }

}
