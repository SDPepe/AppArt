package ch.epfl.sdp.appart.hilt;

import android.content.Context;
import android.net.Uri;


import java.util.List;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.datapass.GenericTransfer;
import ch.epfl.sdp.appart.hilt.annotations.IntegerDataTransferProvider;
import ch.epfl.sdp.appart.hilt.annotations.StringDataTransferProvider;
import ch.epfl.sdp.appart.hilt.annotations.UriListDataTransferProvider;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;



@Module
@InstallIn(SingletonComponent.class)
public abstract class GenericInjectModule {

    /*
    @Singleton
    @Binds
    public abstract DataTransfer bindTransferService(DataTransfer transferService);
    */


    @IntegerDataTransferProvider
    @Singleton
    @Provides
    public static GenericTransfer<Integer> provideDataTransferService(@ApplicationContext Context context) {
        return new GenericTransfer<>();
    }

    @StringDataTransferProvider
    @Singleton
    @Provides
    public static GenericTransfer<String> provideOtherDataTransferService(@ApplicationContext Context context) {
        return new GenericTransfer<>();
    }

    @UriListDataTransferProvider
    @Singleton
    @Provides
    public static GenericTransfer<List<Uri>> provideUriList(@ApplicationContext Context context) {
        return new GenericTransfer<>();
    }

}
