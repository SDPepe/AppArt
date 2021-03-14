package ch.epfl.sdp.appart.database.adapters;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

import ch.epfl.sdp.appart.database.QueryDocument;

/**
 * Class that adapt a document snapshot from firebase to a QueryDocument
 */
public class QueryDocumentSnapshotAdapter implements QueryDocument {

    private final QueryDocumentSnapshot queryDocumentSnapshot;

    public QueryDocumentSnapshotAdapter(QueryDocumentSnapshot queryDocumentSnapshot) {
        if (queryDocumentSnapshot == null) {
            throw new IllegalArgumentException("query documents snapshot cannot be null");
        }
        this.queryDocumentSnapshot = queryDocumentSnapshot;
    }

    @Override
    public String getId() {
        return queryDocumentSnapshot.getId();
    }

    @Override
    public Map<String, Object> getData() {
        return queryDocumentSnapshot.getData();
    }

}
