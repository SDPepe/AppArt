package ch.epfl.sdp.appart.databus;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Solution 1
 * Class that allows activities to talk to each other with a bus. All classes that
 * utilise this class defined for some type T will utilize the same class.
 * @param <T> The type of the data that will be exchangeable over the bus.
 * This class is only meant to be used via hilt injection and should never be instantiated.
 */
@Singleton
public class DataBus<T> {

    T data;

    @Inject
    public DataBus() {}

    /**
     * Set the data contained in the bus.
     * @param data data T to be set.
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Get the data contained in the bus.
     * @return T data
     */
    public T getData() {
        return data;
    }

}
