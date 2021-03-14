package ch.epfl.sdp.appart.scrolling;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.database.QueryDocument;
import ch.epfl.sdp.appart.database.Query;
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