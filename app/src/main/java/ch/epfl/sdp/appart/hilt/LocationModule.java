package ch.epfl.sdp.appart.hilt;

import android.content.Context;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;

import androidx.annotation.Nullable;
import androidx.core.os.ConfigurationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.location.AndroidLocationService;
import ch.epfl.sdp.appart.location.LocationService;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class LocationModule {

    @Singleton
    @Binds
    public abstract LocationService bindLocationService(AndroidLocationService locationService);

    @Singleton
    @Provides
    public static Context provideContext(@ApplicationContext Context context) {
        return context;
    }

}
