package ch.epfl.sdp.appart.favorites;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for the Favorites activity.
 * <p>
 * At initialization it loads the favorites of the current logged in user. It takes care of checking
 * whether the device has a connection to internet. If it does, it takes the the favorites from the
 * server and updates the local db, otherwise it loads the favorites from the local db.
 */
@HiltViewModel
public class FavoriteViewModel extends ViewModel {

    private final MutableLiveData<Pair<List<Card>, Boolean>> lFavorites = new MutableLiveData<>();
    final DatabaseService database;
    final LoginService loginService;
    final LocalDatabaseService localdb;

    @Inject
    public FavoriteViewModel(DatabaseService database, LoginService loginService,
                             LocalDatabaseService localdb) {
        this.database = database;
        this.loginService = loginService;
        this.localdb = localdb;
    }

    /**
     * Initializes the list of favorites.
     * <p>
     * Loads favorites locally, then tries to fetch them from the server.
     */
    public CompletableFuture<Void> initHome() {
        CompletableFuture<Void> result = new CompletableFuture<>();
        localLoad().whenComplete((res, e) -> {
            CompletableFuture<Void> fetchRes = fetch();
            fetchRes.exceptionally(e1 -> {
                result.completeExceptionally(e1);
                return null;
            });
            fetchRes.thenAccept(r -> result.complete(null));
        });

        return result;
    }


    /**
     * Getter for the LiveData of the list of favorite cards
     */
    public MutableLiveData<Pair<List<Card>, Boolean>> getFavorites() {
        return lFavorites;
    }

    /**
     * Tries to load favorites from local database.
     */
    private CompletableFuture<Void> localLoad() {
        CompletableFuture<Void> result = new CompletableFuture<>();
        CompletableFuture<List<Card>> cardsRes = localdb.getCards();
        cardsRes.exceptionally(e -> {
            result.completeExceptionally(e);
            return null;
        });
        cardsRes.thenAccept(cards -> {
            new Handler(Looper.getMainLooper()).post(() -> lFavorites.setValue(new Pair(cards, true)));
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
                    lFavorites.setValue(new Pair<>(new ArrayList<>(), false));
                    return null;
                })
                .thenAccept(u -> {
                    CompletableFuture<List<Card>> cardsRes = database.getCards();
                    cardsRes.exceptionally(e -> {
                        Log.d("EXCEPTION_DB", e.getMessage());
                        result.completeExceptionally(e);
                        return null;
                    });
                    cardsRes.thenAccept(cs -> {
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
        lFavorites.setValue(new Pair<>(filteredCards, false));
    }

}
