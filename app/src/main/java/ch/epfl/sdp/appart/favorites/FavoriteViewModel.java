package ch.epfl.sdp.appart.favorites;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
                CompletableFuture<List<Card>> cards = database.getCards();
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
    }


    /**
     * Getter for the LiveData of the list of favorite cards
     */
    public MutableLiveData<List<Card>> getFavorites() {
        return lFavorites;
    }
}
