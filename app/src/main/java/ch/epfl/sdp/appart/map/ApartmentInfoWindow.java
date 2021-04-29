package ch.epfl.sdp.appart.map;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoaderListener;
import ch.epfl.sdp.appart.scrolling.card.Card;

public class ApartmentInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final Activity activity;
    private final DatabaseService databaseService;

    public ApartmentInfoWindow(Activity activity,
                               DatabaseService databaseService) {
        if (activity == null || databaseService == null)
            throw new IllegalArgumentException();

        this.activity = activity;
        this.databaseService = databaseService;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = activity.getLayoutInflater().inflate(R.layout.infowindow,
                null);

        TextView cityTextView =
                v.findViewById(R.id.city_InfoWindow_textView);
        TextView priceTextView =
                v.findViewById(R.id.price_InfoWindow_textView);

        ImageView photo = v.findViewById(R.id.photo_InfoWindow_imageView);

        Card card = (Card) marker.getTag();

        if (card.getCity() != null) {
            cityTextView.setText(card.getCity());
        }

        priceTextView.setText(card.getPrice() + " CHF");
        if (card.getImageUrl() != null) {
            databaseService.accept(new GlideImageViewLoaderListener(activity,
                    photo, /*"Cards/" + */card.getImageUrl(),
                    new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            e.printStackTrace();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource,
                                                       Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            if (!dataSource.equals(DataSource.MEMORY_CACHE))
                                marker.showInfoWindow();
                            return false;
                        }
                    }));
        }

        marker.setTitle(card.getCity() + " CLICKED");

        return v;
    }
}
