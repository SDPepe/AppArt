package ch.epfl.sdp.appart.panorama;

import android.net.Uri;

public class PanoramaPictureCard {

    private final Uri imageUri;
    private int index;

    public PanoramaPictureCard(Uri imageUri, int index) {
        this.imageUri = imageUri;
        this.index = index;
    }

    Uri getImageUri() {
        return imageUri;
    }
    int getIndex() {
        return index;
    }
    void setIndex(int index) {
        this.index = index;
    }
}
