package ch.epfl.sdp.appart.favorites;

import android.util.Log;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.FirestoreDatabaseService;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.lifecycle.HiltViewModel;

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

    public void initHome() {
            database.getUser(loginService.getCurrentUser().getUserId()).thenAccept(u -> {
                Log.d("favorites", "number of favorites : " + u.getFavoritesIds().size());
                CompletableFuture<List<Card>> cards = database.getCards();
                cards.thenAccept(cs -> {
                    List<Card> filteredCards = cs.stream().filter(c -> u.getFavoritesIds().contains(c.getAdId())).collect(Collectors.toList());
                    for (Card c: filteredCards)
                        Log.d("favorites", c.getAdId() + " passed the filter");
                    Log.d("favorites", "" + filteredCards.size());
                    lFavorites.setValue(filteredCards);
                });
            });
    }


    /**
     * Getter for the LiveData of the list of favorite cards
     */
    public LiveData<List<Card>> getFavorites() {
        return lFavorites;
    }
}
