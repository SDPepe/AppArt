package ch.epfl.sdp.appart.place;

/**
 * This exception should be raised when an internal error of the PlaceService happens.
 */
public class PlaceServiceException extends RuntimeException {
    public PlaceServiceException(String message) {
        super(message);
    }
}
