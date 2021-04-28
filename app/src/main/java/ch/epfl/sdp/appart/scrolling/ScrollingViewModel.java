package ch.epfl.sdp.appart.scrolling;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for the scrolling activity.
 */
@HiltViewModel
public class ScrollingViewModel extends ViewModel {

    private final MutableLiveData<List<Card>> mCards = new MutableLiveData<>();
    private final MutableLiveData<List<Card>> mCardsFilter = new MutableLiveData<>();

    final DatabaseService db;

    @Inject
    public ScrollingViewModel(DatabaseService database) {
        this.db = database;
    }

    /**
     * Gets the cards from the database and updates the LiveData list
     */
    public void initHome() {
        CompletableFuture<List<Card>> queriedCards = db.getCards();
        queriedCards.thenAccept(mCards::setValue);
    }

    public void filter(String location){
        if(location != null && !location.equals("")) {
            CompletableFuture<List<Card>> queriedCards = db.getCardsFilter(location);
            queriedCards.thenAccept(mCardsFilter::setValue);
            queriedCards.exceptionally(e -> {
                initHome();
                return null;});
        } else {
            initHome();
        }

    }

    /**
     * Getter for the LiveData of the list of cards
     */
    public LiveData<List<Card>> getCards() {
        return mCards;
    }

    public LiveData<List<Card>> getCardsFilter() {
        return mCardsFilter;
    }
}