package ch.epfl.sdp.appart.scrolling.card;


import android.app.Activity;
import android.view.View;
import android.widget.Button;

import java.util.List;

import androidx.annotation.NonNull;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.database.DatabaseService;

/**
 * Adapter converting a card created by the current user into a CardViewHolder for the UserAdsActivity
 * Main difference with CardAdapter is the presence of two buttons, to edit and delete the ad.
 */
public class UserCardAdapter extends CardAdapter {
    List<Card> cards;

    public UserCardAdapter(Activity context, DatabaseService databaseService, List<Card> cards) {
        super(context, databaseService, cards, true);

        this.cards = cards;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }
}
