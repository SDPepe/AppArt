package ch.epfl.sdp.appart.database.exceptions;

/**
 * This exception is thrown by the local database in case of failures on one
 * of its operations.
 */
public class LocalDatabaseException extends Exception {
    public LocalDatabaseException(String message) {
        super(message);
    }
}
