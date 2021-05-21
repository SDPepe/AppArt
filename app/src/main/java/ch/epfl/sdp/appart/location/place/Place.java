package ch.epfl.sdp.appart.location.place;

/**
 * This interface is used to represent any location. Its good.
 */
public interface Place {

    /**
     * This method returns the "description" or "name" of the location, for
     * an Address it is the address, for locality it is the city, ...
     *
     * @return the "name" or "description" of the place
     */
    String getName();
}
