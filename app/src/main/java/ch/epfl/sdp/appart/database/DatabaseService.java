package ch.epfl.sdp.appart.database;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.glide.visitor.DatabaseHostVisitor;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;

/**
 * Interface of the database service. This interface provides
 * services to query Cards, Users and Ads.
 */
public interface DatabaseService extends DatabaseHostVisitor {

    /**
     * Get the list of cards stored on the database.
     *
     * @return A future that will contains the cards if the future
     * completed successfully.
     * If the an error occurs the future will complete exceptionally
     * by holding a DatabaseServiceException.
     * The list of cards can be empty but cannot be null.
     */
    @NonNull
    CompletableFuture<List<Card>> getCards();

    @NonNull
    CompletableFuture<List<Card>> getCardsFilter(@NonNull String location);

    /**
     * Update the card given as argument in the database.
     *
     * @param card the Card that will be updated in the database
     * @return A future that wraps a Boolean. True if the card could
     * have been updated or false otherwise.
     * @throws IllegalArgumentException if card is null.
     */
    @NonNull
    CompletableFuture<Boolean> updateCard(@NonNull Card card);

    /**
     * Get the user with the related userId stored on the database.
     *
     * @return A future that will contains the requested user if it
     * was found in the database.
     * If the an error occurs the future will complete exceptionally
     * by holding a DatabaseServiceException if the user was not found
     * or if an error occurred.
     * The requested user cannot be null.
     * @throws IllegalArgumentException if userId is null.
     */
    @NonNull
    CompletableFuture<User> getUser(String userId);

    /**
     * Store the user given as argument in the database.
     *
     * @param user a User that will be stored
     * @return A future that wraps a boolean if it
     * the new id of the card stored on the database. If an error
     * occurs, the future will deliver false.
     * @throws IllegalArgumentException if card is null.
     */
    @NonNull
    CompletableFuture<Boolean> putUser(User user);

    /**
     * Update the user given as argument in the database.
     *
     * @param user the User that will be updated in the database
     * @return A future that wraps a Boolean. True if the user could
     * have been updated or false otherwise.
     * @throws IllegalArgumentException if user is null.
     */
    @NonNull
    CompletableFuture<Boolean> updateUser(User user);


    /**
     * Get the add related to the given CardId
     *
     * @param id a String that is the id of the card referencing the ad.
     * @return A CompletableFuture<Ad> that will hold the value of the Ad or
     * a DatabaseServiceException
     */
    @NonNull
    CompletableFuture<Ad> getAd(String id);

    /**
     * Stores the ad in the database
     *
     * @param ad      the ad to add to the database
     * @param uriList the list of images to add to the ad
     * @return a future that wraps the ID of the new ad
     */
    @NonNull
    CompletableFuture<String> putAd(Ad ad, List<Uri> uriList);

    /**
     * Upload a image
     *
     * @param uri  a Uri of the image.
     * @param imagePathAndName a String represent the complete path of the image
     * @return A CompletableFuture<Boolean> that wraps a boolean if it
     * stored the image on the database. If an error
     * occurs, the future will deliver false.
     */
    @NonNull
    CompletableFuture<Boolean> putImage(Uri uri, String imagePathAndName);

    /**
     * Delete an image
     *
     * @param imagePathAndName a String represent the complete path of the image.
     * @return A CompletableFuture<Boolean> that wraps a boolean returning true
     * if the image was correctly deleted from database, false otherwise.
     */
    @NonNull
    public CompletableFuture<Boolean> deleteImage(String imagePathAndName);




    CompletableFuture<Void> clearCache();

}
