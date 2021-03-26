package ch.epfl.sdp.appart.glide.visitor;

import android.graphics.Bitmap;

import ch.epfl.sdp.appart.database.FirebaseDB;
import ch.epfl.sdp.appart.database.MockDataBase;

public interface GlideBitmapLoaderVisitor {

    /**
     * Visit FirebaseDB for the GlideLoaderVisitor
     * @param database
     */
    void visit(FirebaseDB database);

    /**
     * Visit MockDatabase for the GlideLoaderVisitor
     * @param database
     */
    void visit(MockDataBase database);

}
