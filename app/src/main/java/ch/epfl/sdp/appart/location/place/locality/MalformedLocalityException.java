package ch.epfl.sdp.appart.location.place.locality;

/**
 * This exception should be raised when a locality in the locality factory does not
 * fulfills our constraints.
 */
public class MalformedLocalityException extends RuntimeException {
    protected MalformedLocalityException(String message) {
        super(message);
    }
}
