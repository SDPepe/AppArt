package ch.epfl.sdp.appart.hilt;

import android.content.Context;

import javax.inject.Singleton;

import ch.epfl.sdp.appart.database.local.LocalDatabase;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import kotlin.jvm.JvmSuppressWildcards;

@Module
@InstallIn(SingletonComponent.class)
public abstract class LocalDatabaseModule {

    @Singleton
    @Binds
    @JvmSuppressWildcards
    public abstract LocalDatabaseService bindLocalDatabaseService(LocalDatabase localDatabase);

    @Singleton
    @Provides
    public static String provideAppFolder(@ApplicationContext Context context) {
        return context.getFilesDir().getPath();
    }

}
