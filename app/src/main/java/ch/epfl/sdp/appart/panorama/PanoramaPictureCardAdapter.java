package ch.epfl.sdp.appart.panorama;

import android.net.Uri;
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

public class PanoramaPictureCardAdapter extends RecyclerView.Adapter<PanoramaPictureCardAdapter.CardViewHolder> {

    List<PanoramaPictureCard> cards;
    private PanoramaPictureCard selectedCard;

    public PanoramaPictureCardAdapter() {
        cards = new ArrayList<>();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.panorama_card_layout, parent, false);

        return new PanoramaPictureCardAdapter.CardViewHolder(adapterLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        PanoramaPictureCard picture = cards.get(position);
        holder.sphericalImageView.setImageURI(picture.getImageUri());
        holder.cardIndexTextView.setText(Integer.toString(picture.getIndex()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCard = cards.get(position);
            }
        });
    }

    public void addPicture(Uri uri) {
        cards.add(new PanoramaPictureCard(uri, cards.size() + 1));
    }

    public void swap(int first, int second) {
        PanoramaPictureCard temp = cards.get(first);
        cards.set(first, cards.get(second));
        cards.set(second, temp);
    }

    public void swapPicturesWithAbove(int index) {
        if (index > 0) {
            swap(index, index - 1);
        }
    }

    public void swapPicturesWithBellow(int index) {
        if (index < getItemCount() - 1) {
            swap(index, index + 1);
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

    }

}
