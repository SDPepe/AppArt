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

    private MutableLiveData<List<Card>> mCards = new MutableLiveData<>();

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
        queriedCards.thenAccept(cards -> {
            mCards.setValue(cards);
        });
        /*
        db.getCards(new OnCompleteListener<Query>() {
            @Override
            public void onComplete(@NonNull Task<Query> task) {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
                        List<Card> ls = new ArrayList<>();
                        for (QueryDocument document : task.getResult()) {
                            Log.d("0", document.getId() + " => " + document.getData());
                            ls.add(new Card(document.getId(), (String) document.getData().get("userId"),
                                    (String) document.getData().get("city"),
                                    (long) document.getData().get("price"),
                                    (String) document.getData().get("imageUrl")));
                        }
                        mCards.setValue(ls);
                    } else {
                        Log.w("1", "Error getting documents: ", task.getException());
                    }
                }
            }
        });
        */
    }

    /*
     * Getters for MutableLiveData instances declared above
     */
    public LiveData<List<Card>> getCards() {
        return mCards;
    }
}