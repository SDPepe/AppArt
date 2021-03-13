package ch.epfl.sdp.appart.database;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Iterator;
import java.util.List;


public class QuerySnapshotAdapter implements QueryResult {

    private final QuerySnapshot querySnapshot;

    public QuerySnapshotAdapter(QuerySnapshot querySnapshot) {
        if (querySnapshot == null) {
            throw new IllegalArgumentException("query snapshot cannot be null");
        }
        this.querySnapshot = querySnapshot;
    }

    @Override
    public boolean isEmpty() {
        return querySnapshot.isEmpty();
    }

    @Override
    public int size() {
        return querySnapshot.size();
    }

    private class QueryDocumentSnapshotIteratorAdapter implements Iterator<QueryDocument> {

        private final Iterator<QueryDocumentSnapshot> iterator;

        protected QueryDocumentSnapshotIteratorAdapter(Iterator<QueryDocumentSnapshot> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public QueryDocument next() {
            return new QueryDocumentSnapshotAdapter(iterator.next());
        }
    }

    @NonNull
    @Override
    public Iterator<QueryDocument> iterator() {
        return new QueryDocumentSnapshotIteratorAdapter(querySnapshot.iterator());
    }
}
