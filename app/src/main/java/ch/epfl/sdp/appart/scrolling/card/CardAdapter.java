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

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import ch.epfl.sdp.appart.Database;
import ch.epfl.sdp.appart.FirebaseDB;
import ch.epfl.sdp.appart.MockDataBase;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.ad.AnnounceActivity;

import static java.lang.String.format;

/**
 * Adapter converting an apartment card into a CardViewHolder that will be given to the RecyclerView
 * Based on the following tutorial : https://developer.android.com/codelabs/basic-android-kotlin-training-recyclerview-scrollable-list
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<Card> cards;
    private final Context context;

    private final Database database;

    public CardAdapter(Activity context, Database database, List<Card> cards) {

        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }

        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

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
     * @param holder   The CardViewHolder to be overwrite.
     * @param position The index of the card which will overwrite the CardViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnnounceActivity.class);
            context.startActivity(intent);
        });

        //dirty hack : to be fixed
        if (database instanceof FirebaseDB) {
            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://appart-ec344.appspot.com/Cards/" + card.getImageUrl());
            Glide.with(context)
                    .load(ref)
                    .into(holder.cardImageView);
        } else if (database instanceof MockDataBase) {
            Glide.with(context)
                    .load(card.getImageUrl())
                    .into(holder.cardImageView);
        } else {
            throw new UnsupportedOperationException("card viewer found not implemented backend");
        }


        holder.addressTextView.setText(card.getCity());
        holder.priceTextView.setText(format("%d.-/mo", card.getPrice()));
    }

    /**
     * Get the number of cards available
     *
     * @return the number of cards
     */
    @Override
    public int getItemCount() {
        return cards.size();
    }

    protected static class CardViewHolder extends RecyclerView.ViewHolder {

        private final TextView addressTextView;
        private final TextView priceTextView;
        private final ImageView cardImageView;

        public CardViewHolder(View view) {
            super(view);
            cardImageView = view.findViewById(R.id.card_image);
            addressTextView = view.findViewById(R.id.city_text_view);
            priceTextView = view.findViewById(R.id.price_text_view);
        }

    }

}
