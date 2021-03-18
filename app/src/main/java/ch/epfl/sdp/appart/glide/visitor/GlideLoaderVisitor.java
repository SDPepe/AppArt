package ch.epfl.sdp.appart.glide.visitor;

import ch.epfl.sdp.appart.database.FirebaseDB;
import ch.epfl.sdp.appart.database.MockDataBase;

/**
 * Visitor for the Glide loader
 */
public interface GlideLoaderVisitor {
    /**
     * Visit the firebase database
     * @param database the Firebase database visited
     */
    void visit(FirebaseDB database);

    /**
     * Visit the MockDatabase database
     * @param database the MockDatabase database visited
     */
    void visit(MockDataBase database);
}
