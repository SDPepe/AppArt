package ch.epfl.sdp.appart.database.exceptions;

public class DatabaseRequestFailedException extends Exception {
    public DatabaseRequestFailedException(String errorMsg) {
        super(errorMsg);
    }
}
