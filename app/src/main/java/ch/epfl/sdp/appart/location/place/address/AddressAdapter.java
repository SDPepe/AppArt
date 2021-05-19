package ch.epfl.sdp.appart.location.place.address;

public class AddressAdapter {

    private AddressAdapter(){}
    public static Address fromAndroidToAppartAddress(android.location.Address address) {
        String streetName = address.getThoroughfare() + " " + address.getSubThoroughfare();
        String postalCode = address.getPostalCode();
        String locality = address.getLocality();
        return AddressFactory.makeAddressOrElse(streetName, postalCode, locality, () -> { return null; });
    }

}
