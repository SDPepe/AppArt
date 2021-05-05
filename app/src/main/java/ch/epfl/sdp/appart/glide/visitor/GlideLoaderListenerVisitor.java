package ch.epfl.sdp.appart.glide.visitor;

import ch.epfl.sdp.appart.database.FirestoreDatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;

public interface GlideLoaderListenerVisitor {
    /**
     * Visit the firebase database
     *
     * @param database the Firebase database visited
     */
    void visit(FirestoreDatabaseService database);

    /**
     * Visit the MockDatabase database
     *
     * @param database the MockDatabase database visited
     */
    void visit(MockDatabaseService database);
}
