package ch.epfl.sdp.appart.databus;

import java.util.HashMap;

public class PrivateDataBusTokenFactory {
    public static <T, U> PrivateDataBusToken makeToken(Class<T> first, Class<U> second) {
        int hash = (first.getCanonicalName() + second.getCanonicalName()).hashCode();
        return new PrivateDataBusToken(hash);
    }
}
