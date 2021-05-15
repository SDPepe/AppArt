package ch.epfl.sdp.appart.hilt;

import android.content.Context;

import ch.epfl.sdp.appart.location.GoogleGeocodingService;
import ch.epfl.sdp.appart.place.GooglePlaceService;
import dagger.Binds;
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
        return new GooglePlaceService(new GoogleGeocodingService(context));
    }

}