package ch.epfl.sdp.appart.database.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

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
 * //TODO: Implement removeLocalAd(String cardId) --> Or maybe other
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

    private final String appPath;

    private final List<Card> cards;
    private final Map<String, Ad> idsToAd;
    private final Map<String, User> idsToUser;

    private boolean isDataInitialized;

    public LocalAd(String appPath) {
        if (appPath == null) throw new IllegalArgumentException();
        this.appPath = appPath;
        this.cards = new ArrayList<>();
        this.idsToAd = new HashMap<>();
        this.idsToUser = new HashMap<>();
        this.isDataInitialized = false;
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
    private static boolean createFolder(String adFolderPath,
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
            return removeExtraPhotos(adFolderPath, futureNumberOfPhotos);
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

    private static boolean writeFile(String adFolderPath, List<Map<String,
            Object>> mapList, CompletableFuture<Void> futureSuccess) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(adFolderPath + FirebaseLayout.SEPARATOR + dataFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mapList);
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

        //For now we re read the full favorites every time, this might be
        // optimized later
        this.isDataInitialized = false;
        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();
        String favoritesPath = appPath + favoritesFolder + "/";

        //This path should allow multiple users per phone as the userId is
        // used to name the folder
        String adFolderPath = favoritesPath + currentUserId + "/" + cardId;

        if (!createFolder(adFolderPath, futureSuccess,
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

        //Serializing
        Map<String, Object> adMap = AdSerializer.serialize(localAd);
        adMap.put(ID, adId);
        adMap.put(PHOTO_REFS, localAd.getPhotosRefs());
        adMap.put(CARD_ID, cardId);

        Map<String, Object> userMap = UserSerializer.serialize(localUser);
        //We ad the id because it is not serialized
        userMap.put(ID, localUser.getUserId());


        //We use a list of HashMap so that it is simple to put all the data
        // in a single file.
        //Indeed, we can serialize the List as one big object and then
        // retrieve the individual hash maps
        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(adMap);
        mapList.add(userMap);

        //Write file on disk
        if (!writeFile(adFolderPath, mapList, futureSuccess))
            return futureSuccess;

        CompletableFuture<Void> futureImageLoad =
                loadImages.apply(adFolderPath); //store les images sur le
        // disk localadfolder/Photo0.jpg, Photo1.jpg
        futureImageLoad.thenAccept(arg -> futureSuccess.complete(null));
        futureImageLoad.exceptionally(e -> {
            futureSuccess.completeExceptionally(e);
            return null;
        });

        return futureSuccess;
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
        this.idsToUser.put((String) userMap.get(ID), user);

        Ad ad = AdSerializer.deserialize(adMap);

        Ad adWithRef = new Ad(ad.getTitle(), ad.getPrice(),
                ad.getPricePeriod(), ad.getStreet(), ad.getCity(),
                ad.getAdvertiserName(), ad.getAdvertiserId(),
                ad.getDescription(),
                (List<java.lang.String>) adMap.get(PHOTO_REFS), ad.hasVRTour());

        this.idsToAd.put((String) adMap.get(ID), adWithRef);

        //photoRefs.get(0) maybe unsafe but I don't think really think so
        //TODO:!!!! It is if the ad doesn't have any photos
        Card card = new Card((String) adMap.get(CARD_ID),
                (String) adMap.get(ID), user.getUserId(), adWithRef.getCity(),
                adWithRef.getPrice(), adWithRef.getPhotosRefs().get(0),
                adWithRef.hasVRTour());
        //Maybe use a set instead, but then need to implement equals
        this.cards.add(card);

        return true;
    }

    private boolean readAllLocalDataForAUser(String wantedUserID) {
        String favoritesFolderPath =
                this.appPath + LocalAd.favoritesFolder + "/" + wantedUserID;
        File favFolder = new File(favoritesFolderPath);
        File[] folders = favFolder.listFiles(File::isDirectory);
        boolean failed = false;
        for (File folder : folders) {
            failed |= readFolder(folder);
        }
        if (!failed) {
            this.isDataInitialized = true;
        }
        return failed;
    }

    public List<Card> getCards(String currentUserID) {

        if (this.isDataInitialized) {
            return cards;
        }
        boolean success = readAllLocalDataForAUser(currentUserID);
        if (success) {
            return cards;
        }

        return null;
    }

    public Ad getAd(String adId, String currentUserID) {
        if (this.isDataInitialized) {
            return idsToAd.get(adId);
        }
        boolean success = readAllLocalDataForAUser(currentUserID);
        if (success) {
            return idsToAd.get(adId);
        }
        return null;
    }

    public User getUser(String currentUserID, String wantedUserID) {
        if (this.isDataInitialized) {
            return idsToUser.get(wantedUserID);
        }
        boolean success = readAllLocalDataForAUser(currentUserID);
        if (success) {
            return idsToUser.get(wantedUserID);
        }
        return null;
    }


    public static class LocalCompleteAd {
        public final Card card;
        public final Ad ad;
        public final User user;

        public LocalCompleteAd(Card card, Ad ad, User user) {
            this.card = card;
            this.ad = ad;
            this.user = user;
        }
    }

}
