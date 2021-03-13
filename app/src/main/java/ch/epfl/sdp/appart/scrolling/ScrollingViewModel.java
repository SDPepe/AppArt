package ch.epfl.sdp.appart.scrolling;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.database.QueryDocument;
import ch.epfl.sdp.appart.database.QueryResult;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.FirebaseDB;
import ch.epfl.sdp.appart.user.User;
import dagger.hilt.InstallIn;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.HiltAndroidApp;
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

        db.getCards(new OnCompleteListener<QueryResult>() {
            @Override
            public void onComplete(@NonNull Task<QueryResult> task) {
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
    }

    /*
     * Getters for MutableLiveData instances declared above
     */
    public LiveData<List<Card>> getCards() {
        return mCards;
    }
}