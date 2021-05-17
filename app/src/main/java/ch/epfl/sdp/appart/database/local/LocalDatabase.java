package ch.epfl.sdp.appart.database.local;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.exceptions.LocalDatabaseException;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.FileIO;
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

@Singleton
public class LocalDatabase {

    /*
        Keys for the different maps.
     */


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
    @Inject
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

    private static void checkInfo(String adId, String cardId, Ad ad,
                                  User user, List<Bitmap> adPhotos,
                                  List<Bitmap> panoramas) {
        if (adId == null || cardId == null || ad == null || user == null || adPhotos == null || panoramas == null)
            throw new IllegalArgumentException();
    }

    /**
     * This function performs the writing of a complete ad into local storage
     * . It will create a folder for the added user only if it doesn't
     * already exists (in which case it is updated). It will do the same for
     * the ad. It also performs the writing of all the images. The photos of
     * ad, the panoramas of the ad and the profile picture of the user (if he
     * doesn't uses the default one). The writing of images happens
     * asynchronously.
     *
     * @param adId       the id of the ad
     * @param cardId     the id of the card
     * @param ad         the ad
     * @param user       the user who posted the ad
     * @param adPhotos   list of bitmaps representing the photos of the ad
     * @param panoramas  list of panoramas representing the panoramas of
     *                   the ad
     * @param profilePic bitmap for the profile picture of the user
     * @return a completable future that indicates if the operation succeeded
     * or not
     */
    public CompletableFuture<Void> writeCompleteAd(String adId,
                                                   String cardId, Ad ad,
                                                   User user,
                                                   List<Bitmap> adPhotos,
                                                   List<Bitmap> panoramas,
                                                   Bitmap profilePic) {

        checkInfo(adId, cardId, ad, user, adPhotos, panoramas);

        LocalDatabasePaths.cardID = cardId;
        LocalDatabasePaths.userID = user.getUserId();


        CompletableFuture<Void> futureSuccess = new CompletableFuture<>();
        if (!LocalAdWriter.createAdFolder(ad.getPhotosRefs().size(),
                ad.getPanoramaReferences().size(),
                getCurrentUser().getUserId())) {
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
        Ad localAd = LocalAdWriter.buildLocalAd(ad,
                getCurrentUser().getUserId());
        User localUser = LocalUserWriter.buildLocalUser(user,
                getCurrentUser().getUserId());
        Card localCard = LocalAdReader.buildCardFromAd(localAd, cardId, adId,
                localUser.getUserId());

        //Adding the data to memory if we have done the first load
        syncWithMemory(adId, localAd, localCard, localUser);

        //Write the user
        if (!LocalUserWriter.writeUser(localUser,
                getCurrentUser().getUserId())) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the user !"));
            return futureSuccess;
        }

        //We do not need to check if the set contains it or not.
        this.userIds.add(localUser.getUserId());


        //Serializing
        if (!LocalAdWriter.writeAd(adId, localAd, localUser.getUserId(),
                cardId, getCurrentUser().getUserId())) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing the ad !"));
            return futureSuccess;
        }

        CompletableFuture<Void> futureWriteAdPhotos =
                LocalAdWriter.writeAdPhotos(adPhotos,
                        getCurrentUser().getUserId(), cardId);
        CompletableFuture<Void> futureWritePanoramasPhotos =
                LocalAdWriter.writePanoramas(panoramas,
                        getCurrentUser().getUserId(), cardId);
        CompletableFuture<Void> futureWriteProfilePic =
                LocalUserWriter.writeProfilePic(profilePic,
                        getCurrentUser().getUserId(), localUser.getUserId());
        CompletableFuture<Void> combinedFuture =
                CompletableFuture.allOf(futureWriteAdPhotos,
                        futureWritePanoramasPhotos, futureWriteProfilePic);
        combinedFuture.thenAccept(arg -> futureSuccess.complete(null));
        combinedFuture.exceptionally(e -> {
            e.printStackTrace();
            futureSuccess.completeExceptionally(e);
            return null;
        });
        return futureSuccess;
    }

    /**
     * Returns the list of cards, if it manages to find it either on memory
     * or on disk.
     *
     * @return a completable future containing the list of cards
     */
    public CompletableFuture<List<Card>> getCards() {
        return getFromMemory(() -> cards);
    }

    /**
     * Returns an ad with ad id.
     *
     * @param adId the id of the ad
     * @return a completable future containing the ad
     */
    public CompletableFuture<Ad> getAd(String adId) {
        return getFromMemory(() -> idsToAd.get(adId));
    }

    /**
     * Returns a user with the user id as its id.
     *
     * @param wantedUserID the user id
     * @return a completable future containing the user
     */
    public CompletableFuture<User> getUser(String wantedUserID) {
        return getFromMemory(() -> idsToUser.get(wantedUserID));
    }

    /**
     * This function is called every time we want to get something from the
     * local db. It checks if the data we have in memory is valid, if it is
     * it directly returns it. Otherwise, it loads everything on disk, which
     * happens asynchronously.
     *
     * @param returnFunc the function that performs the actual retrieving of
     *                   data from one of the data structures of the class.
     * @return a completable future containing the data the user wants
     */
    private <T> CompletableFuture<T> getFromMemory(Supplier<T> returnFunc) {
        if (this.firstLoad) {
            return CompletableFuture.completedFuture(returnFunc.get());
        }
        //TODO: I don't know why I have a threading issue here, this needs to be investigated
        //Basically we reach this point even though firstLoad is true
        clearMemory();

        CompletableFuture<Void> futureReadAd =
                LocalAdReader.readAdDataForAUser(getCurrentUser().getUserId()
                        , this.cards, this.idsToAd, this.adIdsToPanoramas);
        CompletableFuture<Void> futureReadUser =
                LocalUserReader.readUsers(getCurrentUser().getUserId(),
                        this.idsToUser, this.userIds);

        CompletableFuture<Void> combinedFuture =
                CompletableFuture.allOf(futureReadAd, futureReadUser);

        CompletableFuture<T> futureData = new CompletableFuture<>();
        combinedFuture.thenAccept(arg -> {
            futureData.complete(returnFunc.get());
            this.firstLoad = true;
        });
        combinedFuture.exceptionally(e -> {
            e.printStackTrace();
            futureData.completeExceptionally(e);
            return null;
        });
        return futureData;
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
        FileIO.deleteDirectory(favoritesDir);
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
        FileIO.deleteDirectory(new File(pathToCard));

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
            FileIO.deleteDirectory(new File(userPath));
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
     * Note that the writing of the profile picture happens asynchronously
     *
     * @param currentUser the current user
     * @param profilePic  the bitmap for the profile picture of the user.
     * @return a completable future that indicates if the operation succeeded
     * or not.
     */
    public CompletableFuture<Void> setCurrentUser(User currentUser,
                                                  Bitmap profilePic) {
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

        //Just to make sure we don't keep invalid data
        File oldProfilePic =
                new File(LocalDatabasePaths.currentUserProfilePicture());
        oldProfilePic.delete();

        File favoritesFolder = new File(LocalDatabasePaths.favoritesFolder());
        if (!favoritesFolder.exists()) {
            boolean success = favoritesFolder.mkdirs();
            if (!success) {
                futureSuccess.completeExceptionally(new LocalDatabaseException("Error while creating favorites folder !"));
                return futureSuccess;
            }
        }

        //We check for the old the user as the local one is the new one and
        // uses the default profile picture at this point
        CompletableFuture<Void> futureProfilePic;
        if (!currentUser.hasDefaultProfileImage()) {
            String profilePicPath =
                    LocalDatabasePaths.currentUserProfilePicture();
            currentLocalUser.setProfileImagePathAndName(profilePicPath);


            futureProfilePic =
                    LocalUserWriter.writeProfilePic(profilePic, profilePicPath);

        } else {
            futureProfilePic = CompletableFuture.completedFuture(null);
        }


        Map<String, Object> userMap =
                UserSerializer.serializeLocal(currentLocalUser);
        if (userMap == null) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while reading the user map !"));
            return futureSuccess;
        }
        if (!FileIO.writeMapObject(LocalDatabasePaths.currentUserData(),
                userMap)) {
            futureSuccess.completeExceptionally(new LocalDatabaseException(
                    "Error while writing current user !"));
            return futureSuccess;
        }

        //This is the last thing we need to check for the future
        futureProfilePic.thenAccept(arg -> futureSuccess.complete(null));
        futureProfilePic.exceptionally(e -> {
            futureSuccess.completeExceptionally(e);
            return null;
        });

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


        return UserSerializer.deserializeLocal(userMap);
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
    public CompletableFuture<List<String>> getPanoramasPaths(String adID) {
        return getFromMemory(() -> this.adIdsToPanoramas.get(adID));
    }
}
