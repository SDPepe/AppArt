package ch.epfl.sdp.appart.database.local;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.utils.FileIO;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;

public class LocalAdWriter {

    /**
     * This type is used to tell some methods with which type of image we are
     * working on. It is either a PHOTO or a PANORAMA.
     */
    private enum ImageType {
        PHOTO, PANORAMA
    }

    /**
     * This function creates the folder for the local ad. If it already
     * exists it calls removeExtraPhotos to make sure we do not leave any
     * invalid data in the folder. It does not need to remove anything else
     * since the ad data will be stored in a file that will be overwritten.
     * The actual operation is performed in {@link FileIO} by the
     * createFolder method.
     *
     * @param futureNumberOfPhotos    the number of photos of the newly updated
     *                                ad, its sole purpose is to be used when
     *                                the ad already exists on disk and we want
     *                                to make sure we do not leave any extra
     *                                photos.
     * @param futureNumberOfPanoramas the number of panoramas.
     * @return a boolean that indicates the success of the operation
     */
    static boolean createAdFolder(int futureNumberOfPhotos,
                                  int futureNumberOfPanoramas,
                                  String currentUserID) {
        return FileIO.createFolder(LocalDatabasePaths.cardFolder(currentUserID),
                () -> removeExtraPhotos(futureNumberOfPhotos, currentUserID) &&
                        removeExtraPanoramas(futureNumberOfPanoramas,
                                currentUserID));
    }


    /**
     * This function allows us to retrieve the number of photos of the ad if
     * it already exists in the favorites folder. The ad is specified by the
     * caller which sets the cardId value in the {@link LocalDatabasePaths}
     * class.
     *
     * @return an int that represents the number of photos of the ad
     */
    private static int getNumberOfPhotos(String currentUserID) {
        String dataPath =
                LocalDatabasePaths.cardData(currentUserID);

        Map<String, Object> adMap = FileIO.readMapObject(dataPath);
        if (adMap == null) return 0;
        @SuppressWarnings("unchecked") List<String> localPhotoRefs =
                (List<String>) adMap.get(AdLayout.PHOTO_REFS);
        return Objects.requireNonNull(localPhotoRefs).size();
    }

    /**
     * This function allows us to retrieve the number of panoramas of the ad if
     * it already exists in the favorites folder. The ad is specified
     * by the caller which sets the cardId value in the
     * {@link LocalDatabasePaths} class.
     *
     * @return an int that represents the number of panoramas of the ad
     */
    private static int getNumberOfPanoramas(String currentUserID) {
        String dataPath =
                LocalDatabasePaths.cardData(currentUserID);


        Map<String, Object> adMap = FileIO.readMapObject(dataPath);
        if (adMap == null) return 0;
        //noinspection unchecked
        List<String> localPanoramaRefs =
                (List<String>) adMap.get(AdLayout.PANORAMA_REFS);
        return Objects.requireNonNull(localPanoramaRefs).size();
    }

    /**
     * This function allows to remove the extra photos that might stay on
     * disk if the newly updated ad has less photos than the previous version.
     * The actual operation is performed by removeExtraPictures.
     *
     * @param futureNumberOfPhotos the number of photos the updated version
     *                             of the ad
     * @return a boolean that indicates if the operation succeeded or not
     */
    private static boolean removeExtraPhotos(int futureNumberOfPhotos,
                                             String currentUserID) {
        return removeExtraPictures(getNumberOfPhotos(currentUserID),
                futureNumberOfPhotos,
                ImageType.PHOTO, currentUserID);
    }

    /**
     * This function handles the removing of extra pictures in case the
     * newest version of an ad has less pictures than the previous one. It
     * handles PHOTO and PANORAMA images.
     *
     * @param curNumberOfPics    the number of pictures of the previous
     *                           version of the ad
     * @param futureNumberOfPics the number of pictures of the newest version
     *                           of the ad
     * @param type               either ImageType.PHOTO or ImageType.PANORAMA.
     * @return true if the operation succeeded, false otherwise
     */
    private static boolean removeExtraPictures(int curNumberOfPics,
                                               int futureNumberOfPics,
                                               ImageType type,
                                               String currentUserID) {
        int deletedFiles = 0;
        if (curNumberOfPics > futureNumberOfPics) {
            for (int i = futureNumberOfPics; i < curNumberOfPics; ++i) {
                String photoPath;
                //Maybe move this outside the for loop
                if (type == ImageType.PHOTO) {
                    photoPath =
                            LocalDatabasePaths.photoFile(currentUserID, i);
                } else {
                    photoPath =
                            LocalDatabasePaths.panoramaFile(currentUserID, i);
                }
                File photoToDelete =
                        new File(Objects.requireNonNull(photoPath));
                if (photoToDelete.exists()) {
                    if (photoToDelete.delete()) {
                        deletedFiles++;
                    }
                }
            }
            return deletedFiles == (curNumberOfPics - futureNumberOfPics);
        }
        return true;
    }

    /**
     * This function allows to remove the extra panoramas that might stay on
     * disk if the newly updated ad has less panoramas than the previous
     * version.
     * The actual operation is performed by removeExtraPictures.
     *
     * @param futureNumberOfPanoramas the number of panoramas the updated
     *                                version
     *                                of the ad
     * @return a boolean that indicates if the operation succeeded or not
     */
    private static boolean removeExtraPanoramas(int futureNumberOfPanoramas,
                                                String currentUserID) {
        return removeExtraPictures(getNumberOfPanoramas(currentUserID),
                futureNumberOfPanoramas,
                ImageType.PANORAMA, currentUserID);
    }

    /**
     * This builds the local refs for the photos of an ad. Since the photos
     * are now stored on disk, we replace the references to the online
     * database by "real" references to "on disk" files. It calls
     * buildLocalRefs that does the actual operation.
     *
     * @param size the number of refs of the ad
     * @return the new list of local refs
     */
    private static List<String> buildPhotoRefs(int size, String currentUserID) {
        return buildLocalRefs(size, ImageType.PHOTO, currentUserID);
    }

    /**
     * This builds the local refs for the panoramas of an ad. Since the
     * panoramas
     * are now stored on disk, we replace the references to the online
     * database by "real" references to "on disk" files. It calls
     * buildLocalRefs that does the actual operation.
     *
     * @param size the number of refs of the ad
     * @return the new list of local refs
     */
    private static List<String> buildPanoramaRefs(int size,
                                                  String currentUserID) {
        return buildLocalRefs(size, ImageType.PANORAMA, currentUserID);
    }

    /**
     * This builds the local refs for the pictures of an ad. Since the pictures
     * are now stored on disk, we replace the references to the online
     * database by "real" references to "on disk" files. It handles panoramas
     * and photos.
     *
     * @param size the number of refs of the ad
     * @param type the type of pictures we are handling, either PHOTOs or
     *             PANORAMAs
     * @return the new list of local refs
     */
    private static List<String> buildLocalRefs(int size, ImageType type,
                                               String currentUserID) {
        List<String> localRefs = new ArrayList<>();
        for (int i = 0; i < size; ++i) {

            String localRef;
            if (type == ImageType.PHOTO) {
                localRef =
                        LocalDatabasePaths.photoFile(currentUserID, i);
            } else {
                localRef =
                        LocalDatabasePaths.panoramaFile(currentUserID, i);
            }
            localRefs.add(localRef);
        }
        return localRefs;
    }

    /**
     * Constructs a local ad from an "online" ad. The only difference is the
     * references to panoramas and photos. The local ad will have paths to
     * files "on disk" has its references for the pictures.
     *
     * @param ad the original "online" ad
     * @return a local ad, that is to say, an ad with updated local references.
     */
    static Ad buildLocalAd(Ad ad, String currentUserID) {
        List<String> localPhotoRefs =
                buildPhotoRefs(ad.getPhotosRefs().size(), currentUserID);
        List<String> localPanoramaRefs =
                buildPanoramaRefs(ad.getPhotosRefs().size(), currentUserID);

        return new Ad(ad.getTitle(), ad.getPrice(), ad.getPricePeriod()
                , ad.getStreet(), ad.getCity(), ad.getAdvertiserName(),
                ad.getAdvertiserId(),
                ad.getDescription(), localPhotoRefs,
                localPanoramaRefs, ad.hasVRTour());
    }

    /**
     * This method writes an ad on disk. If the folder doesn't already
     * exists, it makes sure to create one.
     *
     * @param adID    the id of the ad
     * @param localAd the local ad
     * @param userID  the id of the user
     * @param cardID  the id of the card
     * @return a boolean that indicates if the operation succeeded or not
     */
    static boolean writeAd(String adID, Ad localAd, String userID,
                           String cardID, String currentUserID) {
        //Serializing
        Map<String, Object> adMap = AdSerializer.serializeLocal(localAd, adID
                , cardID, userID);

        //Write file on disk
        return FileIO.writeMapObject(LocalDatabasePaths.cardData(currentUserID), adMap);
    }

    /**
     * This method writes the ad photos on disk. The actual operation is
     * performed by the writeImage function.
     *
     * @param adPhotos list of bitmaps that represents the ad photos
     * @return a boolean that indicates if the operation succeeded or not
     */
    static boolean writeAdPhotos(List<Bitmap> adPhotos, String currentUserID) {
        return writeImage(adPhotos, ImageType.PHOTO, currentUserID);
    }

    /**
     * This method performs the writes of images on disk. It can handle
     * photos and panoramas. The actual operation is performed in
     * {@link FileIO} by the saveBitmap method.
     *
     * @param bitmaps the list of bitmaps representing the images
     * @param type    the type of images
     * @return a boolean that indicates if the operation succeeded or not.
     */
    private static boolean writeImage(List<Bitmap> bitmaps,
                                      ImageType type, String currentUserID) {
        boolean photoSaveSuccess = true;
        for (int i = 0; i < bitmaps.size(); ++i) {

            String bitmapPath;
            if (type == ImageType.PHOTO) {
                bitmapPath =
                        LocalDatabasePaths.photoFile(currentUserID, i);
            } else {
                bitmapPath =
                        LocalDatabasePaths.panoramaFile(currentUserID, i);
            }
            photoSaveSuccess &= FileIO.saveBitmap(bitmaps.get(i), bitmapPath);
        }
        return photoSaveSuccess;
    }

    /**
     * This method writes the ad panoramas on disk. The actual operation is
     * performed by the writeImage function.
     *
     * @param panoramas list of bitmaps that represents the ad panoramas
     * @return a boolean that indicates if the operation succeeded or not
     */
    static boolean writePanoramas(List<Bitmap> panoramas,
                                  String currentUserID) {
        return writeImage(panoramas,
                ImageType.PANORAMA, currentUserID);
    }
}
