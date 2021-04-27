package ch.epfl.sdp.appart.databus;

public class PrivateDataBusToken {
    private final int token;
    public PrivateDataBusToken(int token) { this.token = token; }
    protected int getToken() { return token; }

    @Override
    public int hashCode() {
        return token;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (getClass() != other.getClass()) {
            return false;
        } else {
            PrivateDataBusToken t = (PrivateDataBusToken) other;
            return t.getToken() == getToken();
        }
    }
}
