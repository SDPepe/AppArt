package ch.epfl.sdp.appart.location.address;

import ch.epfl.sdp.appart.location.Location;

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
        formattedAddress  = sb.append(street)
                .append(", ")
                .append(postalCode)
                .append(" ")
                .append(locality).toString();
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

}
