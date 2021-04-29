package ch.epfl.sdp.appart.panorama;

import android.net.Uri;

/**
 * Container class containing the URI of the picture in the card and
 * its index in the activity_fullscreen_image.xml.
 */
public class PictureCard {

    private final Uri imageUri;
    private int index;

    public PictureCard(Uri imageUri, int index) {
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
