package ch.epfl.sdp.appart.glide.visitor;

import ch.epfl.sdp.appart.database.FirebaseDB;
import ch.epfl.sdp.appart.database.MockDataBase;

public interface GlideLoaderVisitor {
    void visit(FirebaseDB database);
    void visit(MockDataBase database);
}
