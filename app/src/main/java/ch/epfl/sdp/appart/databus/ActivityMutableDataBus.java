package ch.epfl.sdp.appart.databus;

import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Test class to allows for activities or view models to exchange and set their respective data.
 * @param <T>
 */
@Singleton
public class ActivityMutableDataBus<T> {
    MutableLiveData<T> data;
    Class<?> owner;

    @Inject
    public ActivityMutableDataBus() {}

    public void bind(Class<?> newOwner, MutableLiveData<T> data) {

        if (owner != null) {
            throw new IllegalStateException("bus already owned by " + owner.getCanonicalName()
                                                + "but requested by " + newOwner.getCanonicalName());
        }

        this.owner = newOwner;
        this.data = data;
    }

    public void release(Class<?> oldOwner) {
        if (!owner.getCanonicalName().equals(oldOwner.getCanonicalName())) {
            throw new IllegalStateException("bus already owned by " + owner.getCanonicalName()
                    + "but trying to be released by " + oldOwner.getCanonicalName());
        }
        owner = null;
        data = null;
    }

    public void setData(T value) {
        if (this.data == null) {
            throw new IllegalStateException("live data must be bounded.");
        }
        data.setValue(value);
    }

}
