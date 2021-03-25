package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ch.epfl.sdp.appart.database.FirebaseDB;
import ch.epfl.sdp.appart.database.MockDataBase;

/**
 * Implementation of the Visitor that helps to select the right reference
 * in function of the bound database.
 * This class is encapsulating the loading with Glide
 */
public final class GlideLoaderVisitorImpl implements GlideLoaderVisitor {

    private final ImageView view;
    private final String imageReference;
    private final Context context;

    public GlideLoaderVisitorImpl(Context context, ImageView view, String imageReference) {

        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        if (view == null) {
            throw new IllegalArgumentException("imageView cannot be null");
        }

        if (imageReference == null) {
            throw new IllegalArgumentException("imageReference cannot be null");
        }

        this.context = context;
        this.view = view;
        this.imageReference = imageReference;
    }

    @Override
    public void visit(FirebaseDB database) {
        Glide.with(context)
                .load(database.getStorageReference(imageReference))
                .into(view);
    }

    @Override
    public void visit(MockDataBase database) {
        Glide.with(context)
                .load(imageReference)
                .into(view);
    }

}
