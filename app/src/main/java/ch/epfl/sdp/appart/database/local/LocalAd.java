package ch.epfl.sdp.appart.database.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;
import ch.epfl.sdp.appart.utils.serializers.CardSerializer;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

public class LocalAd {

    private static final String ID = "ID";

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
     * @param card       the card that will written on disk
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
    public static CompletableFuture<Void> writeCompleteAd(Card card, Ad ad,
                                                          User user,
                                                          String appPath,
                                                          Function<String,
                                                                  CompletableFuture<Void>> loadImages) {

        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();

        //Creating local folder for the complete ad
        String adFolderPath = appPath + "/" + card.getId();

        File adFolder = new File(adFolderPath);
        if (!adFolder.exists()) {
            boolean createdDir = adFolder.mkdir();
            if (!createdDir) {
                futureSuccess.completeExceptionally(new IOException("Could " +
                        "not create dir :" + adFolderPath));
            }
        }

        //Changing the photoRefs to local paths
        List<String> localPhotoRefs = new ArrayList<>();
        for (int i = 0; i < ad.getPhotosRefs().size(); ++i) {
            localPhotoRefs.add(appPath + "/" + "Photo" + i + ".jpg");
        }

        //Building local versions of the ad and card
        Card localCard = new Card(card.getId(), card.getAdId(),
                card.getUserId(), card.getCity(), card.getPrice(),
                card.getImageUrl(), card.hasVRTour());
        Ad localAd = new Ad(ad.getTitle(), ad.getPrice(), ad.getPricePeriod()
                , ad.getStreet(), ad.getCity(), ad.getAdvertiserId(),
                ad.getDescription(), localPhotoRefs, ad.hasVRTour());

        User localUser = new AppUser(user.getUserId(), user.getUserEmail());
        localUser.setPhoneNumber(user.getPhoneNumber());
        localUser.setName(user.getName());
        localUser.setAge(user.getAge());
        localUser.setGender(user.getGender());
        localUser.setProfileImage(appPath + "/user_profile_pic.jpg");



        //Serializing
        Map<String, Object> cardMap = CardSerializer.serialize(localCard);
        cardMap.put(ID, localCard.getId());

        Map<String, Object> adMap = AdSerializer.serialize(localAd);


        Map<String, Object> userMap = UserSerializer.serialize(localUser);
        userMap.put(ID, localUser.getUserId());

        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(cardMap);
        mapList.add(adMap);
        mapList.add(userMap);


        FileOutputStream fos;
        try {
            fos = new FileOutputStream(appPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mapList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            futureSuccess.completeExceptionally(e);
        }

        CompletableFuture<Void> futureImageLoad =
                loadImages.apply(adFolderPath);
        futureImageLoad.thenAccept(arg -> futureSuccess.complete(null));
        futureImageLoad.exceptionally(e -> {
            futureSuccess.completeExceptionally(e);
            return null;
        });

        return futureSuccess;
    }

    public static CompletableFuture<LocalCompleteAd> loadCompleteAd(String fullPath) {
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
