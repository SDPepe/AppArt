package ch.epfl.sdp.appart.datapass;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DataTransfer {

    private HashMap<String, DataContainer<?>> map = new HashMap<>();

    @Inject
    public DataTransfer() {}

    public <T, S> void registerContainer(@NotNull Class<T> source, @NotNull Class<S> dataType) {
        DataContainer<S> emptyContainer = new DataContainer<>();
        map.put(source.getCanonicalName(), emptyContainer);
    }

    public <T, S> void registerContainerList(@NotNull Class<T> source, @NotNull Class<S> dataType) {
        DataContainer<List<S>> emptyContainer = new DataContainer<>();
        map.put(source.getCanonicalName(), emptyContainer);
    }

    public <T, S> DataContainer<S> getRegisteredContainer(@NotNull Class<T> source) {

        if (!map.containsKey(source.getCanonicalName())) {
            throw new IllegalStateException("the class must be registered");
        }

        if (map.get(source.getCanonicalName()) == null) {
            throw new IllegalStateException("the registered container cannot be null");
        }

        DataContainer<S> result;
        try {
            result = (DataContainer<S>) map.get(source.getCanonicalName());
        } catch (ClassCastException e) {
            throw new IllegalStateException("failed to cast the generic buffer");
        }

        return result;
    }

}
