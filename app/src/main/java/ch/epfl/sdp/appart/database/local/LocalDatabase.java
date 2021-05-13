package ch.epfl.sdp.appart.database.local;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.exceptions.LocalDatabaseException;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.FileIO;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

// @formatter:off
/**
 * This class represents the local database. It will perform the storing of
 * data "on disk", and the reading of data "from disk". It is unaware of the
 * android context except for the {@link Bitmap} type and the appFolder it
 * receives.
 * This appFolder is supposed to be Context.getFilesDir(). A current user
 * must be stored in order for the database to work. Either it is already on
 * disk and we can read it, or the caller must set it. One thing that is not
 * handled for now is the privacy of the users, maybe we don't want to store
 * everything "on disk".
 *
 * Here is the file structure of the favorites folder :
 * favorites
 *          /currentUser
 *          profile_picture.jpeg
 *          currentUser.data
 *                      users/
 *                      users/
 *                                  {$user_id}/
 *                                              profile_picture.jpeg
 *                                              user.data
 *                      ${card_id}/
 *                                  data.fav
 *                                  Photo${number}.jpeg
 *                                  Panorama${number}.jpeg
 */
// @formatter:on

@SuppressWarnings("unchecked")
public class LocalDatabase {

    /*
        Keys for the different maps.
     */
    private static final String ID = "ID";
    private static final String PHOTO_REFS = "PHOTO_REFS";
    private static final String CARD_ID = "CARD_ID";
    private static final String USER_ID = "USER_ID";
    private static final String PANORAMA_REFS = "PANORAMA_REFS";

    /*
        The different data structures
     */
    private final List<Card> cards;
    private final Map<String, Ad> idsToAd;
    private final Map<String, User> idsToUser;
    private final Set<String> userIds;
    private final Map<String, List<String>> adIdsToPanoramas;

    private User currentUser = null;

    private boolean firstLoad;

    /**
     * Builds a {@link LocalDatabase}
     *
     * @param appPath the path to the app folder on the phone.
     */
    public LocalDatabase(String appPath) {
        if (appPath == null) throw new IllegalArgumentException();
        this.cards = new ArrayList<>();
        this.idsToAd = new HashMap<>();
        this.idsToUser = new HashMap<>();
        this.firstLoad = false;
        this.userIds = new HashSet<>();
        this.adIdsToPanoramas = new HashMap<>();

        //We set the app path for the LocalDatabasePaths class.
        LocalDatabasePaths.appPath = appPath;
    }

    /**
     * This method retrieves the current user. It will first try to retrieve
     * the current user memory if it there. If it is not it will try to load
     * the current user from disk. If yet again, the current user data is not
     * there, it throws {@link IllegalStateException}
     *
     * @return the current user if it manages to find one, throws
     * {@link IllegalStateException} otherwise.
     */
    public User getCurrentUser() {
        User currentUser;
        if (this.currentUser == null) {
            currentUser = loadCurrentUser();
        } else {
            currentUser = this.currentUser;
        }
        if (currentUser == null) {
            throw new IllegalStateException("The current user is not stored " +
                    "on disk and it was not set !");
        }
        return currentUser;
    }

    /**
     * This function allows us to retrieve the number of photos of the ad if
     * it already exists in the favorites folder. The ad is specified by the
     * caller which sets the cardId value in the {@link LocalDatabasePaths}
     * class.
     *
     * @return an int that represents the number of photos of the ad
     */
    private int getNumberOfPhotos() {
        String dataPath =
                LocalDatabasePaths.cardData(getCurrentUser().getUserId());

        Map<String, Object> adMap = FileIO.readMapObject(dataPath);
        if (adMap == null) return 0;
        @SuppressWarnings("unchecked") List<String> localPhotoRefs =
                (List<String>) adMap.get(PHOTO_REFS);
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
    private int getNumberOfPanoramas() {
        String dataPath =
                LocalDatabasePaths.cardData(getCurrentUser().getUserId());


        Map<String, Object> adMap = FileIO.readMapObject(dataPath);
        if (adMap == null) return 0;
        //noinspection unchecked
        List<String> localPanoramaRefs =
                (List<String>) adMap.get(PANORAMA_REFS);
        return Objects.requireNonNull(localPanoramaRefs).size();
    }

    /**
     * This type is used to tell some methods with which type of image we are
     * working on. It is either a PHOTO or a PANORAMA.
     */
    private enum ImageType {
        PHOTO, PANORAMA
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
    private boolean removeExtraPhotos(int futureNumberOfPhotos) {
        return removeExtraPictures(getNumberOfPhotos(), futureNumberOfPhotos,
                ImageType.PHOTO);
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
    private boolean removeExtraPictures(int curNumberOfPics,
                                        int futureNumberOfPics,
                                        ImageType type) {
        int deletedFiles = 0;
        if (curNumberOfPics > futureNumberOfPics) {
            for (int i = futureNumberOfPics; i < curNumberOfPics; ++i) {
                String photoPath;
                //Maybe move this outside the for loop
                if (type == ImageType.PHOTO) {
                    photoPath =
                            LocalDatabasePaths.photoFile(getCurrentUser().getUserId(), i);
                } else {
                    photoPath =
                            LocalDatabasePaths.panoramaFile(getCurrentUser().getUserId(), i);
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
    private boolean removeExtraPanoramas(int futureNumberOfPanoramas) {
        return removeExtraPictures(getNumberOfPanoramas(),
                futureNumberOfPanoramas,
                ImageType.PANORAMA);
    }

    //TODO: Check if in some cases, the user does not have any profile pic

    //TODO: Be very careful about what you store about the user on the local
    // storage. Store only what the user wants to be visible. Like name, mail
    // and phone number. Also, the user might want to display only its name
    // and not the phone number.

    //TODO: I don't think there are cases where we want to hide ad data. All
    // ad data is public and nothing can come from recovering the id.
    //TODO: Maybe some ad posters don't want to display the address to
    // anybody that is not a student for example.


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
    private boolean createAdFolder(int futureNumberOfPhotos,
                                   int futureNumberOfPanoramas) {
        return FileIO.createFolder(LocalDatabasePaths.cardFolder(getCurrentUser().getUserId()), () -> removeExtraPhotos(futureNumberOfPhotos) && removeExtraPanoramas(futureNumberOfPanoramas));
    }

    /**
     * This method creates a folder for a specific user.
     *
     * @return a boolean that indicates the success of the operation
     */
    private boolean createUserFolder() {

        String userPath =
                LocalDatabasePaths.userFolder(getCurrentUser().getUserId());
        return FileIO.createFolder(userPath, () -> {
            String profilePicPath =
                    LocalDatabasePaths.userProfilePic(getCurrentUser().getUserId());
            File profilePic = new File(profilePicPath);
            profilePic.delete();
            return true;
        });
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
    private List<String> buildPhotoRefs(int size) {
        return buildLocalRefs(size, ImageType.PHOTO);
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
    private List<String> buildPanoramaRefs(int size) {
        return buildLocalRefs(size, ImageType.PANORAMA);
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
    private List<String> buildLocalRefs(int size, ImageType type) {
        List<String> localRefs = new ArrayList<>();
        for (int i = 0; i < size; ++i) {

            String localRef;
            if (type == ImageType.PHOTO) {
                localRef =
                        LocalDatabasePaths.photoFile(getCurrentUser().getUserId(), i);
            } else {
                localRef =
                        LocalDatabasePaths.panoramaFile(getCurrentUser().getUserId(), i);
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
    private Ad buildLocalAd(Ad ad) {
        List<String> localPhotoRefs = buildPhotoRefs(ad.getPhotosRefs().size());
        List<String> localPanoramaRefs =
                buildPanoramaRefs(ad.getPhotosRefs().size());

        return new Ad(ad.getTitle(), ad.getPrice(), ad.getPricePeriod()
                , ad.getStreet(), ad.getCity(), ad.getAdvertiserName(),
                ad.getAdvertiserId(),
                ad.getDescription(), localPhotoRefs,
                localPanoramaRefs, ad.hasVRTour());
    }

    /**
     * Constructs a local user from an online user. Like the local ad, the
     * only difference with the online user is the reference to the profile
     * picture. The local user refers to a file on disk rather than a
     * reference to online storage.
     *
     * @param user the original "online" user
     * @return the new local user
     */
    private User buildLocalUser(User user) {
        User localUser = new AppUser(user.getUserId(), user.getUserEmail());
        if (user.getPhoneNumber() != null) {
            localUser.setPhoneNumber(user.getPhoneNumber());
        }
        if (user.getName() != null) {
            localUser.setName(user.getName());
        }

        localUser.setAge(user.getAge());

        if (user.getGender() != null) {
            localUser.setGender(user.getGender());
        }
        localUser.setProfileImagePathAndName(LocalDatabasePaths.userProfilePic(getCurrentUser().getUserId()));

        return localUser;
    }

    /**
     * This method updates the data structures stored in memory. This is used
     * to synchronize the disk and the memory and to make sure nothing is
     * stale in either of the storage spaces.
     *
     * @param adID      the ad id of the ad
     * @param localAd   the local ad obtained form the original ad
     * @param localCard the local card built from the local ad
     * @param localUser the local user obtained from the original user
     */
    private void syncWithMemory(String adID, Ad localAd, Card localCard,
                                User localUser) {
        if (this.firstLoad) {
            if (!this.idsToAd.containsKey(adID)) {
                this.idsToAd.put(adID, localAd);
                this.adIdsToPanoramas.put(adID,
                        localAd.getPanoramaReferences());
            } else {
                this.idsToAd.replace(adID, localAd);
                this.adIdsToPanoramas.replace(adID,
                        localAd.getPanoramaReferences());
            }

            this.cards.remove(localCard);
            this.cards.add(localCard);
            if (!this.userIds.contains(localUser.getUserId())) {
                this.idsToUser.put(localUser.getUserId(), localUser);
            }
        }
    }

    /**
     * This method writes a user on disk. If the folder isn't created it
     * makes sure to create one.
     *
     * @param localUser the local user
     * @return a boolean that indicates if the operation succeeded or not
     */
    private boolean writeUser(User localUser) {
        if (!createUserFolder())
            return false;
        Map<String, Object> userMap = UserSerializer.serialize(localUser);
        if (userMap == null) return false;
        //We ad the id because it is not serialized
        userMap.put(ID, localUser.getUserId());
        return FileIO.writeMapObject(LocalDatabasePaths.userData(getCurrentUser().getUserId()), userMap);
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
    private boolean writeAd(String adID, Ad localAd, String userID,
                            String cardID) {
        //Serializing
        Map<String, Object> adMap = AdSerializer.serialize(localAd);

        //All these information are manually added because they are not
        // serialized by the ad serializer.
        adMap.put(ID, adID);
        adMap.put(PHOTO_REFS, localAd.getPhotosRefs());
        adMap.put(CARD_ID, cardID);
        adMap.put(USER_ID, userID);
        adMap.put(PANORAMA_REFS, localAd.getPanoramaReferences());

        //Write file on disk
        return FileIO.writeMapObject(LocalDatabasePaths.cardData(getCurrentUser().getUserId()), adMap);
    }

    /**
     * This method writes the ad photos on disk. The actual operation is
     * performed by the writeImage function.
     *
     * @param adPhotos list of bitmaps that represents the ad photos
     * @return a boolean that indicates if the operation succeeded or not
     */
    private boolean writeAdPhotos(List<Bitmap> adPhotos) {
        return writeImage(adPhotos, ImageType.PHOTO);
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
    private boolean writeImage(List<Bitmap> bitmaps,
                               ImageType type) {
        boolean photoSaveSuccess = true;
        for (int i = 0; i < bitmaps.size(); ++i) {

            String bitmapPath;
            if (type == ImageType.PHOTO) {
                bitmapPath =
                        LocalDatabasePaths.photoFile(getCurrentUser().getUserId(), i);
            } else {
                bitmapPath =
                        LocalDatabasePaths.panoramaFile(getCurrentUser().getUserId(), i);
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
    private boolean writePanoramas(List<Bitmap> panoramas) {
        return writeImage(panoramas,
                ImageType.PANORAMA);
    }

    /**
     * This method writes the profile picture of a user on disk. Actually,
     * the write on disk is performed by loadProfilePic. The correct path is
     * given to this function. We opted to do this, this way because we do
     * not want the local database to be aware if Firebase.
     *
     * @param loadProfilePic the function that performs the write on disk of
     *                       the profile picture of the user
     * @param futureSuccess  a completable future that indicates if the
     *                       operation succeeded or not.
     */
    private void writeProfilePic(Function<String,
            CompletableFuture<Void>> loadProfilePic,
                                 CompletableFuture<Void> futureSuccess) {

        CompletableFuture<Void> futureProfilePic =
                loadProfilePic.apply(LocalDatabasePaths.userProfilePic(getCurrentUser().getUserId()));
        futureProfilePic.thenAccept(arg -> futureSuccess.complete(null));
        futureProfilePic.exceptionally(e -> {
            futureSuccess.completeExceptionally(e);
            return null;
        });

        futureSuccess.complete(null);
    }

    /**
     * This function performs the writing of a complete ad into local storage
     * . It will create a folder for the added user only if it doesn't
     * already exists (in which case it is updated). It will do the same for
     * the ad. It also performs the writing of all the images. The photos of
     * ad, the panoramas of the ad and the profile picture of the user (if he
     * doesn't uses the default one).
     *
     * @param adId           the id of the ad
     * @param cardId         the id of the card
     * @param ad             the ad
     * @param user           the user who posted the ad
     * @param adPhotos       list of bitmaps representing the photos of the ad
     * @param panoramas      list of panoramas representing the panoramas of
     *                       the ad
     * @param loadProfilePic the function that will perform the writing on
     *                       disk of the user's profile picture. It receives
     *                       the path representing the location of the
     *                       profile picture and returns a completable future
     *                       to indicate if the operation succeeded or not.
     * @return a completable future that indicates if the operation succeeded
     * or not
     */
    public CompletableFuture<Void> writeCompleteAd(String adId,
                                                   String cardId, Ad ad,
                                                   User user,
                                                   List<Bitmap> adPhotos,
                                                   List<Bitmap> panoramas,
                                                   Function<String,
                                                           CompletableFuture<Void>> loadProfilePic) {

        if (adId == null || cardId == null || ad == null || user == null || adPhotos == null || panoramas == null)
            throw new IllegalArgumentException();

        LocalDatabasePaths.cardID = cardId;
        LocalDatabasePaths.userID = user.getUserId();


        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();
        if (!createAdFolder(ad.getPhotosRefs().size(),
                ad.getPanoramaReferences().size())) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while creating the ad folder !"));
            return futureSuccess;
        }


        /*  I think we do not need to do something specific if the folder
            already exists, we will just overwrite the files
            Maybe we need to check for the number of images so that if the ad
            has one image less than before we do not could keep a stale image
            in the folder
        */

        //Building local versions of the ad only because we can build the
        // card from the ad
        Ad localAd = buildLocalAd(ad);
        User localUser = buildLocalUser(user);
        Card localCard = buildCardFromAd(localAd, cardId, adId,
                localUser.getUserId());

        //Adding the data to memory if we have done the first load
        syncWithMemory(adId, localAd, localCard, localUser);

        //Write the user
        if (!writeUser(localUser)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the user !"));
            return futureSuccess;
        }

        //We do not need to check if the set contains it or not.
        this.userIds.add(localUser.getUserId());


        //Serializing
        if (!writeAd(adId, localAd, localUser.getUserId(), cardId)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the ad !"));
            return futureSuccess;
        }

        if (!writeAdPhotos(adPhotos)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the ad photos !"));
            return futureSuccess;
        }

        if (!writePanoramas(panoramas)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the ad photos !"));
            return futureSuccess;
        }
        if (loadProfilePic != null && !localUser.hasDefaultProfileImage()) {
            writeProfilePic(loadProfilePic, futureSuccess);
        } else {
            futureSuccess.complete(null);
        }
        return futureSuccess;
    }

    /**
     * This method builds a card from the local ad, as most of the
     * information a card contains is already in the ad.
     *
     * @param ad     the ad
     * @param cardID the id of the card
     * @param adID   the id of the ad
     * @param userID the id of the user
     * @return a card
     */
    private Card buildCardFromAd(Ad ad, String cardID, String adID,
                                 String userID) {

        String imageUrl = null;
        if (!ad.getPhotosRefs().isEmpty()) {
            imageUrl = ad.getPhotosRefs().get(0);
        }

        return new Card(cardID, adID, userID, ad.getCity(),
                ad.getPrice(), imageUrl,
                ad.hasVRTour());
    }

    /**
     * This reads a folder containing an ad. It reads the file representing
     * the ad on disk and fill the appropriate data structures of this class.
     *
     * @param folder the folder that contains the file that represents the ad
     * @return a boolean that indicates if the operation succeeded or not
     */
    @SuppressWarnings("unchecked")
    private boolean readAdFolder(File folder) {


        String dataPath =
                new StoragePathBuilder().toDirectory(folder.getPath()).withFile(LocalDatabasePaths.dataFileName);

        Map<String, Object> adMap = FileIO.readMapObject(dataPath);
        if (adMap == null) return false;


        Ad ad = AdSerializer.deserialize(adMap);

        Ad adWithRef = new Ad(ad.getTitle(), ad.getPrice(),
                ad.getPricePeriod(), ad.getStreet(), ad.getCity(),
                ad.getAdvertiserName(), ad.getAdvertiserId(),
                ad.getDescription(),
                (List<java.lang.String>) adMap.get(PHOTO_REFS),
                (List<java.lang.String>) adMap.get(PANORAMA_REFS),
                ad.hasVRTour());

        this.idsToAd.put((String) adMap.get(ID), adWithRef);
        this.adIdsToPanoramas.put((String) adMap.get(ID),
                adWithRef.getPanoramaReferences());

        Card card = buildCardFromAd(adWithRef, (String) adMap.get(CARD_ID),
                (String) adMap.get(ID), (String) adMap.get(USER_ID));

        //Maybe use a set instead, but then need to implement equals
        this.cards.add(card);

        return true;
    }

    /**
     * This reads the whole ad data stored for the current user. It traverses
     * all the folder with a card id as their name. Beware the way this
     * function prevents having a card with id "users", but this is hardly
     * possible.
     *
     * @return a boolean that indicates if the operation succeeded or not
     */
    private boolean readAdDataForAUser() {
        String currentUserFolderPath =
                LocalDatabasePaths.currentUserFolder(getCurrentUser().getUserId());

        File favFolder = new File(currentUserFolderPath);

        Predicate<File> isDirectoryPredicate = File::isDirectory;
        Predicate<File> isNotUsersFolder = file -> !file.getName().equals(
                LocalDatabasePaths.usersFolder);

        FileFilter fileFilter =
                isDirectoryPredicate.and(isNotUsersFolder)::test;

        File[] folders = favFolder.listFiles(fileFilter);
        if (folders == null) return false;
        boolean success = true;
        for (File folder : folders) {
            success &= readAdFolder(folder);
        }
        if (success) {
            this.firstLoad = true;
        }
        return success;
    }

    /**
     * Returns the list of cards, if it manages to find it either on memory
     * or on disk.
     *
     * @return the list of cards, or null if it doesn't find it
     */
    public List<Card> getCards() {
        return getFromMemory(() -> cards);
    }

    /**
     * Returns an ad with ad id.
     *
     * @param adId the id of the ad
     * @return the ad if it finds it, null otherwise
     */
    public Ad getAd(String adId) {
        return getFromMemory(() -> idsToAd.get(adId));
    }

    /**
     * Returns a user with the user id as its id.
     *
     * @param wantedUserID the user id
     * @return the user if it finds it, null otherwise
     */
    public User getUser(String wantedUserID) {
        return getFromMemory(() -> idsToUser.get(wantedUserID));
    }

    /**
     * This reads the folder of a user. It will load the user data from disk.
     *
     * @param userFile a file pointing to the user's directory
     * @return a boolean that indicates if the operation succeeded or not
     */
    private boolean readUserFolder(File userFile) {

        String dataPath =
                new StoragePathBuilder().toDirectory(userFile.getPath()).withFile(LocalDatabasePaths.userData);
        Map<String, Object> userMap = FileIO.readMapObject(dataPath);


        User user = UserSerializer.deserialize((String) userMap.get(ID),
                userMap);
        if (!idsToUser.containsKey(userMap.get(ID))) {
            this.idsToUser.put((String) userMap.get(ID), user);
            this.userIds.add(user.getUserId());
        }

        return true;
    }

    /**
     * This reads the whole user data stored for the current user. It traverses
     * all the folder with a user id as their name, more specifically all the
     * folders in the users folder.
     *
     * @return a boolean that indicates if the operation succeeded or not
     */
    private boolean readUsers() {
        String userPath =
                LocalDatabasePaths.usersFolder(getCurrentUser().getUserId());
        File userFolder = new File(userPath);
        boolean success = true;
        for (File folder :
                Objects.requireNonNull(userFolder.listFiles(File::isDirectory))) {
            success &= readUserFolder(folder);
        }
        if (success) {
            this.firstLoad = true;
        }
        return success;


    }

    /**
     * This function is called every time we want to get something from the
     * local db. It checks if the data we have in memory is valid, if it is
     * it directly returns it. Otherwise, it loads everything on disk, and if
     * the operation succeeded, returns the wanted data.
     *
     * @param returnFunc the function that performs the actual retrieving of
     *                   data from one of the data structures of the class.
     * @return the data the user wants, or null if it doesn't find it
     */
    private <T> T getFromMemory(Supplier<T> returnFunc) {
        if (this.firstLoad) {
            return returnFunc.get();
        }
        boolean success = readAdDataForAUser();
        success &= readUsers();

        if (success) {
            return returnFunc.get();
        }
        return null;
    }

    /**
     * This method resets all the data structures of the class
     */
    private void clearMemory() {
        this.firstLoad = false;
        this.cards.clear();
        this.idsToAd.clear();
        this.idsToUser.clear();
        this.userIds.clear();
        this.adIdsToPanoramas.clear();
    }

    /**
     * This completely removes the favorites folder and everything it
     * contains. Useful for testing or if we reached illegal state.
     */
    public void cleanFavorites() {
        File favoritesDir =
                new File(LocalDatabasePaths.favoritesFolder());
        FileIO.deleteDir(favoritesDir);
        clearMemory();
    }

    /**
     * This method finds a card by id in the cards array and returns the
     * first index that reference a card with the same card id.
     *
     * @param cardId the id of the card
     * @return the index of the card in cards, or -1 if it doesn't find it.
     */
    private int findCardById(String cardId) {
        for (int i = 0; i < this.cards.size(); ++i) {
            Card card = this.cards.get(i);
            if (Objects.requireNonNull(card.getId()).equals(cardId)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Removes a card with id cardId. The user associated with this card is
     * removed only if it isn't referenced by any other card.
     *
     * @param cardId the id of the card
     */
    public void removeCard(String cardId) {
        String pathToCard =
                LocalDatabasePaths.cardFolder(getCurrentUser().getUserId(),
                        cardId);
        FileIO.deleteDir(new File(pathToCard));

        int cardIdx = findCardById(cardId);

        if (cardIdx == -1) return;

        Card card = this.cards.get(cardIdx);

        String adId = card.getAdId();
        String userId = card.getUserId();

        this.idsToAd.remove(adId);
        this.cards.remove(cardIdx);
        this.adIdsToPanoramas.remove(adId);

        boolean isUserUsed = false;
        for (int i = 0; i < this.cards.size() && !isUserUsed; ++i) {
            Card curCard = this.cards.get(i);

            if (curCard.getUserId().equals(userId)) {
                isUserUsed = true;
            }
        }

        if (!isUserUsed) {
            this.userIds.remove(userId);
            this.idsToUser.remove(userId);
            String userPath =
                    LocalDatabasePaths.userFolder(getCurrentUser().getUserId(), userId);
            FileIO.deleteDir(new File(userPath));
        }
    }

    /**
     * This sets the current user for the local database. The favorites are
     * stored per user. It is useful in the case where two accounts are used
     * on the same phone.
     * <p>
     * Also, the last currentUser is stored on disk so that when the user
     * goes into the app while offline, the app can "guess" who this is and
     * thus retrieve the correct favorite data.
     * <p>
     * This method should only be called when the app is online.
     *
     * @param currentUser    the current user
     * @param profilePicLoad the function that will perform the writing of
     *                       the profile picture of the user on disk
     * @return a completable future that indicates if the operation succeeded
     * or not.
     */
    public CompletableFuture<Void> setCurrentUser(User currentUser,
                                                  Function<String,
                                                          CompletableFuture<Void>> profilePicLoad) {
        this.currentUser = currentUser;

        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();

        User currentLocalUser = new AppUser(currentUser.getUserId(),
                currentUser.getUserEmail());
        if (currentUser.getPhoneNumber() != null) {
            currentLocalUser.setPhoneNumber(currentUser.getPhoneNumber());
        }
        if (currentUser.getName() != null) {
            currentLocalUser.setName(currentUser.getName());
        }

        currentLocalUser.setAge(currentUser.getAge());

        if (currentUser.getGender() != null) {
            currentLocalUser.setGender(currentUser.getGender());
        }

        if (!currentLocalUser.hasDefaultProfileImage()) {
            String profilePicPath =
                    LocalDatabasePaths.currentUserProfilePicture();
            currentLocalUser.setProfileImagePathAndName(profilePicPath);
            CompletableFuture<Void> futureProfilePic =
                    profilePicLoad.apply(profilePicPath);
            futureProfilePic.thenAccept(arg -> futureSuccess.complete(null));
            futureProfilePic.exceptionally(e -> {
                futureSuccess.completeExceptionally(e);
                return null;
            });
        }

        File favoritesFolder = new File(LocalDatabasePaths.favoritesFolder());
        if (!favoritesFolder.exists()) {
            boolean success = favoritesFolder.mkdirs();
            if (!success) {
                futureSuccess.completeExceptionally(new LocalDatabaseException("Error while creating favorites folder !"));
                return futureSuccess;
            }
        }


        Map<String, Object> userMap =
                UserSerializer.serialize(currentLocalUser);
        if (userMap == null) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while reading the user map !"));
            return futureSuccess;
        }
        //We ad the id because it is not serialized
        userMap.put(ID, currentLocalUser.getUserId());
        if (!FileIO.writeMapObject(LocalDatabasePaths.currentUserData(),
                userMap)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing current user !"));
            return futureSuccess;
        }

        futureSuccess.complete(null);

        return futureSuccess;
    }

    /**
     * This method tries to load the current user from the app data.
     *
     * @return the current user if it found one, null otherwise
     */
    private User loadCurrentUserOnDisk() {

        String currentUserPath = LocalDatabasePaths.currentUserData();

        File currentUserFile = new File(currentUserPath);
        if (!currentUserFile.exists()) return null;

        Map<String, Object> userMap = FileIO.readMapObject(currentUserPath);


        return UserSerializer.deserialize((String) userMap.get(ID),
                userMap);
    }

    /**
     * This tries to retrieve the current user either from memory or from
     * disk. If the current user is not in memory, then it will read the data
     * on disk.
     *
     * @return the current user if it finds one, null otherwise
     */
    public User loadCurrentUser() {
        if (this.currentUser != null) return this.currentUser;
        return loadCurrentUserOnDisk();
    }

    /**
     * This returns the list of panoramas paths for a specific ad.
     *
     * @param adID the id of the ad
     * @return the list of paths for the panoramas, or null if the operation
     * fails.
     */
    public List<String> getPanoramasPaths(String adID) {
        return getFromMemory(() -> this.adIdsToPanoramas.get(adID));
    }
}
