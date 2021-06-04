package ch.epfl.sdp.appart.database.local;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Predicate;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.exceptions.LocalDatabaseException;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.FileIO;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;

/**
 * This class manages everything that is related to writing an ad on disk.
 */
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
                ad.getPrice(), ad.getPricePeriod(), imageUrl, ad.hasVRTour());
    }

    /**
     * This reads a folder containing an ad. It reads the file representing
     * the ad on disk and fill the appropriate data structures of this class.
     * Upon reading data this method updates the relevant data structures.
     *
     * @param folder           the folder containing the ad data
     * @param cards            the arrays of cards that will be updated
     * @param idsToAd          the map mapping adIds to ad
     * @param adIdsToPanoramas the map mapping ad ids to panoramas
     * @throws LocalDatabaseException in case the reading of the map on disk
     *                                fails
     */
    private static void readAdFolder(File folder, List<Card> cards,
                                     Map<String, Ad> idsToAd, Map<String,
            List<String>> adIdsToPanoramas) throws LocalDatabaseException {

        String dataPath =
                new StoragePathBuilder().toDirectory(folder.getPath()).withFile(LocalDatabasePaths.dataFileName);

        Map<String, Object> adMap = FileIO.readMapObject(dataPath);
        if (adMap == null) {
            throw new LocalDatabaseException("Could not read the ad data file" +
                    " located at : " + dataPath);
        }


        Ad ad = AdSerializer.deserializeLocal(adMap);

        idsToAd.put((String) adMap.get(AdLayout.ID), ad);
        adIdsToPanoramas.put((String) adMap.get(AdLayout.ID),
                ad.getPanoramaReferences());

        Card card = buildCardFromAd(ad, (String) adMap.get(AdLayout.CARD_ID),
                (String) adMap.get(AdLayout.ID),
                (String) adMap.get(AdLayout.USER_ID));

        //Maybe use a set instead, but then need to implement equals
        cards.add(card);
    }

    /**
     * This reads the whole ad data stored for the current user. It traverses
     * all the folder with a card id as their name. Beware the way this
     * function prevents having a card with id "users", but this is hardly
     * possible. Also, the reading happens asynchronously. Upon reading data
     * the relevant data structures are updated.
     *
     * @param currentUserID    the current user ID
     * @param cards            an list containing cards
     * @param idsToAd          a map mapping ad ids to Ad
     * @param adIdsToPanoramas a map mapping ad ids to panoramas
     * @return a completable future that indicates if the operation succeeded
     * or not
     */
    static CompletableFuture<Void> readAdDataForAUser(String currentUserID,
                                                      List<Card> cards,
                                                      Map<String, Ad> idsToAd,
                                                      Map<String,
                                                              List<String>> adIdsToPanoramas) {
        String currentUserFolderPath =
                LocalDatabasePaths.currentUserFolder(currentUserID);

        File favFolder = new File(currentUserFolderPath);

        Predicate<File> isDirectoryPredicate = File::isDirectory;
        Predicate<File> isNotUsersFolder = file -> !file.getName().equals(
                LocalDatabasePaths.usersFolder);

        FileFilter fileFilter =
                isDirectoryPredicate.and(isNotUsersFolder)::test;


        return CompletableFuture.runAsync(() -> {

            File[] folders = favFolder.listFiles(fileFilter);

                /*throw new CompletionException(new LocalDatabaseException
                ("The" +
                        " ad folder : " + favFolder.getPath() + " doesn't " +
                        "contain any folders !"));*/

            if (folders != null) {
                for (File folder : folders) {
                    try {
                        readAdFolder(folder, cards, idsToAd,
                                adIdsToPanoramas);
                    } catch (LocalDatabaseException e) {
                        e.printStackTrace();
                        throw new CompletionException(e);
                    }
                }
            }
        });
    }
}
