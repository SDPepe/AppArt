package ch.epfl.sdp.appart.hilt;

import android.content.Context;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.location.AndroidLocationService;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.location.geocoding.GeocodingService;
import ch.epfl.sdp.appart.location.geocoding.GoogleGeocodingService;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class GeocoderModule {

    @Singleton
    @Binds
    public abstract GeocodingService bindGeocodingService(GoogleGeocodingService geocodingService);

}