package ch.epfl.sdp.appart.hilt.databus;

import android.content.Context;
import android.net.Uri;

import java.util.List;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.databus.DataBus;
import ch.epfl.sdp.appart.databus.PrivateDataBus;
import ch.epfl.sdp.appart.hilt.databus.annotations.IntegerDataBus;
import ch.epfl.sdp.appart.hilt.databus.annotations.StringDataBus;
import ch.epfl.sdp.appart.hilt.databus.annotations.UriListDataBus;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class PrivateDataBusModule {

    @IntegerDataBus
    @Singleton
    @Provides
    public static PrivateDataBus<Integer> provideIntegerDataBus(@ApplicationContext Context context) {
        return new PrivateDataBus<>();
    }

    @StringDataBus
    @Singleton
    @Provides
    public static PrivateDataBus<String> provideStringDataBus(@ApplicationContext Context context) {
        return new PrivateDataBus<>();
    }

    @UriListDataBus
    @Singleton
    @Provides
    public static PrivateDataBus<List<Uri>> provideUriListDataBus(@ApplicationContext Context context) {
        return new PrivateDataBus<>();
    }

}
