package ch.epfl.sdp.appart;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.database.Document;
import ch.epfl.sdp.appart.database.Query;
import ch.epfl.sdp.appart.scrolling.card.Card;

public interface Database {

    CompletableFuture<List<Card>> getCards();

    CompletableFuture<String> putCard(Card card);

    CompletableFuture<Void> updateCard(Card card);

}
