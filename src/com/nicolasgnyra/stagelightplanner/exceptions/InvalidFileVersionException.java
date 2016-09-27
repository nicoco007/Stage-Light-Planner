package com.nicolasgnyra.stagelightplanner.exceptions;

/**
 * InvalidFileVersionException Class:
 * Exception thrown when attempting to load a file with a version code that is older than the current version's code.
 *
 * Date: 2016-09-27
 *
 * @author Nicolas Gnyra
 * @version 1.0
 */
public class InvalidFileVersionException extends Exception {
    public InvalidFileVersionException(String message) {
        super(message);
    }
}
