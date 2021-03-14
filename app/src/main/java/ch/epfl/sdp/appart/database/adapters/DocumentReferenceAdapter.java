package ch.epfl.sdp.appart.database.adapters;

import com.google.firebase.firestore.DocumentReference;

import ch.epfl.sdp.appart.database.Document;

public class DocumentReferenceAdapter implements Document {

    private final DocumentReference documentReference;

    public DocumentReferenceAdapter(DocumentReference documentReference) {
        if (documentReference == null) {
            throw new IllegalArgumentException("document reference cannot be null");
        }
        this.documentReference = documentReference;
    }

    @Override
    public String getId() {
        return documentReference.getId();
    }
}
