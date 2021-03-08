package ch.epfl.sdp.appart.scrolling.card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.AnnounceActivity;
import ch.epfl.sdp.appart.scrolling.ScrollingActivity;

/**
 * Based on the following tutorial : https://developer.android.com/codelabs/basic-android-kotlin-training-recyclerview-scrollable-list
 */
public class ApartmentCardAdapter extends RecyclerView.Adapter<ApartmentCardAdapter.CardViewHolder> {

    private final List<ApartmentCard> cards;
    private final Context context;

    public ApartmentCardAdapter(Activity context, List<ApartmentCard> cards) {

        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }

        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

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
        holder.cardImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, AnnounceActivity.class);
                context.startActivity(intent);
            }
        });
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
