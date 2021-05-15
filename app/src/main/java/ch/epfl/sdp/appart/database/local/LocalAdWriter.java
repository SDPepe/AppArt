package ch.epfl.sdp.appart.database.local;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.exceptions.LocalDatabaseException;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.utils.FileIO;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;

/**
 * This class manages everything related to writing an ad on disk
 */
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
     * @param currentUserID           the current user ID
     * @return a boolean that indicates the success of the operation
     */
    static boolean createAdFolder(int futureNumberOfPhotos,
                                  int futureNumberOfPanoramas,
                                  String currentUserID) {
        return FileIO.createFolderOrElse(LocalDatabasePaths.cardFolder(currentUserID),
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
     * @param currentUserID the current user ID
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
     * @param currentUserID the current user ID
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
     * @param currentUserID        the current user ID
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
     * @param currentUserID      the current user ID
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
     * @param currentUserID           the current user ID
     * @return a boolean that indicates if the operation succeeded or not
     */
    private static boolean removeExtraPanoramas(int futureNumberOfPanoramas,
                                                String currentUserID) {
        return removeExtraPictures(getNumberOfPanoramas(currentUserID),
                futureNumberOfPanoramas,
                ImageType.PANORAMA, currentUserID);
    }

    /**
     * This builds the local refs for the pictures of an ad. Since the pictures
     * are now stored on disk, we replace the references to the online
     * database by "real" references to "on disk" files. It handles panoramas
     * and photos.
     *
     * @param size          the number of refs of the ad
     * @param type          the type of pictures we are handling, either
     *                      PHOTOs or
     *                      PANORAMAs
     * @param currentUserID the current user ids
     * @return the new list of local refs
     */
    /**
     * This builds the local refs for the pictures of an ad. Since the pictures
     * are now stored on disk, we replace the references to the online
     * database by "real" references to "on disk" files. It handles panoramas
     * and photos.
     *
     * @param sizePhotos         the number of photos in the ad
     * @param sizePanoramas      the number of panoramas in the ad
     * @param currentUserID      the current user id
     * @param photoReferences    a list that will be filled with the local
     *                           photo references
     * @param panoramaReferences a list that will be filled with the local
     *                           panorama references
     */
    private static void buildLocalReferences(int sizePhotos, int sizePanoramas,
                                             String currentUserID,
                                             List<String> photoReferences,
                                             List<String> panoramaReferences) {
        for (int i = 0; i < sizePhotos; ++i) {
            photoReferences.add(LocalDatabasePaths.photoFile(currentUserID, i));
        }
        for (int i = 0; i < sizePanoramas; ++i) {
            panoramaReferences.add(LocalDatabasePaths.panoramaFile(currentUserID, i));
        }
    }

    /**
     * Constructs a local ad from an "online" ad. The only difference is the
     * references to panoramas and photos. The local ad will have paths to
     * files "on disk" has its references for the pictures.
     *
     * @param ad            the original "online" ad
     * @param currentUserID the current user ID
     * @return a local ad, that is to say, an ad with updated local references.
     */
    static Ad buildLocalAd(Ad ad, String currentUserID) {
        List<String> localPhotoRefs = new ArrayList<>();
        List<String> localPanoramaRefs = new ArrayList<>();
        buildLocalReferences(ad.getPhotosRefs().size(),
                ad.getPanoramaReferences().size(), currentUserID,
                localPhotoRefs, localPanoramaRefs);

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
     * @param adID          the id of the ad
     * @param localAd       the local ad
     * @param userID        the id of the user
     * @param cardID        the id of the card
     * @param currentUserID the current user ID
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
     * performed by the writeImage function. The writing happens asynchronously.
     *
     * @param adPhotos      list of bitmaps that represents the ad photos
     * @param currentUserID the current user ID
     * @param cardID        the card id
     * @return a completable future that indicates if the operation succeeded
     * or not
     */
    static CompletableFuture<Void> writeAdPhotos(List<Bitmap> adPhotos,
                                                 String currentUserID,
                                                 String cardID) {
        return writeImages(adPhotos, ImageType.PHOTO, currentUserID, cardID);
    }

    /**
     * This method performs the writes of images on disk. It can handle
     * photos and panoramas. The actual operation is performed in
     * {@link FileIO} by the saveBitmap method. The writing happens
     * asynchronously.
     *
     * @param bitmaps       the list of bitmaps representing the images
     * @param type          the type of images
     * @param cardID        the card id
     * @param currentUserID the current user ID
     * @return a completable future that indicates if the operation succeeded
     * or not.
     */
    private static CompletableFuture<Void> writeImages(List<Bitmap> bitmaps,
                                                       ImageType type,
                                                       String currentUserID,
                                                       String cardID) {


        return CompletableFuture.runAsync(() -> {
            boolean photoSaveSuccess = true;
            for (int i = 0; i < bitmaps.size(); ++i) {

                String bitmapPath;
                //Strings are immutable so it should be ok to give
                // them like
                // this I guess
                if (type == ImageType.PHOTO) {
                    bitmapPath =
                            LocalDatabasePaths.photoFile(currentUserID,
                                    cardID, i);
                } else {
                    bitmapPath =
                            LocalDatabasePaths.panoramaFile(currentUserID,
                                    cardID, i);
                }
                photoSaveSuccess &= FileIO.saveBitmap(bitmaps.get(i),
                        bitmapPath);
            }
            if (!photoSaveSuccess)
                throw new CompletionException(new LocalDatabaseException(
                        "Could not write all the images !"));
        });
    }

    /**
     * This method writes the ad panoramas on disk. The actual operation is
     * performed by the writeImage function.
     *
     * @param panoramas     list of bitmaps that represents the ad panoramas
     * @param currentUserID the current user ID
     * @param cardID        the card id
     * @return a boolean that indicates if the operation succeeded or not
     */
    static CompletableFuture<Void> writePanoramas(List<Bitmap> panoramas,
                                                  String currentUserID,
                                                  String cardID) {
        return writeImages(panoramas,
                ImageType.PANORAMA, currentUserID, cardID);
    }
}
