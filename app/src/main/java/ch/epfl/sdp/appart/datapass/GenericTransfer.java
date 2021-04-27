package ch.epfl.sdp.appart.datapass;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GenericTransfer<T> {

    private HashMap<String, DataContainer<T>> map = new HashMap<>();

    @Inject
    public GenericTransfer() {}

    public <S> void registerContainer(@NotNull Class<S> source) {
        DataContainer<T> emptyContainer = new DataContainer<>();
        map.put(source.getCanonicalName(), emptyContainer);
    }

    public <S> DataContainer<T> getRegisteredContainer(@NotNull Class<S> source) {

        if (!map.containsKey(source.getCanonicalName())) {
            throw new IllegalStateException("the class must be registered");
        }

        if (map.get(source.getCanonicalName()) == null) {
            throw new IllegalStateException("the registered container cannot be null");
        }

        DataContainer<T> result = map.get(source.getCanonicalName());
        return result;
    }

}
