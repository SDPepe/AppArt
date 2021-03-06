package ch.epfl.sdp.appart.scrolling.card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.sdp.appart.ad.PricePeriod;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.AdActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.UserAdsActivity;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;

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
    private final boolean isUserAd;
    private final boolean isLocal;

    /**
     * Constructor of the card adapter.
     *
     * @param context  the parent activity
     * @param database the database where the card images are fetched
     * @param cards    list of Firebase Storage image references
     */
    public CardAdapter(Activity context, DatabaseService database, List<Card> cards, boolean isUserAd, boolean isLocal) {
        if (cards == null)
            throw new IllegalArgumentException("cards cannot be null");
        if (context == null)
            throw new IllegalArgumentException("context cannot be null");

        this.cards = cards;
        this.context = context;
        this.database = database;
        this.isUserAd = isUserAd;
        this.isLocal = isLocal;
    }

    /**
     * Constructor of the card adapter.
     *
     * @param context  the parent activity
     * @param database the database where the card images are fetched
     * @param cards    list of Firebase Storage image references
     */
    public CardAdapter(Activity context, DatabaseService database, List<Card> cards, boolean isUserAd) {
        this(context, database, cards, isUserAd, false);
    }

    public CardAdapter(Activity context, DatabaseService database, List<Card> cards) {
        this(context, database, cards, false);
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
            String activityName = ((Activity) context).getLocalClassName();
            Intent intent = new Intent(context, AdActivity.class);
            intent.putExtra(ActivityCommunicationLayout.PROVIDING_ACTIVITY_NAME, activityName);
            intent.putExtra(ActivityCommunicationLayout.PROVIDING_CARD_ID, card.getId());
            intent.putExtra(ActivityCommunicationLayout.PROVIDING_AD_ID, card.getAdId());
            context.startActivity(intent);
        });

        // load image from database into ImageView
        String sep = FirebaseLayout.SEPARATOR;
        if(isLocal) {
            Bitmap bitmap = BitmapFactory.decodeFile(card.getImageUrl());
            Glide.with(context).load(bitmap).into(holder.cardImageView);
        }
        else {
            database.accept(new GlideImageViewLoader(context, holder.cardImageView,
                    CardLayout.IMAGE_DIRECTORY + sep + card.getAdId() + sep + card.getImageUrl()));
        }
        holder.addressTextView.setText(card.getCity());
        String price;
        //TODO Remove if when finish clean up of the add
        if(card.getPricePeriod() != null) {
            price = card.getPrice() + ".- / " + card.getPricePeriod().toString();
            holder.priceTextView.setText(price);
        } else {
            price = card.getPrice() + ".- / " + "not set in database";
            holder.priceTextView.setText(price);
        }
        if (!card.hasVRTour())
            holder.vrAvailableImageView.setVisibility(View.GONE);
        else
            holder.vrAvailableImageView.setVisibility(View.VISIBLE);

        // Not the cleanest way of doing it, but it was difficult to do it differently with inheritance
        Log.d("delete_ads", "This card is a UserCardAdapter");
        if (isUserAd)
            holder.deleteButton.setOnClickListener(v -> {
                CompletableFuture<Boolean> deleteFuture = database.deleteAd(card.getAdId(), card.getId());
                deleteFuture.thenAccept(b -> {
                    if (b) {
                        Intent intent = new Intent(context, UserAdsActivity.class);
                        context.startActivity(intent);
                    }
                });
            });
        else
            holder.deleteButton.setVisibility(View.INVISIBLE);
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
        private final Button deleteButton;

        public CardViewHolder(View view) {
            super(view);
            cardImageView = view.findViewById(R.id.place_card_image_CardLayout_imageView);
            addressTextView = view.findViewById(R.id.place_name_card_textView);
            priceTextView = view.findViewById(R.id.place_price_CardLayout_textView);
            vrAvailableImageView = view.findViewById(R.id.vrAvailable_CardLayout_imageView);
            deleteButton = view.findViewById(R.id.card_delete_button);
        }

    }

}
