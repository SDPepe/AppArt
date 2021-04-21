package ch.epfl.sdp.appart.panorama;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.sdp.appart.R;

public class PanoramaPictureCardAdapter extends RecyclerView.Adapter<PanoramaPictureCardAdapter.CardViewHolder> {

    List<PanoramaPictureCard> cards;
    private PanoramaPictureCard selectedCard;
    private static List<CardViewHolder> viewHolders;
    private static int selectedIndex;

    public PanoramaPictureCardAdapter() {
        cards = new ArrayList<>();
        viewHolders = new ArrayList<>();
        selectedIndex = -1;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.panorama_card_layout, parent, false);
        CardViewHolder holder = new PanoramaPictureCardAdapter.CardViewHolder(adapterLayout);
        viewHolders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        PanoramaPictureCard picture = cards.get(position);
        holder.sphericalImageView.setImageURI(picture.getImageUri());
        holder.cardIndexTextView.setText(Integer.toString(picture.getIndex()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CardViewHolder h : viewHolders) {
                    h.itemView.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.DARKEN);
                }
                selectedIndex = picture.getIndex() - 1;
                v.getBackground().setColorFilter(Color.parseColor("#6A9EF6"), PorterDuff.Mode.DARKEN);
                notifyDataSetChanged();
            }
        });
    }

    public void addPicture(Uri uri) {
        cards.add(new PanoramaPictureCard(uri, cards.size() + 1));
    }

    public List<Uri> getOrderedPicturesUris() {
        List<Uri> result = new ArrayList<>();
        for (PanoramaPictureCard card : cards) {
            result.add(card.getImageUri());
        }
        return result;
    }

    public void swap(int first, int second) {

        PanoramaPictureCard temp = cards.get(first);
        cards.set(first, cards.get(second));
        cards.set(second, temp);
        cards.get(first).setIndex(first + 1);
        cards.get(second).setIndex(second + 1);

        notifyDataSetChanged();
    }

    public void swapSelectedPicturesWithAbove() {
        if (selectedIndex > 0) {
            swap(selectedIndex, selectedIndex - 1);
            //selectedIndex = selectedIndex - 1;
        }
    }

    public void swapSelectedPictureWithBellow() {
        if (selectedIndex < getItemCount() - 1 && selectedIndex >= 0) {
            swap(selectedIndex, selectedIndex + 1);
            //selectedIndex = selectedIndex + 1;
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    /**
     * ScrollingView tile
     */
    protected static class CardViewHolder extends RecyclerView.ViewHolder {

        private final ImageView sphericalImageView;
        private final TextView cardIndexTextView;

        public CardViewHolder(View view) {
            super(view);
            sphericalImageView = view.findViewById(R.id.panoramaCardimageView_PanoramaCreation);
            cardIndexTextView = view.findViewById(R.id.panorama_card_index_textView);
        }

        void updateNumber() {
            cardIndexTextView.setText(Integer.toString(getLayoutPosition() + 1));
        }

    }

}
