package ch.epfl.sdp.appart.database.local;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.FileIO;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;

public class LocalAdReader {

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
    static Card buildCardFromAd(Ad ad, String cardID, String adID,
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
    private static boolean readAdFolder(File folder, List<Card> cards,
                                        Map<String,
            Ad> idsToAd, Map<String, List<String>> adIdsToPanoramas) {


        String dataPath =
                new StoragePathBuilder().toDirectory(folder.getPath()).withFile(LocalDatabasePaths.dataFileName);

        Map<String, Object> adMap = FileIO.readMapObject(dataPath);
        if (adMap == null) return false;


        Ad ad = AdSerializer.deserializeLocal(adMap);

        idsToAd.put((String) adMap.get(AdLayout.ID), ad);
        adIdsToPanoramas.put((String) adMap.get(AdLayout.ID),
                ad.getPanoramaReferences());

        Card card = buildCardFromAd(ad, (String) adMap.get(AdLayout.CARD_ID),
                (String) adMap.get(AdLayout.ID),
                (String) adMap.get(AdLayout.USER_ID));

        //Maybe use a set instead, but then need to implement equals
        cards.add(card);

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
    static boolean readAdDataForAUser(String currentUserID, List<Card> cards
            , Map<String, Ad> idsToAd,
                                      Map<String, List<String>> adIdsToPanoramas, Runnable onSuccess) {
        String currentUserFolderPath =
                LocalDatabasePaths.currentUserFolder(currentUserID);

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
            success &= readAdFolder(folder, cards, idsToAd, adIdsToPanoramas);
        }
        if (success) {
            //this.firstLoad = true;
            onSuccess.run();
        }
        return success;
    }
}
