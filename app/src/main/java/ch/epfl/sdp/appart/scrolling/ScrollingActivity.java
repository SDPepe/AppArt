package ch.epfl.sdp.appart.scrolling;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import ch.epfl.sdp.appart.Card;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.card.ApartmentCardAdapter;
import ch.epfl.sdp.appart.user.AppUser;
import ch.epfl.sdp.appart.user.User;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        User u = new AppUser("dummy", "dummy", "dummy", "dummy");

        List<Card> cardsId = Arrays.asList(
                new Card(null, u, "Lausanne", 1000, "apart_fake_image_1.jpeg"),
                new Card(null, u, "Ecublens", 1200, "apart_fake_image_2.jpeg"),
                new Card(null, u, "Renens", 900, "apart_fake_image_3.jpeg"),
                new Card(null, u, "Prilly", 1150, "apart_fake_image_4.jpeg"),
                new Card(null, u, "Lausanne", 1200, "apart_fake_image_5.jpeg")
        );



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