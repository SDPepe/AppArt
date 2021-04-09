package ch.epfl.sdp.appart.glide.visitor;

import ch.epfl.sdp.appart.database.FirestoreDatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;

public interface GlideBitmapLoaderVisitor {

    /**
     * Visit FirebaseDB for the GlideLoaderVisitor
     *
     * @param database the databse that will be visited
     */
    void visit(FirestoreDatabaseService database);

    /**
     * Visit MockDatabase for the GlideLoaderVisitor
     *
     * @param database the database that will be visited
     */
    void visit(MockDatabaseService database);

}
