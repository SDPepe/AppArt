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

import java.util.List;
import java.util.Locale;

import ch.epfl.sdp.appart.AdActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;

/**
 * Adapter converting an apartment card into a CardViewHolder that will be given to the RecyclerView
 * used by the ScrollingActivity.
 * <p>
 * Based on the following tutorial :
 * https://developer.android.com/codelabs/basic-android-kotlin-training-recyclerview-scrollable-list
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<Card> cards;
    private final Context context;
    private final DatabaseService database;

    /**
     * Constructor of the card adapter.
     *
     * @param context  the parent activity
     * @param database the database where the card images are fetched
     * @param cards    list of Firebase Storage image references
     */
    public CardAdapter(Activity context, DatabaseService database, List<Card> cards) {
        if (cards == null)
            throw new IllegalArgumentException("cards cannot be null");
        if (context == null)
            throw new IllegalArgumentException("context cannot be null");

        this.cards = cards;
        this.context = context;
        this.database = database;
    }

    /**
     * Create a new CardViewHolder based on the layout of a card.
     *
     * @param parent   the View that will contain the ViewHolder
     * @param viewType unknown
     * @return the newly created CardViewHolder
     */
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);

        return new CardViewHolder(adapterLayout);
    }

    /**
     * Replace the content of a view according to the card stored at position.
     *
     * @param holder   the CardViewHolder to overwrite.
     * @param position the index of the card which will overwrite the CardViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdActivity.class);
            intent.putExtra("fromAdCreation", false);
            intent.putExtra("adID", card.getId());
            context.startActivity(intent);
        });

        // load image from database into ImageView
        database.accept(new GlideImageViewLoader(context, holder.cardImageView,
                "Cards/" + card.getImageUrl()));
        holder.addressTextView.setText(card.getCity());
        holder.priceTextView.setText(String.format("%d.-/mo", card.getPrice()));
        if (!card.hasVRTour())
            holder.vrAvailableImageView.setVisibility(View.INVISIBLE);
    }

    /**
     * Get the number of available cards
     *
     * @return the number of cards
     */
    @Override
    public int getItemCount() {
        return cards.size();
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
