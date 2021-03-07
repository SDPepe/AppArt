package ch.epfl.sdp.appart.scroll;

public class ApartmentCard implements Card {

    private int id;

    public ApartmentCard(int id) {
        this.id = id;
    }

    @Override
    public int getResourceId() {
        return id;
    }

}
