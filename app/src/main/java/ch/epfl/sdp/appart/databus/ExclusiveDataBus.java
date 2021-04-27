package ch.epfl.sdp.appart.databus;

/**
 * Solution 2
 * This class allows for two classes to exchange data by allowing exactly two classes to
 * talk with each other.
 * @param <T> the type of parameters
 */
public class ExclusiveDataBus<T> {

    private T data;
    private Class<?> firstTalker;
    private Class<?> secondTalker;

    /**
     * Set the owner ship of the bus for one of the talker
     * @param newTalker
     */
    public void bind(Class<?> newTalker) {
        if (firstTalker == null) {
            firstTalker = newTalker;
            return;
        }

        if (secondTalker == null) {
            secondTalker = newTalker;
            return;
        }

        throw new IllegalStateException("bus already owned by " + firstTalker.getCanonicalName()
                + " and " + secondTalker.getCanonicalName() + " but requested by " + newTalker.getCanonicalName());
    }

    public void release(Class<?> talker) {

        if (firstTalker.getCanonicalName().equals(talker.getCanonicalName())) {
            firstTalker = null;
            return;
        }

        if (secondTalker.getCanonicalName().equals(talker.getCanonicalName())) {
            secondTalker = null;
            return;
        }

        throw new IllegalStateException("bus owned by " + firstTalker.getCanonicalName()
                + " and " + secondTalker.getCanonicalName() + " but trying to be released by "
                + talker.getCanonicalName());

    }

    public void setData(Class<?> talker, T data) {
        if (!firstTalker.getCanonicalName().equals(talker.getCanonicalName()) &&
                !secondTalker.getCanonicalName().equals(talker.getCanonicalName())) {
            throw new IllegalStateException("bus already owned by " + firstTalker.getCanonicalName()
                    + " and " + secondTalker.getCanonicalName() + " but accessed by " + talker.getCanonicalName());
        }

        if (firstTalker == null || secondTalker == null) {
            throw new IllegalStateException("Two talkers must be registered");
        }

        this.data = data;
    }

    public T getData(Class<?> talker) {
        if (!firstTalker.getCanonicalName().equals(talker.getCanonicalName()) &&
                !secondTalker.getCanonicalName().equals(talker.getCanonicalName())) {
            throw new IllegalStateException("bus already owned by " + firstTalker.getCanonicalName()
                    + " and " + secondTalker.getCanonicalName() + " but read by " + talker.getCanonicalName());
        }

        if (firstTalker == null || secondTalker == null) {
            throw new IllegalStateException("Two talkers must be registered");
        }

        return data;
    }

}
