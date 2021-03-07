package ch.epfl.sdp.appart.scroll;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.sdp.appart.R;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private List<ApartmentCard> cardsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        cardsId = Arrays.asList(
                new ApartmentCard(R.string.apart1),
                new ApartmentCard(R.string.apart2),
                new ApartmentCard(R.string.apart3),
                new ApartmentCard(R.string.apart4),
                new ApartmentCard(R.string.apart5),
                new ApartmentCard(R.string.apart6)
        );

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new CardAdapter(this, cardsId));
        recyclerView.setHasFixedSize(true); //use for performance if card dims does not change

    }
}