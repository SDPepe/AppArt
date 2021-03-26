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
import java.util.concurrent.ExecutionException;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.FirebaseDB;
import ch.epfl.sdp.appart.database.MockDataBase;

public class GlideBitmapLoaderImpl extends GlideVisitorBase implements GlideBitmapLoaderVisitor {

    private final CompletableFuture<Bitmap> bitmapFuture;

    public GlideBitmapLoaderImpl(Context context, CompletableFuture<Bitmap> bitmapFuture, String imagePath) {
        super(context);
        if (imagePath == null) throw new IllegalArgumentException("image path cannot be null");
        if (bitmapFuture == null) throw new IllegalArgumentException("future cannot be null");
        this.bitmapFuture = bitmapFuture;
    }

    /**
     * Container class that will complete the future with the right bitmap
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
    public void visit(FirebaseDB database) {

        BitmapTarget target = new BitmapTarget(bitmapFuture);
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.panorama_test)
                .into(target);
    }

    @Override
    public void visit(MockDataBase database) {
        BitmapTarget target = new BitmapTarget(bitmapFuture);
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.panorama_test)
                .into(target);

    }
}
