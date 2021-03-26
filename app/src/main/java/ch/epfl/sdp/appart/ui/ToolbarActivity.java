package ch.epfl.sdp.appart.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.user.LoginActivity;
import ch.epfl.sdp.appart.user.UserProfileActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class ToolbarActivity extends AppCompatActivity {

        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.actions_toolbar, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_logout:
                    Intent intentLogout = new Intent(this, LoginActivity.class);
                    startActivity(intentLogout);
                    return true;

                case R.id.action_account:
                    Intent intentAccount = new Intent(this, UserProfileActivity.class);
                    startActivity(intentAccount);
                    return true;

                case R.id.action_settings:
                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);

            }
        }

}