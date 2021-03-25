package ch.epfl.sdp.appart.database;

public class DatabaseRequestFailedException extends Exception {
    public DatabaseRequestFailedException(String errorMsg) {
        super(errorMsg);
    }
}
