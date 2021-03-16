package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ch.epfl.sdp.appart.database.FirebaseDB;
import ch.epfl.sdp.appart.database.MockDataBase;

public final class GlideDatabaseLoaderVisitorImpl implements GlideLoaderVisitor {

    private final ImageView view;
    private final String imageReference;
    private final Context context;

    public GlideDatabaseLoaderVisitorImpl(Context context, ImageView view, String imageReference) {
        this.context = context;
        this.view = view;
        this.imageReference = imageReference;
    }

    @Override
    public void visit(FirebaseDB database) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://appart-ec344.appspot.com/Cards/" + imageReference);
        Glide.with(context)
                .load(ref)
                .into(view);
    }

    @Override
    public void visit(MockDataBase database) {
        Glide.with(context)
                .load(imageReference)
                .into(view);
    }

}
