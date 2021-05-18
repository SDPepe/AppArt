package ch.epfl.sdp.appart.location.address;

import java.util.Objects;

import ch.epfl.sdp.appart.location.Location;

/**
 * Represent an address. This class is only a container and does not contains any logic.
 * Checking format is deferred to the Factory.
 */
public class Address {

    private final String formattedAddress;
    private final String street;
    private final String locality;
    private final String postalCode;

    protected Address(String street, String postalCode, String locality) {
        this.street = street;
        this.locality = locality;
        this.postalCode = postalCode;

        StringBuilder sb = new StringBuilder();
        formattedAddress  = sb.append(this.street)
                .append(", ")
                .append(this.postalCode)
                .append(" ")
                .append(this.locality).toString();
    }

    protected Address(String street, String locality) {
        this.street = street;
        this.locality = locality;
        this.postalCode = null;

        StringBuilder sb = new StringBuilder();
        formattedAddress  = sb.append(street)
                .append(", ")
                .append(locality).toString();
    }

    public String getAddress() {
        return formattedAddress;
    }

    public String getLocality() {
        return locality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return formattedAddress.equals(address.formattedAddress) &&
                street.equals(address.street) &&
                locality.equals(address.locality) &&
                Objects.equals(postalCode, address.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formattedAddress, street, locality, postalCode);
    }
}
