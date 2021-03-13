package ch.epfl.sdp.appart.database;

import java.util.Map;

public interface QueryDocument {
    String getId();
    Map<String, Object> getData();
}
