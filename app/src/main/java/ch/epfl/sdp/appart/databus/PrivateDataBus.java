package ch.epfl.sdp.appart.databus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * This class allows activities to talk with the use of tokens.
 * A token equals a channel and enforce that to see a value you need to have the right token
 * See the token factory to see how it is synthesized.
 * @param <T>
 */
public class PrivateDataBus<T> {

    private HashMap<PrivateDataBusToken, T> entries = new HashMap<>();

    @Inject
    public PrivateDataBus() {}

    /**
     * Set the data contained in the bus.
     * @param data data T to be set.
     */
    public <W, X> void setData(@NonNull PrivateDataBusToken token, @Nullable T data) {
        if (token == null) {
            throw new IllegalArgumentException("token cannot be null");
        }
        entries.put(token, data);
    }

    /**
     * Get the data contained in the bus.
     * @return T data
     */
    public <W, X> T getData(@NonNull PrivateDataBusToken token) {
        if (token == null) {
            throw new IllegalArgumentException("token cannot be null");
        }
        return entries.getOrDefault(token, null);
    }
}
