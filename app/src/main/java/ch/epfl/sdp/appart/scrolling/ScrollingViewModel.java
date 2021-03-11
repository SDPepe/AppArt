package ch.epfl.sdp.appart.scrolling;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.FirebaseDB;
import ch.epfl.sdp.appart.user.User;

public class ScrollingViewModel extends AndroidViewModel {

    private MutableLiveData<List<Card>> mCards = new MutableLiveData<>();
    ;
    private FirebaseDB db = new FirebaseDB();

    public ScrollingViewModel(@NonNull Application application) {
        super(application);
    }

    public void initHome() {

        db.getCards(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.isSuccessful()) {
                        List<Card> ls = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("0", document.getId() + " => " + document.getData());
                            ls.add(new Card(document.getId(), (String) document.getData().get("userId"),
                                    (String) document.getData().get("city"),
                                    (int) document.getData().get("price"),
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