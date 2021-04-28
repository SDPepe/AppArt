package ch.epfl.sdp.appart.hilt;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.map.GoogleMapService;
import ch.epfl.sdp.appart.map.MapService;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class MapModule {

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MapActivityProvider {
    }

    @Singleton
    @Binds
    public abstract MapService bindMapService(GoogleMapService mapService);
}
