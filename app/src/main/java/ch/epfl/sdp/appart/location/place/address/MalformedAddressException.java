package ch.epfl.sdp.appart.location.place.address;

/**
 * This exception is used in the AddressFactory and should be raised
 * when the address format does not fulfills our constraints.
 */
public class MalformedAddressException extends RuntimeException {
    protected MalformedAddressException(String message) {
        super(message);
    }
}
