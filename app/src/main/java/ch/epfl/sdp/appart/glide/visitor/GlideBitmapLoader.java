package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

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
    private final String imagePath;

    public GlideBitmapLoader(Context context, CompletableFuture<Bitmap> bitmapFuture, String imagePath) {
        super(context);
        if (imagePath == null) throw new IllegalArgumentException("image path cannot be null");
        if (bitmapFuture == null) throw new IllegalArgumentException("future cannot be null");
        this.bitmapFuture = bitmapFuture;
        this.imagePath = imagePath;
    }

    @Override
    public void visit(FirestoreDatabaseService database) {
        /*
          WARNING : For simplicity we keep the loading on the drawable for now, will change.
         */
        BitmapTarget target = new BitmapTarget(bitmapFuture);
        Glide.with(context)
                .asBitmap()
                .load(database.getStorageReference(imagePath))
                .error(R.drawable.no_connection)
                .into(target);
    }

    @Override
    public void visit(MockDatabaseService database) {
        /*
          WARNING : For simplicity we keep the loading on the drawable for now, will change.
         */
        BitmapTarget target = new BitmapTarget(bitmapFuture);
        Glide.with(context)
                .asBitmap()
                .load(Uri.parse(database.prependAndroidFilePath("panorama_test.jpg")))
                .into(target);

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
            //PanoramaGl wants a bitmap with a max size, for now we scale it to 2048*2048
            Bitmap result = Bitmap.createScaledBitmap(resource, 2048, 2048, false);
            targetFuture.complete(result.copy(result.getConfig(), false));
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) { }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            targetFuture.completeExceptionally(new IllegalStateException("panorama bitmap loading failed"));
        }
        /**
         * Return the bitmap that was inserted in the target
         *
         * @return bitmap result
         */
        @Nullable
        protected CompletableFuture<Bitmap> getBitmap() {
            return targetFuture;
        }

    }
}
