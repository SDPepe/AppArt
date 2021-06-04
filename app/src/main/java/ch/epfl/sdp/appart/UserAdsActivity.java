package ch.epfl.sdp.appart;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import ch.epfl.sdp.appart.ad.UserAdsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@AndroidEntryPoint
public class UserAdsActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;
    private RecyclerView recyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_userAds);
        item.setVisible(false);
        invalidateOptionsMenu();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ads);

        Toolbar toolbar = findViewById(R.id.UserAds_toolbar);
        setSupportActionBar(toolbar);
        //toolbar.getMenu().removeItem(R.id.action_userAds);


        UserAdsViewModel mViewModel = new ViewModelProvider(this).get(UserAdsViewModel.class);

        mViewModel.initHome();

        recyclerView = findViewById(R.id.recycler_userAds);
        recyclerView.setAdapter(new CardAdapter(this, database, new ArrayList<>(), true));
        recyclerView.setHasFixedSize(true); //use for performance if card dims does not change
        mViewModel.getUserAds().observe(this, this::updateList);
    }

    /**
     * Update the list of cards.
     *
     * @param ls a list of card.
     */
    private void updateList(List<Card> ls) {
        recyclerView.setAdapter(new CardAdapter(this, database, ls, true));
    }
}