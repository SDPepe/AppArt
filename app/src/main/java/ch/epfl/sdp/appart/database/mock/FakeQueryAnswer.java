package ch.epfl.sdp.appart.database.mock;

import androidx.annotation.NonNull;

import java.util.Iterator;

import ch.epfl.sdp.appart.database.Query;
import ch.epfl.sdp.appart.database.QueryDocument;

public class FakeQueryAnswer implements Query {

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @NonNull
    @Override
    public Iterator<QueryDocument> iterator() {
        return null;
    }

}
