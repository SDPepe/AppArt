package ch.epfl.sdp.appart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.AdViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import dagger.hilt.android.AndroidEntryPoint;
import kotlin.NotImplementedError;

/**
 * This class manages the UI of the ad.
 */
@AndroidEntryPoint
public class AdActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;

    @Inject
    LoginService login;

    private String advertiserId;

    private String adId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);
        AdViewModel mViewModel = new ViewModelProvider(this).get(AdViewModel.class);


        Toolbar toolbar = findViewById(R.id.account_Ad_toolbar);
        setSupportActionBar(toolbar);

        mViewModel.getTitle().observe(this, this::updateTitle);
        mViewModel.getPhotosRefs().observe(this, this::updatePhotos);
        mViewModel.getAddress().observe(this, this::updateAddress);
        mViewModel.getPrice().observe(this, this::updatePrice);
        mViewModel.getDescription().observe(this, this::updateDescription);
        mViewModel.getAdAdvertiserName().observe(this, this::updateAdvertiserName);
        mViewModel.getAdvertiserId().observe(this, this::updateAdvertiserId);

        adId = getIntent().getStringExtra(ActivityCommunicationLayout.PROVIDING_AD_ID);
        mViewModel.initAd(adId);
    }

    private void updateTitle(String title) {
        TextView titleView = findViewById(R.id.title_Ad_textView);
        setIfNotNull(titleView, title);
    }

    private void updatePhotos(List<String> references) {
        LinearLayout horizontalLayout = findViewById(R.id.horizontal_children_Ad_linearLayout);
        horizontalLayout.removeAllViews();

        for (int i = 0; i < references.size(); i++) {
            String sep = FirebaseLayout.SEPARATOR;
            String fullRef = FirebaseLayout.ADS_DIRECTORY + sep + adId + sep + references.get(i);
            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.photo_layout, (ViewGroup) null);
            ImageView photo = myView.findViewById(R.id.photo_Photo_imageView);
            database.accept(new GlideImageViewLoader(this, photo, fullRef));
            horizontalLayout.addView(myView);
            if (i != 4) {
                Space hspacer = new Space(this);
                hspacer.setLayoutParams(new ViewGroup.LayoutParams(
                        8,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
                horizontalLayout.addView(hspacer);
            }
            // open image fullscreen on tap
            myView.setOnClickListener(e -> openImageFullscreen(fullRef));
        }
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
        TextView descriptionView = findViewById(R.id.description_field_Ad_textView);
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
        intent.putExtra(ActivityCommunicationLayout.PROVIDING_USER_ID, this.advertiserId);
        startActivity(intent);
    }

    /**
     * Method called when you want open the virtual tour.
     *
     * @param view
     */
    public void openVirtualTour(View view) {
        Intent intent = new Intent(this, PanoramaActivity.class);
        startActivity(intent);
    }

    public void onSeeLocationClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        TextView addressView = findViewById(R.id.address_field_Ad_textView);
        intent.putExtra(getString(R.string.intentLocationForMap), addressView.getText().toString());
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
            addNewFavorite()
                    .exceptionally(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
                        return null;
                    })
                    .thenAccept(res ->
                            Toast.makeText(this, R.string.favSuccess_Ad, Toast.LENGTH_SHORT)
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
        database.getUser(user.getUserId())
                .exceptionally(e -> {
                    result.completeExceptionally(
                            new DatabaseServiceException(getString(R.string.favFail_Ad)));
                    return null;
                })
                .thenAccept(u -> saveFavorite(result, u));
        return result;
    }

    /**
     * Save new ad id in database and if successful save ad locally
     */
    private void saveFavorite(CompletableFuture<Void> result, User user){
        user.addFavorite(adId);
        database.updateUser(user)
                .exceptionally(e -> {
                    result.completeExceptionally(
                            new DatabaseServiceException(
                                    getString(R.string.favFail_Ad)));
                    return null;
                })
                // if update successful, save ad locally
                .thenAccept(res -> {
                    // TODO use antoine API to save ad locally, complete result accordingly
                    /*
                    .exceptionally(e -> {
                        result.completeExceptionally(new DatabaseServiceException(getString(R.string.favFail_Ad)));
                        return null; });
                    .thenAccept(res -> result.complete(null));
                     */
                });
    }
}