package ch.epfl.sdp.appart.favorites;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlin.NotImplementedError;

/**
 * ViewModel for the Favorites activity.
 * <p>
 * At initialization it loads the favorites of the current logged in user. It takes care of checking
 * whether the device has a connection to internet. If it does, it takes the the favorites from the
 * server and updates the local db, otherwise it loads the favorites from the local db.
 */
@HiltViewModel
public class FavoriteViewModel extends ViewModel {

    private final MutableLiveData<List<Card>> lFavorites = new MutableLiveData<>();
    final DatabaseService database;
    final LoginService loginService;
    final LocalDatabaseService localdb;

    @Inject
    public FavoriteViewModel(DatabaseService database, LoginService loginService, LocalDatabaseService localdb) {
        this.database = database;
        this.loginService = loginService;
        this.localdb = localdb;
    }

    /**
     * Initializes the list of favorites.
     * <p>
     * It follows the android app architecture guidelines on exposing network status:
     * https://developer.android.com/jetpack/guide#addendum
     * It first loads from the local db, then fetches from the server (skipping the check whether
     * the fetch is necessary) and if the fetch is successful it updates the content with the
     * fetched info and updates the local db.
     * If any task fails, it returns a future completed exceptionally where the exception message is
     * the string to show to the user.
     */
    public CompletableFuture<Void> initHome() {
        CompletableFuture<Void> result = new CompletableFuture<>();

        // load from local db
        localLoad()
                // after local load fetch from databaseservice and update content
                // try to fetch even if local load fails
                .whenComplete((e, res) ->
                        fetch()
                                .exceptionally(e1 -> {
                                    result.completeExceptionally(e1);
                                    return null;
                                })
                                .thenAccept(res1 -> {
                                    result.complete(null);
                                })
                );

        return result;
    }


    /**
     * Getter for the LiveData of the list of favorite cards
     */
    public MutableLiveData<List<Card>> getFavorites() {
        return lFavorites;
    }

    /**
     * Loads content from local database
     */
    private CompletableFuture<Void> localLoad() {
        CompletableFuture<Void> result = new CompletableFuture<>();
        localdb.getCards()
                .exceptionally(e -> {
                    result.completeExceptionally(e);
                    return null;
                })
                .thenAccept(cards -> {
                    lFavorites.setValue(cards);
                    result.complete(null);
                });
        return result;
    }

    /**
     * Fetches info from the DatabaseService and if successful updates the local database
     */
    private CompletableFuture<Void> fetch() {
        CompletableFuture<Void> result = new CompletableFuture<>();

        // if any exception, complete exceptionally.
        // if good results, from user get favorites, then get all cards and filter keeping favorites
        // only, set favorites values and update local db
        database.getUser(loginService.getCurrentUser().getUserId())
                .exceptionally(e -> {
                    Log.d("EXCEPTION_DB", e.getMessage());
                    result.completeExceptionally(e);
                    return null;
                })
                .thenAccept(u -> {
                    database.getCards()
                            .exceptionally(e -> {
                                Log.d("EXCEPTION_DB", e.getMessage());
                                result.completeExceptionally(e);
                                return null;
                            })
                            .thenAccept(cs -> {
                                filter(u, cs);
                                result.complete(null);
                            });
                });

        return result;
    }

    /**
     * Filters cards with user favorites, then sets values and updates the local database
     */
    private void filter(User user, List<Card> cards) {
        Set<String> favoritesIds = user.getFavoritesIds();
        List<Card> filteredCards = new LinkedList<>();
        for (Card c : cards) {
            if (favoritesIds.contains(c.getAdId()))
                filteredCards.add(c);
        }
        lFavorites.setValue(filteredCards);
    }

}
