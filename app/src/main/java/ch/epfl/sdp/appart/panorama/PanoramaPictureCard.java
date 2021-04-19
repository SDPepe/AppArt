package ch.epfl.sdp.appart.panorama;

import android.net.Uri;

public class PanoramaPictureCard {

    private final Uri imageUri;

    public PanoramaPictureCard(Uri imageUri) {
        this.imageUri = imageUri;
    }

    Uri getImageUri() {
        return imageUri;
    }
}
