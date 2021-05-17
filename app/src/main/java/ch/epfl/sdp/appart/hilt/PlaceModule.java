package ch.epfl.sdp.appart.hilt;

import android.content.Context;

import ch.epfl.sdp.appart.location.geocoding.GoogleGeocodingService;
import ch.epfl.sdp.appart.place.GooglePlaceService;
import ch.epfl.sdp.appart.place.helper.HttpGooglePlaceHelper;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityScoped;

@Module
@InstallIn(ActivityComponent.class)
public class PlaceModule {

    @ActivityScoped
    @Provides
    public GooglePlaceService providePlaceService(@ApplicationContext Context context) {
        return new GooglePlaceService(new HttpGooglePlaceHelper(context), new GoogleGeocodingService(context));
    }

}