package ch.epfl.sdp.appart.hilt;

import android.content.Context;
import android.net.Uri;


import java.util.List;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.databus.DataBus;
import ch.epfl.sdp.appart.hilt.annotations.IntegerDataBus;
import ch.epfl.sdp.appart.hilt.annotations.StringDataBus;
import ch.epfl.sdp.appart.hilt.annotations.UriListDataBus;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ActivityBusModule {

    @IntegerDataBus
    @Singleton
    @Provides
    public static DataBus<Integer> provideDataTransferService(@ApplicationContext Context context) {
        return new DataBus<>();
    }

    @StringDataBus
    @Singleton
    @Provides
    public static DataBus<String> provideOtherDataTransferService(@ApplicationContext Context context) {
        return new DataBus<>();
    }

    @UriListDataBus
    @Singleton
    @Provides
    public static DataBus<List<Uri>> provideUriList(@ApplicationContext Context context) {
        return new DataBus<>();
    }

}
