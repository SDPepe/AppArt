package ch.epfl.sdp.appart.scrolling;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.scrolling.card.Card;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ScrollingViewModel extends ViewModel {

    private final MutableLiveData<List<Card>> mCards = new MutableLiveData<>();

    Database db;

    @Inject
    public ScrollingViewModel(Database database) {
        this.db = database;
    }

    /*
     * Gets the cards from the database and updates the LiveData list
     */
    public void initHome() {

        CompletableFuture<List<Card>> queriedCards = db.getCards();
        queriedCards.thenAccept(mCards::setValue);

    }

    /*
     * Getters for MutableLiveData instances declared above
     */
    public LiveData<List<Card>> getCards() {
        return mCards;
    }
}