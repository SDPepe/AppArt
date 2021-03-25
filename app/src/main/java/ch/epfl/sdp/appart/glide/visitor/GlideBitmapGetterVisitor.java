package ch.epfl.sdp.appart.glide.visitor;

import android.graphics.Bitmap;

import ch.epfl.sdp.appart.database.FirebaseDB;
import ch.epfl.sdp.appart.database.MockDataBase;

public interface GlideBitmapGetterVisitor {

    Bitmap visit(FirebaseDB database);

    Bitmap visit(MockDataBase database);

}
