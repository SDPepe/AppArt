package ch.epfl.sdp.appart.database.local;

import android.util.Pair;

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
import ch.epfl.sdp.appart.utils.serializers.CardSerializer;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

public class LocalAd {

    private static final String ID = "ID";
    private static final String favoritesFolder = "/favorites";
    private static final String profilePicName = "user_profile_pic.jpg";
    private static final String dataFileName = "ad.fav";

    private static boolean createFolder(String adFolderPath, CompletableFuture<Void> futureSuccess) {
        //Creating local folder for the complete ad
        File adFolder = new File(adFolderPath);
        if (!adFolder.exists()) {
            boolean createdDir = adFolder.mkdirs();
            if (!createdDir) {
                futureSuccess.completeExceptionally(new IOException("Could " +
                        "not create dir :" + adFolderPath));
                return  false;
            }
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

    private static Pair<Ad, User> buildLocalStructures(Ad ad, User user, List<String> localPhotoRefs, String adFolderPath) {
        Ad localAd = new Ad(ad.getTitle(), ad.getPrice(), ad.getPricePeriod()
                , ad.getStreet(), ad.getCity(), ad.getAdvertiserId(),
                ad.getDescription(), localPhotoRefs, ad.hasVRTour());

        User localUser = new AppUser(user.getUserId(), user.getUserEmail());
        localUser.setPhoneNumber(user.getPhoneNumber());
        localUser.setName(user.getName());
        localUser.setAge(user.getAge());
        localUser.setGender(user.getGender());
        localUser.setProfileImage(adFolderPath + FirebaseLayout.SEPARATOR + profilePicName);

        return new Pair<>(localAd, user);
    }

    private static boolean writeFile(String adFolderPath, List<Map<String, Object>> mapList, CompletableFuture<Void> futureSuccess) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(adFolderPath + FirebaseLayout.SEPARATOR + dataFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mapList);
            oos.close();
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
     * @param cardId     the card id
     * @param ad         the ad that will be written on disk
     * @param user       the user that will be written on disk
     * @param appPath    the path of the app folder. It is given to the
     *                   function because we don't want to deal with anything
     *                   related to android, such as the Context.
     * @param loadImages the function that will load the images. The String
     *                   argument will take the name of the folder where we
     *                   want to store the images. It will return a Void
     *                   completable future to indicate when it has finished,
     *                   and whether it has succeeded or not.
     * @return a Void completable future to indicate whether the task has
     * succeeded or not.
     */
    public static CompletableFuture<Void> writeCompleteAd(String cardId, Ad ad,
                                                          User user,
                                                          String appPath,
                                                          Function<String,
                                                                  CompletableFuture<Void>> loadImages) {

        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();

        String favoritesPath = appPath + favoritesFolder + "/";
        String adFolderPath = favoritesPath + cardId;

        if(!createFolder(adFolderPath, futureSuccess)) return futureSuccess;


        //I think we do not need to do something specific if the folder already exists, we will just overwrite the files
        //Maybe we need to check for the number of images so that if the ad has one image less than before we do not could keep a stale image in the folder

        //Changing the photoRefs to local paths
        List<String> localPhotoRefs = buildPhotoRefs(adFolderPath, ad.getPhotosRefs().size());

        //Building local versions of the ad only because we can build the card from the ad
        Pair<Ad, User> structures = buildLocalStructures(ad, user, localPhotoRefs, adFolderPath);

        Ad localAd = structures.first;
        User localUser = structures.second;

        //Serializing
        Map<String, Object> adMap = AdSerializer.serialize(localAd);

        Map<String, Object> userMap = UserSerializer.serialize(localUser);
        //We ad the id because it is not serialized
        userMap.put(ID, localUser.getUserId());

        //We use a list of HashMap so that it is simple to put all the data in a single file.
        //Indeed, we can serialize the List as one big object and then retrieve the individual hash maps
        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(adMap);
        mapList.add(userMap);

        //Write file on disk
        if(!writeFile(adFolderPath, mapList, futureSuccess)) return futureSuccess;

        CompletableFuture<Void> futureImageLoad =
                loadImages.apply(adFolderPath);
        futureImageLoad.thenAccept(arg -> futureSuccess.complete(null));
        futureImageLoad.exceptionally(e -> {
            futureSuccess.completeExceptionally(e);
            return null;
        });

        return futureSuccess;
    }

    /*public static CompletableFuture<LocalCompleteAd> loadCompleteAd(String fullPath) {
        CompletableFuture<LocalCompleteAd> futureCompleteAd =
                new CompletableFuture<>();

        FileInputStream fis;
        List<Map<String, Object>> mapList;
        try {
            fis = new FileInputStream(fullPath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mapList = (List<Map<String, Object>>) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            futureCompleteAd.completeExceptionally(e);
            return futureCompleteAd;
        }

        Map<String, Object> cardMap = mapList.get(0);
        Map<String, Object> adMap = mapList.get(1);
        Map<String, Object> userMap = mapList.get(2);

        Card card = CardSerializer.deserialize((String) cardMap.get(ID),
                cardMap);


        User user = UserSerializer.deserialize((String) userMap.get(ID),
                userMap);

        LocalCompleteAd completeAd = new LocalCompleteAd(card, null, user);
        futureCompleteAd.complete(completeAd);

        return futureCompleteAd;
    }*/

    public static CompletableFuture<List<LocalCompleteAd>> findLocalAds(String appFolder) {
        return  null;
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
