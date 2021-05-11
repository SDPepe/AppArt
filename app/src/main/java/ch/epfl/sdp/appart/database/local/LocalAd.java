package ch.epfl.sdp.appart.database.local;

import java.io.File;
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
 * //TODO: Write unit tests but also test on android
 * //TODO: Write complete documentation
 * //TODO: Change loading to account for the act that the ad and the user are
 * not stored in the same folder. We need to load the user first and then the
 * ad. We need to load the ad, get the id and then load the user.
 * //TODO: Implement BITMAP loading
 * interface if its better for the callers.
 */
public class LocalAd {

    private static final String ID = "ID";
    private static final String PHOTO_REFS = "photo_refs";
    private static final String CARD_ID = "CARD_ID";

    private static final String favoritesFolder = "/favorites";
    private static final String profilePicName = "user_profile_pic.jpg";
    private static final String dataFileName = "data.fav";
    private static final String usersFolder = "/users";

    private final String appPath;

    private final List<Card> cards;
    private final Map<String, Ad> idsToAd;
    private final Map<String, User> idsToUser;
    private final Set<String> userIds;

    private boolean firstLoad;

    public LocalAd(String appPath) {
        if (appPath == null) throw new IllegalArgumentException();
        this.appPath = appPath;
        this.cards = new ArrayList<>();
        this.idsToAd = new HashMap<>();
        this.idsToUser = new HashMap<>();
        this.firstLoad = false;
        this.userIds = new HashSet<>();
    }

    /**
     * This function allows us to retrieve the number of photos of the ad if
     * it already exists in the favorites folder.
     *
     * @param adFolderPath the path to the local ad
     * @return an int that represents the number of photos for the ad
     */
    private static int getNumberOfPhotos(String adFolderPath) {
        String dataPath =
                adFolderPath + FirebaseLayout.SEPARATOR + dataFileName;

        FileInputStream fis;
        List<Map<String, Object>> mapList;
        try {
            fis = new FileInputStream(dataPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mapList = (List<Map<String, Object>>) ois.readObject();
            ois.close();
            fis.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return 0;
        }


        Map<String, Object> adMap = mapList.get(1);
        List<String> localPhotoRefs = (List<String>) adMap.get(PHOTO_REFS);

        return localPhotoRefs.size();
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
    private static boolean removeExtraPhotos(String adFolderPath,
                                             int futureNumberOfPhotos) {
        int curNumberOfPhotos = getNumberOfPhotos(adFolderPath);
        int deletedFiles = 0;
        if (curNumberOfPhotos > futureNumberOfPhotos) {
            //futureSize comes from getSize on a list. Therefore, its minimum
            // value is 0.
            //If curSize is > futureSize then we can safely do curSize - 1
            for (int i = futureNumberOfPhotos; i < curNumberOfPhotos; ++i) {
                String photoPath =
                        adFolderPath + FirebaseLayout.SEPARATOR + FirebaseLayout.PHOTO_NAME + i + ".jpg";
                File photoToDelete = new File(photoPath);
                if (photoToDelete.exists()) {
                    if (photoToDelete.delete()) {
                        deletedFiles++;
                    }
                }
            }
            return deletedFiles == (curNumberOfPhotos - futureNumberOfPhotos);
        }
        return true;
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
     * since the ad data will be stored in a file that will be overwritten,
     * same thing for the profile picture.
     *
     * @param adFolderPath         the path to the ad folder
     * @param futureSuccess        future that indicates the overall success of
     *                             writing a complete ad to local storage.
     * @param futureNumberOfPhotos the number of photos of the newly updated
     *                             ad, its sole purpose is to be used when
     *                             the ad already exists on disk and we want
     *                             to make sure
     * @return
     */
    private static boolean createAdFolder(String adFolderPath,
                                          CompletableFuture<Void> futureSuccess
            , int futureNumberOfPhotos) {
        //Creating local folder for the complete ad
        File adFolder = new File(adFolderPath);
        if (!adFolder.exists()) {
            boolean createdDir = adFolder.mkdirs();
            if (!createdDir) {
                futureSuccess.completeExceptionally(new IOException("Could " +
                        "not create dir :" + adFolderPath));
                return false;
            }
        } else {
            //We can just remove the extra photos because FileOutputStream
            // overwrites the whole file
            return removeExtraPhotos(adFolderPath, futureNumberOfPhotos);
        }
        return true;
    }

    private static boolean createUserFolder(String favoritesPath,
                                            CompletableFuture<Void> futureSuccess, String userId) {

        String userPath = favoritesPath + "/" + usersFolder + "/" + userId;

        //Creating local folder for the complete ad
        File adFolder = new File(userPath);
        if (!adFolder.exists()) {
            boolean createdDir = adFolder.mkdirs();
            if (!createdDir) {
                futureSuccess.completeExceptionally(new IOException("Could " +
                        "not create dir :" + userPath));
                return false;
            }
        } else {

            //We always delete the user's profile picture because it is as
            // costly to check it it exists and remove it only in this case.
            String profilePicPath = userPath + "/" + profilePicName;
            File profilePic = new File(profilePicPath);
            profilePic.delete();
        }
        return true;
    }

    private static List<String> buildPhotoRefs(String adFolderPath, int size) {
        List<String> localPhotoRefs = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            localPhotoRefs.add(adFolderPath + FirebaseLayout.SEPARATOR + FirebaseLayout.PHOTO_NAME + i + ".jpg");
        }
        return localPhotoRefs;
    }

    private static Ad buildLocalAd(Ad ad, String adFolderPath) {
        List<String> localPhotoRefs = buildPhotoRefs(adFolderPath,
                ad.getPhotosRefs().size());

        Ad localAd = new Ad(ad.getTitle(), ad.getPrice(), ad.getPricePeriod()
                , ad.getStreet(), ad.getCity(), ad.getAdvertiserName(),
                ad.getAdvertiserId(),
                ad.getDescription(), localPhotoRefs, ad.hasVRTour());
        return localAd;
    }

    private static User buildLocalUser(User user, String adFolderPath) {
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


        localUser.setProfileImagePathAndName(adFolderPath + FirebaseLayout.SEPARATOR + profilePicName);

        return localUser;
    }

    private static boolean writeFile(String path, Map<String,
            Object> map, CompletableFuture<Void> futureSuccess) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path + FirebaseLayout.SEPARATOR + dataFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            futureSuccess.completeExceptionally(e);
            return false;
        }
        return true;
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
     * @param adId       the id of the ad, we need it because the ad doesn't
     *                   store it
     * @param cardId     the card id
     * @param ad         the ad that will be written on disk
     * @param user       the user that will be written on disk
     * @param loadImages the function that will load the images. The String
     *                   argument will take the name of the folder where we
     *                   want to store the images. It will return a Void
     *                   completable future to indicate when it has finished,
     *                   and whether it has succeeded or not.
     * @return a Void completable future to indicate whether the task has
     * succeeded or not.
     */
    public CompletableFuture<Void> writeCompleteAd(String adId,
                                                   String cardId, Ad ad,
                                                   User user,
                                                   Function<String,
                                                           CompletableFuture<Void>> loadImages, String currentUserId) {
        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();
        String favoritesPath = appPath + favoritesFolder + "/";

        //This path should allow multiple users per phone as the userId is
        // used to name the folder
        String adFolderPath = favoritesPath + currentUserId + "/" + cardId;

        if (!createAdFolder(adFolderPath, futureSuccess,
                ad.getPhotosRefs().size()))
            return futureSuccess;


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
        if (this.firstLoad) {
            this.idsToAd.put(adId, localAd);
            this.cards.add(localCard);
            if (!this.userIds.contains(localUser.getUserId())) {
                this.idsToUser.put(localUser.getUserId(), localUser);
            }
        }

        //Write the user
        if (!this.userIds.contains(localUser.getUserId())) {
            if (!createUserFolder(favoritesPath, futureSuccess,
                    localUser.getUserId()))
                return futureSuccess;
            Map<String, Object> userMap = UserSerializer.serialize(localUser);
            //We ad the id because it is not serialized
            userMap.put(ID, localUser.getUserId());

            if (!writeFile(adFolderPath, userMap, futureSuccess))
                return futureSuccess;
        }

        //We do not need to check if the set contains it or not.
        this.userIds.add(localUser.getUserId());


        //Serializing
        Map<String, Object> adMap = AdSerializer.serialize(localAd);
        adMap.put(ID, adId);
        adMap.put(PHOTO_REFS, localAd.getPhotosRefs());
        adMap.put(CARD_ID, cardId);

        //Write file on disk
        if (!writeFile(adFolderPath, adMap, futureSuccess))
            return futureSuccess;


        //We need to change this to BitMap loading
        CompletableFuture<Void> futureImageLoad =
                loadImages.apply(adFolderPath);
        futureImageLoad.thenAccept(arg -> futureSuccess.complete(null));
        futureImageLoad.exceptionally(e -> {
            futureSuccess.completeExceptionally(e);
            return null;
        });

        return futureSuccess;
    }

    private Card buildCardFromAd(Ad ad, String cardID, String adID,
                                 String userID) {
        return new Card(cardID, adID, userID, ad.getCity(),
                ad.getPrice(), ad.getPhotosRefs().get(0),
                ad.hasVRTour());
    }

    private boolean readFolder(File folder) {
        String dataPath =
                folder.getPath() + "/" + LocalAd.dataFileName;

        FileInputStream fis;
        List<Map<String, Object>> mapList;
        try {
            fis = new FileInputStream(dataPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mapList = (List<Map<String, Object>>) ois.readObject();
            ois.close();
            fis.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return false;
        }


        Map<String, Object> adMap = mapList.get(0);
        Map<String, Object> userMap = mapList.get(1);


        User user = UserSerializer.deserialize((String) userMap.get(ID),
                userMap);
        if (!userIds.contains(user.getUserId())) {
            this.idsToUser.put((String) userMap.get(ID), user);
            this.userIds.add(user.getUserId());
        }


        Ad ad = AdSerializer.deserialize(adMap);

        Ad adWithRef = new Ad(ad.getTitle(), ad.getPrice(),
                ad.getPricePeriod(), ad.getStreet(), ad.getCity(),
                ad.getAdvertiserName(), ad.getAdvertiserId(),
                ad.getDescription(),
                (List<java.lang.String>) adMap.get(PHOTO_REFS), ad.hasVRTour());

        this.idsToAd.put((String) adMap.get(ID), adWithRef);


        //photoRefs.get(0) maybe unsafe but I don't think really think so
        //TODO:!!!! It is if the ad doesn't have any photos
        Card card = buildCardFromAd(adWithRef, (String) adMap.get(CARD_ID),
                (String) adMap.get(ID), user.getUserId());
        //Maybe use a set instead, but then need to implement equals
        this.cards.add(card);

        return true;
    }

    private boolean readAllLocalDataForAUser(String wantedUserID) {
        String favoritesFolderPath =
                this.appPath + LocalAd.favoritesFolder + "/" + wantedUserID;
        File favFolder = new File(favoritesFolderPath);
        File[] folders = favFolder.listFiles(File::isDirectory);
        boolean success = true;
        for (File folder : folders) {
            success &= readFolder(folder);
        }
        if (success) {
            this.firstLoad = true;
        }
        return success;
    }

    public List<Card> getCards(String currentUserID) {
        return getFromMemory(currentUserID, () -> cards);
    }

    public Ad getAd(String adId, String currentUserID) {
        return getFromMemory(currentUserID, () -> idsToAd.get(adId));
    }

    public User getUser(String currentUserID, String wantedUserID) {
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

    private <T> T getFromMemory(String currentUserID, Supplier<T> returnFunc) {
        if (this.firstLoad) {
            return returnFunc.get();
        }
        boolean success = readAllLocalDataForAUser(currentUserID);
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
    }

    public void cleanFavorites() {
        File favoritesDir = new File(this.appPath + LocalAd.favoritesFolder);
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
                this.appPath + LocalAd.favoritesFolder + "/" + currentUserID + "/" + cardId;
        deleteDir(new File(pathToCard));

        int cardIdx = findCardById(cardId);

        if (cardIdx == -1) return;

        Card card = this.cards.get(cardIdx);

        String adId = card.getAdId();
        String userId = card.getUserId();

        //TODO: There is one card per ad so it is ok to remove the card and
        // the ad. However, a user might have posted several ads, therefore
        // we need to check before removing the user from memory.
        // We should maybe store the users in a separate folder.
        this.idsToUser.remove(userId);
        this.idsToAd.remove(adId);
        this.cards.remove(cardIdx);
        this.userIds.remove(userId);
    }

}
