package ch.epfl.sdp.appart.scrolling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.card.ApartmentCard;
import ch.epfl.sdp.appart.scrolling.card.ApartmentCardAdapter;

import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private List<ApartmentCard> cardsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        cardsId = Arrays.asList(
                new ApartmentCard(R.drawable.apart_fake_image_1, "Lausanne", 1000),
                new ApartmentCard(R.drawable.apart_fake_image_2, "Ecublens", 1200),
                new ApartmentCard(R.drawable.apart_fake_image_3, "Renens", 800),
                new ApartmentCard(R.drawable.apart_fake_image_4, "Prilly", 1150),
                new ApartmentCard(R.drawable.apart_fake_image_5, "Lausanne", 900)
        );
        /*
        cardsId = Arrays.asList(
                new ApartmentCard(R.string.apart1),
                new ApartmentCard(R.string.apart2),
                new ApartmentCard(R.string.apart3),
                new ApartmentCard(R.string.apart4),
                new ApartmentCard(R.string.apart5),
                new ApartmentCard(R.string.apart6)
        );*/

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new ApartmentCardAdapter(this, cardsId));
        recyclerView.setHasFixedSize(true); //use for performance if card dims does not change

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Scroll", "stoping");
    }

}