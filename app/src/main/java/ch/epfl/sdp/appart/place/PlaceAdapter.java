package ch.epfl.sdp.appart.place;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.sdp.appart.R;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final List<Pair<PlaceOfInterest, Float>> places;


    public PlaceAdapter(List<Pair<PlaceOfInterest, Float>> places) {
        if (places == null) throw new IllegalArgumentException();
        this.places = places;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        View adapterLayout =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.place_layout, parent, false);
        return new PlaceViewHolder(adapterLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder,
                                 int position) {
        Pair<PlaceOfInterest, Float> place = places.get(position);
        holder.placeNameTextView.setText(place.first.getName());
        holder.placeDistanceTextView.setText(place.second.toString());
    }

    @Override
    public int getItemCount() {
        return this.places.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final TextView placeNameTextView;
        private final TextView placeDistanceTextView;

        public PlaceViewHolder(View view) {
            super(view);
            this.placeNameTextView =
                    view.findViewById(R.id.placeName_Place_textView);
            this.placeDistanceTextView =
                    view.findViewById(R.id.placeDistance_Place_textView);
        }
    }


}
