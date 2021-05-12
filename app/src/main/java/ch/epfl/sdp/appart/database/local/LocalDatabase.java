package ch.epfl.sdp.appart.database.local;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;


/**
 * This class is the one that will perform the reading and writing of an ad.
 * When reading or writing it will need the adFolderPath which corresponds to
 * Context.getFilesDir().
 * This class shouldn't contain anything related to android.
 * <p>
 * because we want to have quick access to it.
 * not stored in the same folder. We need to load the user first and then the
 * ad. We need to load the ad, get the id and then load the user.
 * interface if its better for the callers.
 */
@SuppressWarnings("unchecked")
public class LocalDatabase {

    private static final String ID = "ID";
    private static final String PHOTO_REFS = "photo_refs";
    private static final String CARD_ID = "CARD_ID";
    private static final String USER_ID = "USER_ID";
    private static final String PANORAMA_REFS = "panorama_refs";

    private static final String favoritesFolder = "/favorites";
    //TODO: Clean this
    private static final String profilePicName =
            FirebaseLayout.PROFILE_IMAGE_NAME + ".jpeg";
    private static final String dataFileName = "data.fav";
    private static final String usersFolder = "/users";
    private static final String userData = "user.data";
    private static final String currentUserData = "currentUser.data";

    private final String appPath;

    private final List<Card> cards;
    private final Map<String, Ad> idsToAd;
    private final Map<String, User> idsToUser;
    private final Set<String> userIds;
    private final Map<String, List<String>> adIdsToPanoramas;

    private User currentUser = null;

    private boolean firstLoad;

    public LocalDatabase(String appPath) {
        if (appPath == null) throw new IllegalArgumentException();
        this.appPath = appPath;
        this.cards = new ArrayList<>();
        this.idsToAd = new HashMap<>();
        this.idsToUser = new HashMap<>();
        this.firstLoad = false;
        this.userIds = new HashSet<>();
        this.adIdsToPanoramas = new HashMap<>();
    }

    public User getCurrentUser() throws IOException, ClassNotFoundException {
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

    private String getFavoritesPath() {
        return this.appPath + favoritesFolder;
    }

    private String getAdFolderPath(String cardID) throws IOException,
            ClassNotFoundException {

        return this.getFavoritesPath() + "/" + getCurrentUser().getUserId() + "/" + cardID;
    }

    private String getAdDataPath(String cardID) throws IOException,
            ClassNotFoundException {
        return getAdFolderPath(cardID) + "/" + dataFileName;
    }

    private String getAdDataPathFromAdFolder(String adFolderPath) {
        return adFolderPath + "/" + dataFileName;
    }

    private String getPhotoPathFromAdFolder(String adFolderPath,
                                            String photoName) {
        return adFolderPath + "/" + photoName;
    }

    private String getUserPathFromFavorites(String favoritesPath,
                                            String userID) throws IOException
            , ClassNotFoundException {
        return favoritesPath + "/" + getCurrentUser().getUserId() + "/" + usersFolder + "/" + userID;
    }

    private String getProfilePicPathFromAdFolder(String adFolderPath) {
        return adFolderPath + FirebaseLayout.SEPARATOR + profilePicName;
    }

    private String getCurrentUserPath() {
        return this.appPath + favoritesFolder + "/" + currentUserData;
    }

    private String getProfilePicPath() {
        return this.appPath + favoritesFolder + "/" + profilePicName;
    }

    private Map<String, Object> readMapObject(String path) throws IOException
            , ClassNotFoundException {
        FileInputStream fis;
        Map<String, Object> map;
        fis = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(fis);
        //noinspection unchecked
        map = (Map<String, Object>) ois.readObject();
        ois.close();
        fis.close();
        return map;
    }

    /**
     * This function allows us to retrieve the number of photos of the ad if
     * it already exists in the favorites folder.
     *
     * @param adFolderPath the path to the local ad
     * @return an int that represents the number of photos for the ad
     */
    private int getNumberOfPhotos(String adFolderPath) throws IOException,
            ClassNotFoundException {
        String dataPath = getAdDataPathFromAdFolder(adFolderPath);


        Map<String, Object> adMap = readMapObject(dataPath);
        @SuppressWarnings("unchecked") List<String> localPhotoRefs =
                (List<String>) adMap.get(PHOTO_REFS);
        return localPhotoRefs.size();
    }

    private int getNumberOfPanoramas(String adFolderPath) throws IOException,
            ClassNotFoundException {
        String dataPath = getAdDataPathFromAdFolder(adFolderPath);


        Map<String, Object> adMap = readMapObject(dataPath);
        //noinspection unchecked
        List<String> localPanoramaRefs =
                (List<String>) adMap.get(PANORAMA_REFS);
        return localPanoramaRefs.size();
    }


    /**
     * This function allows to remove the extra photos that might stay on
     * disk if the newly updated ad has less photos than the previous version.
     *
     * @param adFolderPath         the path to the local ad
     * @param futureNumberOfPhotos the number of photos the updated version
     *                             of the ad
     * @return a boolean that indicates if the operation succeeded or not
     */
    private boolean removeExtraPhotos(String adFolderPath,
                                      int futureNumberOfPhotos) throws IOException, ClassNotFoundException {
        return removeExtraPictures(adFolderPath,
                getNumberOfPhotos(adFolderPath), futureNumberOfPhotos,
                FirebaseLayout.PHOTO_NAME);
    }

    private boolean removeExtraPictures(String adFolderPath,
                                        int curNumberOfPics,
                                        int futureNumberOfPics, String name) {
        int deletedFiles = 0;
        if (curNumberOfPics > futureNumberOfPics) {
            for (int i = futureNumberOfPics; i < curNumberOfPics; ++i) {
                String photoPath = getPhotoPathFromAdFolder(adFolderPath,
                        name + i + ".jpeg");
                File photoToDelete = new File(photoPath);
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

    private boolean removeExtraPanoramas(String adFolderPath,
                                         int futureNumberOfPanoramas) throws IOException, ClassNotFoundException {
        return removeExtraPictures(adFolderPath,
                getNumberOfPanoramas(adFolderPath), futureNumberOfPanoramas,
                FirebaseLayout.PANORAMA_NAME);
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

    private boolean createFolder(String path, Supplier<Boolean> alreadyExists) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            //We can just remove the extra photos because FileOutputStream
            // overwrites the whole file
            return alreadyExists.get();
        }
    }

    /**
     * This function creates the folder for the local ad. If it already
     * exists it calls removeExtraPhotos to make sure we do not leave any
     * invalid data in the folder. It does not need to remove anything else
     * since the ad data will be stored in a file that will be overwritten,
     * same thing for the profile picture.
     *
     * @param adFolderPath         the path to the ad folder
     * @param futureNumberOfPhotos the number of photos of the newly updated
     *                             ad, its sole purpose is to be used when
     *                             the ad already exists on disk and we want
     *                             to make sure
     * @return
     */
    private boolean createAdFolder(String adFolderPath,
                                   int futureNumberOfPhotos,
                                   int futureNumberOfPanoramas) {
        return createFolder(adFolderPath, () -> {
            try {
                return removeExtraPhotos(adFolderPath, futureNumberOfPhotos) && removeExtraPanoramas(adFolderPath, futureNumberOfPanoramas);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    private boolean createUserFolder(String favoritesPath, String userId) throws IOException, ClassNotFoundException {

        String userPath = getUserPathFromFavorites(favoritesPath, userId);
        return createFolder(userPath, () -> {
            String profilePicPath = userPath + "/" + profilePicName;
            File profilePic = new File(profilePicPath);
            profilePic.delete();
            return true;
        });
    }

    private List<String> buildPhotoRefs(String adFolderPath, int size) {
        return buildLocalRefs(adFolderPath, size, FirebaseLayout.PHOTO_NAME);
    }

    private List<String> buildPanoramaRefs(String adFolderPath, int size) {
        return buildLocalRefs(adFolderPath, size, FirebaseLayout.PANORAMA_NAME);
    }

    private List<String> buildLocalRefs(String adFolderPath, int size,
                                        String name) {
        List<String> localRefs = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            localRefs.add(getPhotoPathFromAdFolder(adFolderPath,
                    name + i + ".jpeg"));
        }
        return localRefs;
    }

    private Ad buildLocalAd(Ad ad, String adFolderPath) {
        List<String> localPhotoRefs = buildPhotoRefs(adFolderPath,
                ad.getPhotosRefs().size());
        List<String> localPanoramaRefs = buildPanoramaRefs(adFolderPath,
                ad.getPhotosRefs().size());

        return new Ad(ad.getTitle(), ad.getPrice(), ad.getPricePeriod()
                , ad.getStreet(), ad.getCity(), ad.getAdvertiserName(),
                ad.getAdvertiserId(),
                ad.getDescription(), localPhotoRefs,
                localPanoramaRefs, ad.hasVRTour());
    }

    private User buildLocalUser(User user, String adFolderPath) {
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


        localUser.setProfileImagePathAndName(getProfilePicPathFromAdFolder(adFolderPath));

        return localUser;
    }

    private static boolean writeMapObject(String path,
                                          Map<String, Object> map,
                                          String filename) throws IOException {
        FileOutputStream fos;
        fos = new FileOutputStream(path + FirebaseLayout.SEPARATOR + filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
        oos.close();
        fos.close();
        return true;
    }

    private boolean saveBitmap(Bitmap bitmap, String path) {
        File photo = new File(path);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(photo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

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

    private boolean writeUser(User localUser, String favoritesPath) throws IOException, ClassNotFoundException {
        //This check is not correct as it prevents the update of a user
        //if (!this.userIds.contains(localUser.getUserId()))
        if (!createUserFolder(favoritesPath, localUser.getUserId()))
            return false;
        Map<String, Object> userMap = UserSerializer.serialize(localUser);
        //We ad the id because it is not serialized
        userMap.put(ID, localUser.getUserId());

        return writeMapObject(getUserPathFromFavorites(favoritesPath,
                localUser.getUserId()), userMap, userData);
    }

    private boolean writeAd(String adID, Ad localAd, String userID,
                            String cardID, String adFolderPath) throws IOException {
        //Serializing
        Map<String, Object> adMap = AdSerializer.serialize(localAd);
        adMap.put(ID, adID);
        adMap.put(PHOTO_REFS, localAd.getPhotosRefs());
        adMap.put(CARD_ID, cardID);
        adMap.put(USER_ID, userID);
        adMap.put(PANORAMA_REFS, localAd.getPanoramaReferences());

        //Write file on disk
        return writeMapObject(adFolderPath, adMap, dataFileName);
    }

    private boolean writeAdPhotos(List<Bitmap> adPhotos, String adFolderPath) {
        return writeImage(adPhotos, adFolderPath, FirebaseLayout.PHOTO_NAME);
    }

    private boolean writeImage(List<Bitmap> bitmaps, String adFolderPath,
                               String fileBaseName) {
        boolean photoSaveSuccess = true;
        for (int i = 0; i < bitmaps.size(); ++i) {
            photoSaveSuccess &= saveBitmap(bitmaps.get(i), adFolderPath +
                    "/" + fileBaseName + i + ".jpeg");
        }
        return photoSaveSuccess;
    }

    private boolean writePanoramas(List<Bitmap> panoramas,
                                   String adFolderPath) {
        return writeImage(panoramas, adFolderPath,
                FirebaseLayout.PANORAMA_NAME);
    }

    private CompletableFuture<Void> writeProfilePic(Function<String,
            CompletableFuture<Void>> loadProfilePic, String adFolderPath) {

        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();
        //We need to change this to BitMap loading
        CompletableFuture<Void> futureProfilePic =
                loadProfilePic.apply(adFolderPath + "/" + profilePicName);
        futureProfilePic.thenAccept(arg -> futureSuccess.complete(null));
        futureProfilePic.exceptionally(e -> {
            futureSuccess.completeExceptionally(e);
            return null;
        });

        futureSuccess.complete(null);
        return futureSuccess;
    }

    /**
     * This function handles the writing of a complete ad to the local
     * storage. It will create a folder with all the ad images and a "binary"
     * file. The name of the folder will be the card ID. The name of the
     * binary file will be also be the card ID. The images will keep their
     * names. Since we do not want the LocalAd file to not deal with anything
     * related to Firebase, we take as parameter a function that takes in a
     * String and return a Void completable future. This function will
     * perform the getFile from StorageReference, and download the images
     * (card + ad + user) to the specified folder (the string arg). It will
     * return a completable future of void to indicate if the task succeeded
     * or not and to allow the writeCompleteAd function to perform the
     * actions when the operation is finished. This function will modify the
     * path to the images so that on load, the images from local storage will
     * be displayed.
     *
     * @param adId   the id of the ad, we need it because the ad doesn't
     *               store it
     * @param cardId the card id
     * @param ad     the ad that will be written on disk
     * @param user   the user that will be written on disk
     * @return a Void completable future to indicate whether the task has
     * succeeded or not.
     */
    public CompletableFuture<Void> writeCompleteAd(String adId,
                                                   String cardId, Ad ad,
                                                   User user,
                                                   List<Bitmap> adPhotos,
                                                   List<Bitmap> panoramas,
                                                   Function<String,
                                                           CompletableFuture<Void>> loadProfilePic) throws IOException, ClassNotFoundException {
        String favoritesPath = getFavoritesPath();

        //This path should allow multiple users per phone as the userId is
        // used to name the folder
        String adFolderPath = getAdFolderPath(cardId);


        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();
        if (!createAdFolder(adFolderPath, ad.getPhotosRefs().size(),
                ad.getPanoramaReferences().size())) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while creating the ad folder !"));
            return futureSuccess;
        }


        //I think we do not need to do something specific if the folder
        // already exists, we will just overwrite the files
        //Maybe we need to check for the number of images so that if the ad
        // has one image less than before we do not could keep a stale image
        // in the folder
        // in the folder

        //Building local versions of the ad only because we can build the
        // card from the ad
        Ad localAd = buildLocalAd(ad, adFolderPath);
        User localUser = buildLocalUser(user, adFolderPath);
        Card localCard = buildCardFromAd(localAd, cardId, adId,
                localUser.getUserId());

        //Adding the data to memory if we have done the first load
        syncWithMemory(adId, localAd, localCard, localUser);

        //Write the user
        if (!writeUser(localUser, favoritesPath)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the user !"));
            return futureSuccess;
        }

        //We do not need to check if the set contains it or not.
        this.userIds.add(localUser.getUserId());


        //Serializing
        if (!writeAd(adId, localAd, localUser.getUserId(), cardId,
                adFolderPath)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the ad !"));
            return futureSuccess;
        }

        if (!writeAdPhotos(adPhotos, adFolderPath)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the ad photos !"));
            return futureSuccess;
        }

        if (!writePanoramas(panoramas, adFolderPath)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the ad photos !"));
            return futureSuccess;
        }
        futureSuccess.complete(null);
        return futureSuccess;
    }

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

    @SuppressWarnings("unchecked")
    private boolean readAdFolder(File folder) throws IOException,
            ClassNotFoundException {
        String dataPath =
                folder.getPath() + "/" + LocalDatabase.dataFileName;

        Map<String, Object> adMap = readMapObject(dataPath);


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

    private boolean readAdDataForAUser(String currentUserID) throws IOException, ClassNotFoundException {
        String favoritesFolderPath =
                this.appPath + LocalDatabase.favoritesFolder + "/" + currentUserID;
        File favFolder = new File(favoritesFolderPath);

        Predicate<File> isDirectoryPredicate = File::isDirectory;
        Predicate<File> isNotUsersFolder = file -> !file.getName().equals(
                "users");

        FileFilter fileFilter =
                isDirectoryPredicate.and(isNotUsersFolder)::test;

        File[] folders = favFolder.listFiles(fileFilter);
        boolean success = true;
        for (File folder : folders) {
            success &= readAdFolder(folder);
        }
        if (success) {
            this.firstLoad = true;
        }
        return success;
    }

    public List<Card> getCards(String currentUserID) throws IOException,
            ClassNotFoundException {
        return getFromMemory(currentUserID, () -> cards);
    }

    public Ad getAd(String adId, String currentUserID) throws IOException,
            ClassNotFoundException {
        return getFromMemory(currentUserID, () -> idsToAd.get(adId));
    }

    public User getUser(String currentUserID, String wantedUserID) throws IOException, ClassNotFoundException {
        return getFromMemory(currentUserID, () -> idsToUser.get(wantedUserID));
    }

    private void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                deleteDir(file);
            }
        }
        dir.delete();
    }

    private boolean readUserFolder(File userFile) throws IOException,
            ClassNotFoundException {

        String dataPath = userFile.getPath() + "/" + userData;
        Map<String, Object> userMap = readMapObject(dataPath);


        User user = UserSerializer.deserialize((String) userMap.get(ID),
                userMap);
        if (!idsToUser.containsKey((String) userMap.get(ID))) {
            this.idsToUser.put((String) userMap.get(ID), user);
            this.userIds.add(user.getUserId());
        }

        return true;
    }

    private boolean readUsers() throws IOException, ClassNotFoundException {
        String userPath =
                this.appPath + LocalDatabase.favoritesFolder + "/" + getCurrentUser().getUserId() + usersFolder;
        File userFolder = new File(userPath);
        boolean success = true;
        for (File folder : userFolder.listFiles(File::isDirectory)) {
            success &= readUserFolder(folder);
        }
        if (success) {
            this.firstLoad = true;
        }
        return success;


    }

    private <T> T getFromMemory(String currentUserID, Supplier<T> returnFunc) throws IOException, ClassNotFoundException {
        if (this.firstLoad) {
            return returnFunc.get();
        }
        boolean success = readAdDataForAUser(currentUserID);
        success &= readUsers();

        if (success) {
            return returnFunc.get();
        }
        return null;
    }

    private void clearMemory() {
        this.firstLoad = false;
        this.cards.clear();
        this.idsToAd.clear();
        this.idsToUser.clear();
        this.userIds.clear();
        this.adIdsToPanoramas.clear();
    }

    public void cleanFavorites() {
        File favoritesDir = new File(this.appPath + LocalDatabase.favoritesFolder);
        deleteDir(favoritesDir);
        clearMemory();
    }

    public int findCardById(String cardId) {
        for (int i = 0; i < this.cards.size(); ++i) {
            Card card = this.cards.get(i);
            if (card.getId().equals(cardId)) {
                return i;
            }
        }
        return -1;
    }


    public void removeCard(String cardId, String currentUserID) {
        String pathToCard =
                this.appPath + LocalDatabase.favoritesFolder + "/" + currentUserID + "/" + cardId;
        deleteDir(new File(pathToCard));

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
                    getFavoritesPath() + "/" + currentUserID + usersFolder +
                            "/" + userId;
            deleteDir(new File(userPath));
        }
    }

    public CompletableFuture<Void> setCurrentUser(User currentUser,
                                                  Function<String,
                                                          CompletableFuture<Void>> profilePicLoad) throws IOException {
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
            String profilePicPath = getProfilePicPath();
            currentLocalUser.setProfileImagePathAndName(profilePicPath);
            CompletableFuture<Void> futureProfilePic =
                    profilePicLoad.apply(profilePicPath);
            futureProfilePic.thenAccept(arg -> futureSuccess.complete(null));
            futureProfilePic.exceptionally(e -> {
                futureSuccess.completeExceptionally(e);
                return null;
            });
        }

        File favoritesFolder = new File(getFavoritesPath());
        if (!favoritesFolder.exists()) {
            boolean success = favoritesFolder.mkdirs();
            if (!success) {
                return null;
            }
        }


        Map<String, Object> userMap =
                UserSerializer.serialize(currentLocalUser);
        //We ad the id because it is not serialized
        userMap.put(ID, currentLocalUser.getUserId());
        if (!writeMapObject(getFavoritesPath(), userMap, currentUserData)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing current user !"));
            return futureSuccess;
        }

        futureSuccess.complete(null);

        return futureSuccess;
    }

    private User loadCurrentUserOnDisk() throws IOException,
            ClassNotFoundException {

        String currentUserPath = getCurrentUserPath();

        File currentUserFile = new File(currentUserPath);
        if (!currentUserFile.exists()) return null;

        Map<String, Object> userMap = readMapObject(currentUserPath);


        return UserSerializer.deserialize((String) userMap.get(ID),
                userMap);
    }

    public User loadCurrentUser() throws IOException, ClassNotFoundException {
        if (this.currentUser != null) return this.currentUser;
        return loadCurrentUserOnDisk();
    }

    public static class LocalDatabaseException extends Exception {
        public LocalDatabaseException(String msg) {
            super(msg);
        }
    }

    public List<String> getPanoramasPaths(String adID) throws IOException,
            ClassNotFoundException {
        return getFromMemory(getCurrentUser().getUserId(), () -> this.adIdsToPanoramas.get(adID));
    }
}
