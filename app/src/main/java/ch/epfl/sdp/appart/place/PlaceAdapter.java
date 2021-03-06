package ch.epfl.sdp.appart.place;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.sdp.appart.R;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final List<Pair<PlaceOfInterest, Float>> places;
    private final Context context;
    private static final int MAX_CHARACTER_PER_LINE = 15;

    public PlaceAdapter(Context context,
                        List<Pair<PlaceOfInterest, Float>> places) {
        if (places == null) throw new IllegalArgumentException();
        this.places = places;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        View adapterLayout =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.place_card_layout, parent, false);
        return new PlaceViewHolder(adapterLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder,
                                 int position) {
        Pair<PlaceOfInterest, Float> placeAndDistance = places.get(position);
        PlaceOfInterest place = placeAndDistance.first;

        String name = place.getName();
        int lines = (name.length() / MAX_CHARACTER_PER_LINE) + ((name.length() % MAX_CHARACTER_PER_LINE) != 0 ? 1 : 0);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lines; i++) {
            String sub =
                    name.substring(
                            i * MAX_CHARACTER_PER_LINE, Math.min((i + 1) * MAX_CHARACTER_PER_LINE,
                                    name.length())
                    );
            sb.append(sub);

            if (!Character.isSpaceChar(sub.charAt(sub.length() - 1)) && i != lines - 1) {
                sb.append("-");
            }

            if (i != lines - 1) {
                sb.append("\n");
            }
        }

        holder.placeNameTextView.setText(sb.toString());

        float distance = placeAndDistance.second;
        boolean overOneKilometer = distance >= 1000;

        String unit = overOneKilometer ? "km" : "m";
        String distanceStr = overOneKilometer ?
                Integer.toString(((int) distance / 1000)) :
                Integer.toString((int) distance);

        holder.placeDistanceTextView.setText(distanceStr + " " + unit);

        double rating = place.getRating();
        holder.star1.setVisibility(View.INVISIBLE);
        holder.star2.setVisibility(View.INVISIBLE);
        holder.star3.setVisibility(View.INVISIBLE);
        holder.star4.setVisibility(View.INVISIBLE);
        holder.star5.setVisibility(View.INVISIBLE);

        if (rating >= 1) {
            holder.star1.setVisibility(View.VISIBLE);
        }
        if (rating >= 2) {
            holder.star2.setVisibility(View.VISIBLE);
        }
        if (rating >= 3) {
            holder.star3.setVisibility(View.VISIBLE);
        }
        if (rating >= 4) {
            holder.star4.setVisibility(View.VISIBLE);
        }
        if (rating >= 5) {
            holder.star5.setVisibility(View.VISIBLE);
        }
        Bitmap bitmap = place.getBitmap();
        if (bitmap != null) {
            holder.image.setImageBitmap(bitmap);
        } else {
            holder.image.setVisibility(View.GONE);
        }

    }

    @Override
    public void onViewRecycled(@NonNull PlaceViewHolder holder) {
        super.onViewRecycled(holder);
        holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_launcher_background));
    }

    @Override
    public int getItemCount() {
        return this.places.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final TextView placeNameTextView;
        private final TextView placeDistanceTextView;
        private final ImageView star1;
        private final ImageView star2;
        private final ImageView star3;
        private final ImageView star4;
        private final ImageView star5;
        private final ImageView image;

        public PlaceViewHolder(View view) {
            super(view);
            placeNameTextView =
                    view.findViewById(R.id.place_name_card_textView);
            placeDistanceTextView = view.findViewById(R.id.place_card_distance);
            star1 = view.findViewById(R.id.place_card_star_1);
            star2 = view.findViewById(R.id.place_card_star_2);
            star3 = view.findViewById(R.id.place_card_star_3);
            star4 = view.findViewById(R.id.place_card_star_4);
            star5 = view.findViewById(R.id.place_card_star_5);
            image = view.findViewById(R.id.place_card_image_CardLayout_imageView);
        }
    }


}
