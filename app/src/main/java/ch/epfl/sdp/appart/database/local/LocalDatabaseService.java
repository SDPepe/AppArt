package ch.epfl.sdp.appart.database.local;

import android.graphics.Bitmap;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;

public interface LocalDatabaseService {

    /**
     * This method retrieves the current user. It will first try to retrieve
     * the current user memory if it there. If it is not it will try to load
     * the current user from disk. If yet again, the current user data is not
     * there, it throws {@link IllegalStateException}
     *
     * @return the current user if it manages to find one, throws
     * {@link IllegalStateException} otherwise.
     */
    User getCurrentUser() throws IllegalStateException;

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
    CompletableFuture<Void> writeCompleteAd(String adId,
                                                   String cardId, Ad ad,
                                                   User user,
                                                   List<Bitmap> adPhotos,
                                                   List<Bitmap> panoramas,
                                                   Bitmap profilePic);

    /**
     * Returns the list of cards, if it manages to find it either on memory
     * or on disk.
     *
     * @return a completable future containing the list of cards
     */
    CompletableFuture<List<Card>> getCards() throws IllegalStateException;

    /**
     * Returns an ad with ad id.
     *
     * @param adId the id of the ad
     * @return a completable future containing the ad
     */
    CompletableFuture<Ad> getAd(String adId);

    /**
     * Returns a user with the user id as its id.
     *
     * @param wantedUserID the user id
     * @return a completable future containing the user
     */
    CompletableFuture<User> getUser(String wantedUserID);

    /**
     * This completely removes the favorites folder and everything it
     * contains. Useful for testing or if we reached illegal state.
     */
    void cleanFavorites();

    /**
     * Removes a card with id cardId. The user associated with this card is
     * removed only if it isn't referenced by any other card.
     *
     * @param cardId the id of the card
     */
    void removeCard(String cardId);

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
    CompletableFuture<Void> setCurrentUser(User currentUser,
                                                  Bitmap profilePic);

    /**
     * This tries to retrieve the current user either from memory or from
     * disk. If the current user is not in memory, then it will read the data
     * on disk.
     *
     * @return the current user if it finds one, null otherwise
     */
    User loadCurrentUser();

    /**
     * This returns the list of panoramas paths for a specific ad.
     *
     * @param adID the id of the ad
     * @return the list of paths for the panoramas, or null if the operation
     * fails.
     */
    CompletableFuture<List<String>> getPanoramasPaths(String adID);
}
