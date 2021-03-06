package ch.epfl.sdp.appart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.AdViewModel;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import ch.epfl.sdp.appart.utils.DatabaseSync;
import dagger.hilt.android.AndroidEntryPoint;

import static android.widget.Toast.makeText;

/**
 * This class manages the UI of the ad.
 */
@AndroidEntryPoint
public class AdActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;
    @Inject
    LoginService login;

    public static class Intents {
        public static final String INTENT_PANORAMA_PICTURES =
                "panoramas_pictures_references";
        public static final String INTENT_AD_ID = "adId";
    }


    private String advertiserId;
    private Pair<ArrayList<String>, Boolean> panoramasReferences;

    private String adId;
    private AdViewModel mViewModel;

    private Button panoramaButton;
    private Button contactButton;
    private Button seeLocationButton;
    private Button seeNearbyPlacesButton;
    private MenuItem addFavoriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);

        addFavoriteItem = findViewById(R.id.action_add_favorite);

        /**
         * Retrieve buttons
         */
        panoramaButton = findViewById(R.id.vtour_Ad_button);
        contactButton = findViewById(R.id.contact_info_Ad_button);
        seeLocationButton = findViewById(R.id.seeLocation_Ad_button);
        seeNearbyPlacesButton = findViewById(R.id.seeNearbyPlaces_Ad_button);

        panoramaButton.setOnClickListener(this::noInformationOnClickListener);
        contactButton.setOnClickListener(this::noInformationOnClickListener);
        seeLocationButton.setOnClickListener(this::noInformationOnClickListener);
        seeNearbyPlacesButton.setOnClickListener(this::noInformationOnClickListener);


        panoramasReferences = new Pair(new ArrayList<>(), false);
        mViewModel = new ViewModelProvider(this).get(AdViewModel.class);

        Toolbar toolbar = findViewById(R.id.account_Ad_toolbar);
        setSupportActionBar(toolbar);

        panoramaButton = findViewById(R.id.vtour_Ad_button);
        panoramaButton.setVisibility(View.INVISIBLE);

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

        mViewModel.getHasVTour().observe(this, this::updateHasVTourButton);
        mViewModel.getHasLoaded().observe(this, this::setNormalClickListeners);

        adId = getIntent().getStringExtra(ActivityCommunicationLayout.PROVIDING_AD_ID);
        // init content, show a toast if load failed
        mViewModel.initAd(adId)
                .exceptionally(e -> {
                    Log.d("AD", "Failed to fetch data from server");
                    return null;
                });

        //updateBookmarkIconState();
    }

    public void noInformationOnClickListener(View view) {
        Snackbar.make(view, R.string.buttonInfoMessageAdNotLoaded,
                BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    private void setNormalClickListeners(Boolean hasLoaded) {
        panoramaButton.setOnClickListener(this::openVirtualTour);
        contactButton.setOnClickListener(this::openContactInfo);
        seeLocationButton.setOnClickListener(this::onSeeLocationClick);
        seeNearbyPlacesButton.setOnClickListener(this::onNearbyPlacesClick);
    }

    private void updateTitle(String title) {
        TextView titleView = findViewById(R.id.title_Ad_textView);
        setIfNotNull(titleView, title);
    }

    private void updateHasVTourButton(Boolean hasVTour) {
        if (hasVTour) {
            panoramaButton.setVisibility(View.VISIBLE);
        }
    }

    private void updatePhotos(Pair<List<String>, Boolean> referencesAndIsLocal) {
        LinearLayout horizontalLayout =
                findViewById(R.id.horizontal_children_Ad_linearLayout);
        horizontalLayout.removeAllViews();
        List<String> references = referencesAndIsLocal.first;
        boolean isLocal = referencesAndIsLocal.second;
        for (int i = 0; i < references.size(); i++) {
            String sep = FirebaseLayout.SEPARATOR;
            LayoutInflater inflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.photo_layout,
                    (ViewGroup) null);
            ImageView photo = myView.findViewById(R.id.photo_Photo_imageView);
            String fullRef;
            if (isLocal) {
                fullRef = references.get(i);
                Bitmap bitmap = BitmapFactory.decodeFile(fullRef);
                Glide.with(this).load(bitmap)
                        .into(photo);
            } else {
                fullRef =
                        FirebaseLayout.ADS_DIRECTORY + sep + adId + sep + references.get(i);
                database.accept(new GlideImageViewLoader(this, photo, fullRef));
            }
            horizontalLayout.addView(myView);

            // open image fullscreen on tap
            myView.setOnClickListener(e -> openImageFullscreen(fullRef,
                    isLocal));
        }
    }

    private void updatePanoramasReferences(Pair<List<String>, Boolean> references) {
        panoramasReferences.first.clear();
        panoramasReferences.first.addAll(references.first);
        panoramasReferences = new Pair<>(panoramasReferences.first,
                references.second);
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
                panoramasReferences.first);
        intent.putExtra(Intents.INTENT_AD_ID, adId);
        intent.putExtra("isLocalExtra", panoramasReferences.second);
        startActivity(intent);
    }

    /**
     * Opens the location activity showing the position of this ad
     *
     * @param view the view that we never use in this calls
     */
    public void onSeeLocationClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(getString(R.string.intentLocationForMap),
                mViewModel.getAddress().getValue());
        startActivity(intent);
    }

    private void openImageFullscreen(String imageId, boolean isLocal) {
        Intent intent = new Intent(this, FullScreenImageActivity.class);
        intent.putExtra("imageId", imageId);
        intent.putExtra("isLocalExtra", isLocal);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ad_toolbar, menu);
        addFavoriteItem = menu.getItem(0);
        updateBookmarkIconState();
        return true;
    }

    /**
     * Adds ad to favorites, shows a toast with the action result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_favorite) {
            if (!DatabaseSync.areWeOnline(this)) {
                makeText(this, "You can't add or remove favorites while " +
                        "offline !", Toast.LENGTH_SHORT).show();
                return true;
            }

            CompletableFuture<Boolean> addRes = addNewFavorite();
            addRes.exceptionally(e -> {
                Log.d("AD", "Failed to add to favorites");
                return null;
            });
            addRes.thenAccept(res -> {
                        setBookmarkIcon(!res);
                    }
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check if user is logged in and add new favorite ad to user
     */
    private CompletableFuture<Boolean> addNewFavorite() {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        // check that the user is logged in
        User user = login.getCurrentUser();
        if (user == null) {
            result.completeExceptionally(new IllegalStateException("User has " +
                    "to be logged in"));
            return result;
        }
        CompletableFuture<User> userRes = database.getUser(user.getUserId());
        userRes.exceptionally(e -> {
            Log.d("AD", "Failed to get user");
            result.completeExceptionally(e);
            return null;
        });
        userRes.thenAccept(u -> saveFavorite(result, u));
        return result;
    }

    /**
     * Adds the ad id to the list of favorites of the user and update the
     * user in the server.
     */
    private void saveFavorite(CompletableFuture<Boolean> result, User user) {
        boolean alreadyAdded = user.getFavoritesIds().contains(adId);
        CompletableFuture<List<Card>> futureCards = database.getCards();
        CompletableFuture<Card> futureLocalUpdate;
        if (alreadyAdded) {
            futureLocalUpdate = futureCards.thenApply(cards -> {
                Card card = getMatchingCard(cards);
                if (card == null) {
                    return card;
                }
                localdb.removeCard(card.getId());
                user.removeFavorite(adId);
                return card;
            });
        } else {
            futureLocalUpdate = futureCards.thenApply(cards -> {
                Card card = getMatchingCard(cards);
                if (card == null) {
                    return card;
                }
                DatabaseSync.writeAd(database, card, this, localdb);
                user.addFavorite(adId);
                return card;
            });
        }
        CompletableFuture<Boolean> updateRes =
                futureLocalUpdate.thenCompose(card -> database.updateUser(user).exceptionally(e -> {
                    if (alreadyAdded) {
                        user.addFavorite(adId);
                    } else {
                        user.removeFavorite(adId);
                    }
                    return null;
                }));
        updateRes.exceptionally(e -> {
            Log.d("AD", "failed to update user");
            result.completeExceptionally(e);
            return null;
        });
        updateRes.thenAccept(res -> {
            Log.d("AD", "user updated");
            localdb.setCurrentUser(user, BitmapFactory.decodeFile(user.getProfileImagePathAndName()));
            result.complete(alreadyAdded);
        });
    }

    private Card getMatchingCard(List<Card> cards) {
        for (Card card : cards) {
            if (card.getAdId().equals(adId)) {
                return card;
            }
        }
        return null;
    }

    public void onNearbyPlacesClick(View view) {
        Intent intent = new Intent(this, PlaceActivity.class);
        intent.putExtra(ActivityCommunicationLayout.AD_ADDRESS,
                mViewModel.getAddress().getValue());
        startActivity(intent);
    }

    private void updateBookmarkIconState() {


        User currentUser = login.getCurrentUser();
        if (currentUser == null) {
            currentUser = localdb.getCurrentUser();
        }


         /*
                This is really slow if we don't check for connectivity.
          */
        if (DatabaseSync.areWeOnline(this)) {
            User finalCurrentUser = currentUser;
            database.getUser(currentUser.getUserId()).thenAccept(user -> {
                setBookmarkIcon(user.getFavoritesIds().contains(adId));
            }).exceptionally(e -> {
                e.printStackTrace();
                updateBookmarkLocalDatabase(finalCurrentUser);
                return null;
            });
        } else {
            updateBookmarkLocalDatabase(currentUser);
        }
    }

    private void updateBookmarkLocalDatabase(User currentUser) {
        setBookmarkIcon(currentUser.getFavoritesIds().contains(adId));

    }

    private void setBookmarkIcon(boolean filled) {
        runOnUiThread(() -> {
            if (filled) {
                addFavoriteItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bookmark_filled, null));
            } else {
                addFavoriteItem.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bookmark, null));
            }
        });
    }

}