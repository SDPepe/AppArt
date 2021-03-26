package ch.epfl.sdp.appart.scrolling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.Database;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import ch.epfl.sdp.appart.ui.ToolbarActivity;
import ch.epfl.sdp.appart.user.LoginActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ScrollingActivity extends ToolbarActivity {

    @Inject
    Database database;
    private ScrollingViewModel mViewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        mViewModel = new ViewModelProvider(this).get(ScrollingViewModel.class);

        mViewModel.initHome();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new CardAdapter(this, database, new ArrayList<>()));
        recyclerView.setHasFixedSize(true); //use for performance if card dims does not change
        mViewModel.getCards().observe(this, this::updateList);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_account:
                return true;

            case R.id.action_settings:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Scroll", "stopping");
    }

    private void updateList(List<Card> ls) {
        recyclerView.setAdapter(new CardAdapter(this, database, ls));
    }



}