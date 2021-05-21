package ch.epfl.sdp.appart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.AdViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoader;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import ch.epfl.sdp.appart.utils.StoragePathBuilder;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI of the ad.
 */
@AndroidEntryPoint
public class AdActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;

    @Inject
    LoginService login;

    @Inject
    LocalDatabaseService localdb;

    public static class Intents {
        public static final String INTENT_PANORAMA_PICTURES =
                "panoramas_pictures_references";
        public static final String INTENT_AD_ID = "adId";
    }

    private String advertiserId;
    private ArrayList<String> panoramasReferences = new ArrayList<>();
    private AdViewModel mViewModel;

    private String adId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);
        mViewModel = new ViewModelProvider(this).get(AdViewModel.class);


        Toolbar toolbar = findViewById(R.id.account_Ad_toolbar);
        setSupportActionBar(toolbar);

        mViewModel.getTitle().observe(this, this::updateTitle);
        mViewModel.getPhotosRefs().observe(this, this::updatePhotos);
        mViewModel.getAddress().observe(this, this::updateAddress);
        mViewModel.getPrice().observe(this, this::updatePrice);
        mViewModel.getDescription().observe(this, this::updateDescription);
        mViewModel.getAdAdvertiserName().observe(this,
                this::updateAdvertiserName);
        mViewModel.getAdvertiserId().observe(this, this::updateAdvertiserId);
        mViewModel.observePanoramasReferences(this,
                this::updatePanoramasReferences);

        adId = getIntent().getStringExtra(ActivityCommunicationLayout.PROVIDING_AD_ID);
        // init content, show a toast if load failed
        mViewModel.initAd(adId)
                .exceptionally(e -> {
                    Toast.makeText(this, R.string.loadFail_Ad,
                            Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

    private void updateTitle(String title) {
        TextView titleView = findViewById(R.id.title_Ad_textView);
        setIfNotNull(titleView, title);
    }

    private void updatePhotos(List<String> references) {
        LinearLayout horizontalLayout =
                findViewById(R.id.horizontal_children_Ad_linearLayout);
        horizontalLayout.removeAllViews();

        for (int i = 0; i < references.size(); i++) {
            String sep = FirebaseLayout.SEPARATOR;
            String fullRef =
                    FirebaseLayout.ADS_DIRECTORY + sep + adId + sep + references.get(i);
            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.photo_layout,
                    (ViewGroup) null);
            ImageView photo = myView.findViewById(R.id.photo_Photo_imageView);
            database.accept(new GlideImageViewLoader(this, photo, fullRef));
            horizontalLayout.addView(myView);

            // open image fullscreen on tap
            myView.setOnClickListener(e -> openImageFullscreen(fullRef));
        }
    }

    private void updatePanoramasReferences(List<String> references) {
        panoramasReferences.clear();
        panoramasReferences.addAll(references);
    }

    private void updateAddress(String address) {
        TextView addressView = findViewById(R.id.address_field_Ad_textView);
        setIfNotNull(addressView, address);
    }

    private void updatePrice(String price) {
        TextView priceView = findViewById(R.id.price_field_Ad_textView);
        setIfNotNull(priceView, price);
    }

    private void updateDescription(String description) {
        TextView descriptionView =
                findViewById(R.id.description_field_Ad_textView);
        setIfNotNull(descriptionView, description);
    }

    private void updateAdvertiserName(String username) {
        TextView usernameView = findViewById(R.id.user_field_Ad_textView);
        setIfNotNull(usernameView, username);
    }

    private void updateAdvertiserId(String advertiserId) {
        this.advertiserId = advertiserId;
    }

    private void setIfNotNull(TextView view, String content) {
        if (content == null) {
            view.setText(R.string.loadingTextAdActivity);
        } else {
            view.setText(content);
        }
    }

    /**
     * Method called when you want open the contact info.
     *
     * @param view
     */
    public void openContactInfo(View view) {
        Intent intent = new Intent(this, SimpleUserProfileActivity.class);
        intent.putExtra(ActivityCommunicationLayout.PROVIDING_USER_ID,
                this.advertiserId);
        startActivity(intent);
    }

    /**
     * Method called when you want open the virtual tour.
     *
     * @param view the view that we never use in this calls
     */
    public void openVirtualTour(View view) {
        Intent intent = new Intent(this, PanoramaActivity.class);
        intent.putStringArrayListExtra(Intents.INTENT_PANORAMA_PICTURES,
                panoramasReferences);
        intent.putExtra(Intents.INTENT_AD_ID, adId);
        startActivity(intent);
    }

    /**
     * Opens the location activity showing the position of this ad
     *
     * @param view the view that we never use in this calls
     */
    public void onSeeLocationClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        TextView addressView = findViewById(R.id.address_field_Ad_textView);
        intent.putExtra(getString(R.string.intentLocationForMap),
                addressView.getText().toString());
        startActivity(intent);
    }

    private void openImageFullscreen(String imageId) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("imageId", imageId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ad_toolbar, menu);
        return true;
    }

    /**
     * Adds ad to favorites, shows a toast with the action result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_favorite) {
            CompletableFuture<Void> addRes = addNewFavorite();
            addRes.exceptionally(e -> {
                Toast.makeText(this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                return null;
            });
            addRes.thenAccept(res ->
                    Toast.makeText(this, R.string.favSuccess_Ad,
                            Toast.LENGTH_SHORT).show()
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check if user is logged in and add new favorite ad to user
     */
    private CompletableFuture<Void> addNewFavorite() {
        CompletableFuture<Void> result = new CompletableFuture<>();
        // check that the user is logged in
        User user = login.getCurrentUser();
        if (user == null) {
            result.completeExceptionally(
                    new UnsupportedOperationException(getString(R.string.userNotLoggedIn_Ad))
            );
            return result;
        }
        CompletableFuture<User> userRes = database.getUser(user.getUserId());
        userRes.exceptionally(e -> {
            result.completeExceptionally(
                    new DatabaseServiceException(getString(R.string.favFail_Ad)));
            return null;
        });
        userRes.thenAccept(u -> saveFavorite(result, u));
        return result;
    }

    /**
     * Adds the ad id to the list of favorites of the user and update the user in the server.
     */
    private void saveFavorite(CompletableFuture<Void> result, User user) {
        user.addFavorite(adId);
        CompletableFuture<Boolean> updateRes = database.updateUser(user);
        updateRes.exceptionally(e -> {
            result.completeExceptionally(
                    new DatabaseServiceException(
                            getString(R.string.favFail_Ad)));
            return null;
        });
        updateRes.thenAccept(res -> result.complete(null));
    }

}