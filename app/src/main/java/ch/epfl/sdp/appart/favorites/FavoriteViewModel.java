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

import ch.epfl.sdp.appart.database.DatabaseService;
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

    @Inject
    public FavoriteViewModel(DatabaseService database, LoginService loginService) {
        this.database = database;
        this.loginService = loginService;
    }

    /**
     * Initializes the list of favorites.
     * <p>
     * It follows the android app architecture guidelines on exposing network status:
     * https://developer.android.com/jetpack/guide#addendum
     * It first loads from the local db, then fetches from the server (skipping the check whether
     * the fetch is necessary) and if the fetch is successful it updates the content with the
     * fetched info and updates the local db.
     */
    public CompletableFuture<Void> initHome() {
        // load from local db

        // fetch from databaseservice and update content

        CompletableFuture<User> user = database.getUser(loginService.getCurrentUser().getUserId());
        user.exceptionally(e -> {
            Log.d("EXCEPTION_DB", e.getMessage());
            return null;
        });
        user.thenAccept(u -> {
            CompletableFuture<List<Card>> cards = database.getCards();
            cards.exceptionally(e -> {
                Log.d("EXCEPTION_DB", e.getMessage());
                return null;
            });
            cards.thenAccept(cs -> {
                Set<String> favoritesIds = u.getFavoritesIds();
                List<Card> filteredCards = new LinkedList<>();
                for (Card c : cs) {
                    if (favoritesIds.contains(c.getAdId()))
                        filteredCards.add(c);
                }
                lFavorites.setValue(filteredCards);
            });
        });

        return new CompletableFuture<>();
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
    private CompletableFuture<Void> localLoad(){
        throw new NotImplementedError();
    }

    /**
     * Fetches info from the DatabaseService and if successful updates the local database
     * @return
     */
    private CompletableFuture<Void> fetchAndUpdate() {
        throw new NotImplementedError();
    }
}
