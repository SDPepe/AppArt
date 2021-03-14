package ch.epfl.sdp.appart.database.mock;

import java.util.Map;

import ch.epfl.sdp.appart.database.QueryDocument;

public class FakeQueryDocument implements QueryDocument {


    @Override
    public String getId() {
        return null;
    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }

}
