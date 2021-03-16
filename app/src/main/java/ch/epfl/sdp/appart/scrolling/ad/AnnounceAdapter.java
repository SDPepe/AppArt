package ch.epfl.sdp.appart.scrolling.ad;

import android.app.Activity;

import ch.epfl.sdp.appart.Database;

public class AnnounceAdapter {

    private final Activity parent;
    private final Database db;

    public AnnounceAdapter(Activity parent, Database database){
        if (parent == null){
            throw new IllegalArgumentException("parent view is null!");
        }
        if (database == null){
            throw new IllegalArgumentException("database is null!");
        }
        db = database;
        this.parent = parent;
        initAd();
    }

    private void initAd(){
        // TODO query db for ad info, modify parent view fields with answer
    }
}
