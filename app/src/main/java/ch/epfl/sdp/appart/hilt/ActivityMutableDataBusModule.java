package ch.epfl.sdp.appart.hilt;

import android.content.Context;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.databus.ActivityMutableDataBus;
import ch.epfl.sdp.appart.hilt.annotations.StringMutableDataBus;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ActivityMutableDataBusModule {

    @StringMutableDataBus
    @Singleton
    @Provides
    public static ActivityMutableDataBus<String> provideStringMutableDataBus(@ApplicationContext Context context) {
        return new ActivityMutableDataBus<>();
    }

}
