package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.panoramagl.PLImage;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.FirebaseDB;
import ch.epfl.sdp.appart.database.MockDataBase;

public class GlideBitmapLoaderImpl extends GlideVisitorBase implements GlideBitmapGetterVisitor {

    GlideBitmapLoaderImpl(Context context, String imagePath) {
        super(context);
        if (imagePath == null) throw new IllegalArgumentException("image path cannot be null");
    }

    /**
     * Container class to get the bitmap
     */
    private class BitmapTarget extends CustomTarget<Bitmap> {

        private Bitmap bitmap;

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            bitmap = resource;
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {}

        /**
         * Return the bitmap that was inserted in the target
         * @return bitmap result
         */
        @Nullable
        protected Bitmap getBitmap() {
            return bitmap;
        }
    }

    @Override
    public Bitmap visit(FirebaseDB database) {

        BitmapTarget target = new BitmapTarget();
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.panorama_test)
                .into(target);

        return target.getBitmap();
    }

    @Override
    public Bitmap visit(MockDataBase database) {
        BitmapTarget target = new BitmapTarget();
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.panorama_test)
                .into(target);

        return target.getBitmap();
    }
}
