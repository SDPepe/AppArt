package ch.epfl.sdp.appart.hilt;

import android.content.Context;

import ch.epfl.sdp.appart.location.GoogleGeocodingService;
import ch.epfl.sdp.appart.place.GooglePlaceService;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityScoped;

@Module
@InstallIn(ActivityComponent.class)
public class GeocoderModule {

    @ActivityScoped
    @Provides
    public GoogleGeocodingService provideGeocodingService(@ApplicationContext Context context) {
        return new GoogleGeocodingService(context);
    }

}