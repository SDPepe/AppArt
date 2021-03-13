package ch.epfl.sdp.appart.database;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public interface QueryResult extends Iterable<QueryDocument> {
    boolean isEmpty();
    int size();
}
