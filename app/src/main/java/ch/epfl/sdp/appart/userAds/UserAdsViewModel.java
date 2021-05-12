package ch.epfl.sdp.appart.userAds;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserAdsViewModel extends ViewModel {
    private final MutableLiveData<List<Card>> lUserAds = new MutableLiveData<>();
    final DatabaseService database;
    final LoginService loginService;

    @Inject
    public UserAdsViewModel(DatabaseService database, LoginService loginService) {
        this.database = database;
        this.loginService = loginService;
    }

    public void initHome() {
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
                List<String> userAdsIds = u.getAdsIds();
                List<Card> filteredCards = new LinkedList<>();
                for (Card c : cs) {
                    if (userAdsIds.contains(c.getAdId()))
                        filteredCards.add(c);
                }
                lUserAds.setValue(filteredCards);
            });
        });
    }


    /**
     * Getter for the LiveData of the list of favorite cards
     */
    public MutableLiveData<List<Card>> getUserAds() {
        return lUserAds;
    }
}
