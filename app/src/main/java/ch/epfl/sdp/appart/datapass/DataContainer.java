package ch.epfl.sdp.appart.datapass;

public class DataContainer<T> {
    private T data;
    private boolean isDirty = true;
    public void setData(T data) {
        this.data = data;
        isDirty = false;
    }
    public T getData() {
        if (isDirty) {
            throw new IllegalStateException("container is in dirty state");
        }
        isDirty = true;
        return data;
    }
    public boolean isDirty() {
        return isDirty;
    }
}
