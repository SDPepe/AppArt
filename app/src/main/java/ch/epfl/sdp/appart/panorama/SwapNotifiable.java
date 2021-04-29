package ch.epfl.sdp.appart.panorama;

/**
 * Interface to include only necessary behavior in the CardViewHolder of the
 * Picture import activity. Should be used only with the PictureCardAdapter.
 */
public interface SwapNotifiable {
    /**
     * Notify the PictureCardAdapter to swap the element at index
     * with the element above in the activity_fullscreen_image.xml.
     * @param index
     */
    void swapFromIndexWithAbove(int index);

    /**
     * Notify the PictureCardAdapter to swap the element at index
     * with the element bellow in the activity_fullscreen_image.xml.
     * @param index
     */
    void swapFromIndexWithBellow(int index);
}
