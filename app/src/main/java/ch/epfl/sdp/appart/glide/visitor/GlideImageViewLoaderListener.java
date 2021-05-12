package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;

import ch.epfl.sdp.appart.database.FirestoreDatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;

public class GlideImageViewLoaderListener extends GlideVisitor implements GlideLoaderListenerVisitor {

    private final ImageView view;
    private final String imageReference;
    private final RequestListener<Drawable> listener;

    public GlideImageViewLoaderListener(Context context, ImageView view,
                                        String imageReference,
                                        RequestListener<Drawable> listener) {
        super(context);

        if (view == null) {
            throw new IllegalArgumentException("imageView cannot be null");
        }

        if (imageReference == null) {
            throw new IllegalArgumentException("imageReference cannot be null");
        }

        if (listener == null) {
            throw new IllegalArgumentException("listener can't be null");
        }

        this.view = view;
        this.imageReference = imageReference;
        this.listener = listener;
    }

    @Override
    public void visit(FirestoreDatabaseService database) {
        Glide.with(context)
                .load(database.getStorageReference(imageReference)).listener(this.listener)
                .into(view);
    }

    @Override
    public void visit(MockDatabaseService database) {
        Glide.with(context)
                .load(database.prependAndroidFilePath(imageReference)).listener(this.listener)
                .into(view);
    }
}
