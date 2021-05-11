package ch.epfl.sdp.appart.hilt;

import android.content.Context;
import android.location.Geocoder;

import androidx.core.os.ConfigurationCompat;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.configuration.ApplicationConfiguration;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppConfigurationModule {
    @Singleton
    @Provides
    public static ApplicationConfiguration provideAntoine(@ApplicationContext Context context) {
        return new ApplicationConfiguration();
    }
}
