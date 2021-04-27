package ch.epfl.sdp.appart.hilt;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.datapass.DataTransfer;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class TransferModule {

    /*
    @Singleton
    @Binds
    public abstract DataTransfer bindTransferService(DataTransfer transferService);
    */

    @Singleton
    @Provides
    public static DataTransfer provideDataTransferService(@ApplicationContext Context context) {
        return new DataTransfer();
    }
}
