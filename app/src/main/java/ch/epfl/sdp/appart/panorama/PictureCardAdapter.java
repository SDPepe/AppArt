package ch.epfl.sdp.appart.panorama;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.R;

public class PictureCardAdapter extends RecyclerView.Adapter<PictureCardAdapter.CardViewHolder>
    implements SwapNotifiable {

    List<PictureCard> cards;
    private PictureCard selectedCard;
    private static List<CardViewHolder> viewHolders;
    private static int selectedIndex;

    public PictureCardAdapter() {
        cards = new ArrayList<>();
        viewHolders = new ArrayList<>();
        selectedIndex = -1;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.panorama_card_layout, parent, false);
        CardViewHolder holder = new PictureCardAdapter.CardViewHolder(adapterLayout, this);
        viewHolders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        PictureCard picture = cards.get(position);
        holder.bindPictureCard(picture);
        holder.sphericalImageView.setImageURI(picture.getImageUri());
        holder.cardIndexTextView.setText(Integer.toString(picture.getIndex()));
        /*
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
         */
    }

    public void addPicture(Uri uri) {
        cards.add(new PictureCard(uri, cards.size() + 1));
    }

    public List<Uri> getOrderedPicturesUris() {
        List<Uri> result = new ArrayList<>();
        for (PictureCard card : cards) {
            result.add(card.getImageUri());
        }
        return result;
    }

    public void swap(int first, int second) {

        PictureCard temp = cards.get(first);
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
    public void swapFromIndexWithAbove(int index) {
        if (index > 0) {
            swap(index, index - 1);
        }
    }

    @Override
    public void swapFromIndexWithBellow(int index) {
        if (index < getItemCount() - 1 && index >= 0) {
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
        private final Button upButton;
        private final Button downButton;
        private PictureCard pictureCard;

        public CardViewHolder(View view, SwapNotifiable swapper) {
            super(view);
            sphericalImageView = view.findViewById(R.id.panoramaCardimageView_PanoramaCreation);
            cardIndexTextView = view.findViewById(R.id.panorama_card_index_textView);
            upButton = view.findViewById(R.id.up_button_card_PanoramaCreation);
            downButton = view.findViewById(R.id.down_button_card_PanoramaCreation);
            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pictureCard == null) {
                        throw new IllegalStateException("picture card cannot be null !");
                    }
                    swapper.swapFromIndexWithAbove(pictureCard.getIndex() - 1);
                }
            });
            sphericalImageView.setTranslationZ(-1);
            upButton.bringToFront();
            upButton.setTranslationZ(-11.0f);
            downButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pictureCard == null) {
                        throw new IllegalStateException("picture card cannot be null !");
                    }
                    swapper.swapFromIndexWithBellow(pictureCard.getIndex() - 1);
                }
            });
            downButton.bringToFront();
            downButton.setTranslationZ(11.0f);
            pictureCard = null;
        }

        void bindPictureCard(PictureCard pictureCard) {
            this.pictureCard = pictureCard;
        }

    }

}
