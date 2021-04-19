package ch.epfl.sdp.appart.panorama;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.sdp.appart.R;

public class PanoramaPictureCardAdapter extends RecyclerView.Adapter<PanoramaPictureCardAdapter.CardViewHolder> {

    List<PanoramaPictureCard> cards;


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * ScrollingView tile
     */
    protected static class CardViewHolder extends RecyclerView.ViewHolder {

        private final TextView addressTextView;
        private final TextView priceTextView;
        private final ImageView cardImageView;
        private final ImageView vrAvailableImageView;

        public CardViewHolder(View view) {
            super(view);
            cardImageView = view.findViewById(R.id.image_CardLayout_imageView);
            addressTextView = view.findViewById(R.id.city_CardLayout_textView);
            priceTextView = view.findViewById(R.id.price_CardLayout_textView);
            vrAvailableImageView = view.findViewById(R.id.vrAvailable_CardLayout_imageView);
        }

    }

}
