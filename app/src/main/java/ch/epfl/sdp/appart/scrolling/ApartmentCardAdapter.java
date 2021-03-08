package ch.epfl.sdp.appart.scrolling;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.R;

/**
 * Based on the following tutorial : https://developer.android.com/codelabs/basic-android-kotlin-training-recyclerview-scrollable-list
 */
public class ApartmentCardAdapter extends RecyclerView.Adapter<ApartmentCardAdapter.CardViewHolder> {

    private List<ApartmentCard> cards;
    private Context context;

    public ApartmentCardAdapter(Context context, List<ApartmentCard> cards) {
        this.cards = new ArrayList<>();
        this.cards.addAll(cards);
        this.context = context;
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout /*R.layout.scrolling_card_layout*/, parent, false);

        return new CardViewHolder(adapterLayout);
    }

    /**
     * Replace the content of a view.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ApartmentCard card = cards.get(position);
        holder.cardImageView.setImageResource(card.getImageId());
        holder.addressTextView.setText(card.getCity());
        StringBuilder sb = new StringBuilder().append(card.getPrice()).append(".-/mo");
        holder.priceTextView.setText(sb.toString());
    }

    /**
     * Get the number of cards that can be shown.
     * @return
     */
    @Override
    public int getItemCount() {
        return cards.size();
    }

    protected class CardViewHolder extends RecyclerView.ViewHolder {
        //private View view;
        private TextView addressTextView;
        private TextView priceTextView;
        private ImageView cardImageView;

        public CardViewHolder(View view) {
            super(view);
            cardImageView = view.findViewById(R.id.card_image);
            addressTextView = view.findViewById(R.id.city_text_view);
            priceTextView = view.findViewById(R.id.price_text_view);
        }

    }

}
