package ch.epfl.sdp.appart.scroll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<ApartmentCard> cards;
    private Context context;

    public CardAdapter(Context context, List<ApartmentCard> cards) {
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
                .inflate(R.layout.scrolling_card_layout, parent, false);

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
        holder.textView.setText(context.getResources().getString(card.getResourceId())); //context.resources.getString(item.stringResourceId)
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
        private View view;
        private TextView textView;

        public CardViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.card_layout);
        }

    }

}
