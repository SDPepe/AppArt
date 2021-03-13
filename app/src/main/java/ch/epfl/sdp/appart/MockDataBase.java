package ch.epfl.sdp.appart;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;

import ch.epfl.sdp.appart.database.QueryResult;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class MockDataBase implements Database {

    @Override
    public void getCards(OnCompleteListener<QueryResult> callback) {
        /*
        callback.onComplete(new Task<QuerySnapshot>() {
            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Nullable
            @Override
            public QuerySnapshot getResult() {
                return null;
            }

            @Nullable
            @Override
            public <X extends Throwable> QuerySnapshot getResult(@NonNull Class<X> aClass) throws X {
                throw new UnsupportedOperationException("unimplemented function");
            }

            @Nullable
            @Override
            public Exception getException() {
                throw new UnsupportedOperationException("unimplemented function");
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnSuccessListener(@NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
                return null;
            }

            @NonNull
            @Override
            public Task<QuerySnapshot> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
                return null;
            }
        });
         */
    }

    @Override
    public void putCard(Card card, OnCompleteListener<DocumentReference> callback) {

    }

    @Override
    public void updateCard(Card card, OnCompleteListener<Void> callback) {

    }

}
