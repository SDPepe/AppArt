package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.FirestoreDatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;

/**
 * This class visit the bound database and allows to fetch a Bitmap using Glide.
 * WARNING NOTE : We might implement doing a copy of the bitmap since glide absolutely
 * wants to recycle them all on its own and so the caller cannot keep a reference to the bitmap.
 * This would cause memory overhead but less chances of bugs to happen. Will discuss with the team
 */
public class GlideBitmapLoader extends GlideVisitor implements GlideBitmapLoaderVisitor {

    private final CompletableFuture<Bitmap> bitmapFuture;

    public GlideBitmapLoader(Context context, CompletableFuture<Bitmap> bitmapFuture, String imagePath) {
        super(context);
        if (imagePath == null) throw new IllegalArgumentException("image path cannot be null");
        if (bitmapFuture == null) throw new IllegalArgumentException("future cannot be null");
        this.bitmapFuture = bitmapFuture;
    }

    /**
     * Container class that will complete the future with the bitmap when it will be loaded
     */
    private static class BitmapTarget extends CustomTarget<Bitmap> {

        private final CompletableFuture<Bitmap> targetFuture;

        protected BitmapTarget(CompletableFuture<Bitmap> targetFuture) {
            if (targetFuture == null) throw new IllegalArgumentException("future cannot be null");
            this.targetFuture = targetFuture;
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            targetFuture.complete(resource);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {}

        /**
         * Return the bitmap that was inserted in the target
         * @return bitmap result
         */
        @Nullable
        protected CompletableFuture<Bitmap> getBitmap() {
            return targetFuture;
        }

    }

    @Override
    public void visit(FirestoreDatabaseService database) {
        /**
         * WARNING : For simplicity we keep the loading on the drawable for now, will change
         * next week.
         */
        BitmapTarget target = new BitmapTarget(bitmapFuture);
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.panorama_test)
                .into(target);
    }

    @Override
    public void visit(MockDatabaseService database) {
        /**
         * WARNING : For simplicity we keep the loading on the drawable for now, will change
         * next week.
         */
        BitmapTarget target = new BitmapTarget(bitmapFuture);
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.panorama_test)
                .into(target);

    }
}
